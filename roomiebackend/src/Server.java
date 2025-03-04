import jdk.jshell.execution.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This is the main server class for the backend of Roomie.
 */
public class Server {
    static private final boolean VERBOSE_OUTPUT = true;
    static private final boolean DEV_CONSOLE = true;
    static private final int MAX_CONNECTIONS = 10;
    static public int connections = 0;

    static Router router = new Router();

    /**
     * Main entry point for the server, and is responsible for spawning threads for new
     * connections.
     *
     * @param args
     */
    public static void main(String[] args) {
        int port = 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Notice] Server is running on port " + port);

            // Authentication routes
//            router.addRoute("/auth/login", AuthController::login);
//            router.addRoute("/auth/logout", AuthController::logout);
//            router.addRoute("/auth/register", AuthController::register);
//            router.addRoute("/auth/verify", AuthController::verify);
//            router.addRoute("/auth/isregistered", AuthController::isRegistered);
//            router.addRoute("/auth/sendRegistration", AuthController::sendRegistration);
//            router.addRoute("/upload/fileSubmit", FileController::uploadFile);
            // Matches routes??
//            router.addRoute("/matches/getPotentialRoommate", MatchController::getNextMatch);
//

            // Add authentication Routes with helper method
            // these routes are the default JSON routes :)
            addAuthenticationRoutes();

            // File Submit Route
            router.addRoute("/upload/fileSubmit", (data, method) -> {
                if (data instanceof byte[]) {
                    return FileController.uploadFile((byte[]) data, method);
                } else {
                    return Utils.assembleHTTPResponse(400, "{\"status\":\"error\", \"message\":\"Invalid data type for file upload\"}");
                }
            });

            // Matches Route
            router.addRoute("/matches/getPotentialRoomate", (data, method) -> {
                if (data instanceof Map) {
                    return MatchController.getNextMatch((Map<String, String>) data, method);
                } else {
                    return Utils.assembleHTTPResponse(400, "{\"status\":\"error\", \"message\":\"Invalid data type for matches\"}");
                }
            });

            if (DEV_CONSOLE) {
                System.out.println("[Notice] Development console is active. Type 'help' for commands list");
                new Thread(() -> {
                    Console c = new Console();
                    c.start();
                }).start();
            }

            while (true) {
                if (connections <= MAX_CONNECTIONS) {
                    Socket client = serverSocket.accept();
                    new Thread(() -> {
                        try {
                            handleClient(client);
                            connections++;
                            System.out.println("[Notice] Incoming connection from " + client.getInetAddress());
                        } catch (IOException | SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                } else {
                    System.out.println("[Alert] Max connections exceeded. Waiting 30 seconds...");
                    TimeUnit.SECONDS.sleep(30);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds all the authentication routes to the router
     */
    private static void addAuthenticationRoutes() {
        // array of route paths and controller methods
        String[][] authRoutes = {
                {"/auth/login", "login"},
                {"/auth/logout", "logout"},
                {"/auth/register", "register"},
                {"/auth/verify", "verify"},
                {"/auth/isregistered", "isRegistered"},
                {"/auth/sendRegistration", "sendRegistration"}
        };

        // Loop through each route and add it to the router
        for (String[] route : authRoutes) {
            String path = route[0];
            String method = route[1];

            router.addRoute(path, (data, httpMethod) -> {
                if (data instanceof Map) {
                    try {
                        return (String) AuthController.class.getMethod(method, Map.class, String.class)
                                .invoke(null, data, httpMethod);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Utils.assembleHTTPResponse(500, "{\"status\":\"error\", \"message\":\"Internal Server Error\"}");
                    }
                } else {
                    return Utils.assembleHTTPResponse(400, "{\"status\":\"error\", \"message\":\"Invalid data type for authentication\"}");
                }
            });
        }
    }

    /**
     * Handles specific requests from single clients.
     *
     * @param client Connected client object
     * @throws IOException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void handleClient(Socket client) throws IOException, SQLException, ClassNotFoundException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        OutputStream out = client.getOutputStream();

        // This first section is just parsing the http header
        String request = in.readLine();
        String[] sections = request.split(" ");

        if (sections.length < 2) {
            connections--;
            return;
        }

        String method = sections[0];
        String path = sections[1];

        if (method.equals("OPTIONS")) {
            out.write(Utils.corsResponse.getBytes());
            out.flush();
            client.close();
            connections--;
            return;
        }

        if (path.equals("/favicon.ico")) {
            if (VERBOSE_OUTPUT)
                System.out.println("[Alert] favicon request... terminating" +
                        " connection.");
            connections--;
            return;
        }

        if (VERBOSE_OUTPUT) {
            System.out.println("[Info] " + client.getInetAddress() + " Responded with: " +
                    "\n\tMethod:\t" + method + "\n\tPath:\t" + path);
        }

        int requestLength = 0;
        String contentType = "";
        boolean isBinary = false; // need to check if binary here for handling file upload :)
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                requestLength = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.startsWith("Content-Type:")) {
                contentType = line.split(":")[1].trim();
                isBinary = contentType.startsWith("image/") || contentType.startsWith("application/octet-stream");
            }
        }

        String httpResponse;
        if (isBinary) {
            // read the binary file
            byte[] fileData = new byte[requestLength];
            client.getInputStream().read(fileData);
            // pass binary data to file controller
            httpResponse = FileController.uploadFile(fileData, contentType);

        } else {
            // Default to JSON Handling
            // Read in the rest of the request
            StringBuilder body = new StringBuilder();
            if ((method.equals("POST")) && requestLength > 0) {
                char[] buffer = new char[requestLength];
                in.read(buffer, 0, requestLength);
                body.append(buffer);
            }

            Map<String, String> data = Utils.parseJson(body.toString());
            httpResponse = router.handleRequest(path, data, method);
        }

        out.write(httpResponse.getBytes());
        out.flush();
        client.close();
        connections--;
    }
}
