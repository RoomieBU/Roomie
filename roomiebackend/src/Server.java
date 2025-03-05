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
    static public final boolean ALLOW_EMAIL_VERIFICATION = true;
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
            router.addRoute("/auth/login", AuthController::login);
            router.addRoute("/auth/logout", AuthController::logout);
            router.addRoute("/auth/register", AuthController::register);
            router.addRoute("/auth/verify", AuthController::verify);
            router.addRoute("/auth/isregistered", AuthController::isRegistered);
            router.addRoute("/auth/sendRegistration", AuthController::sendRegistration);
            router.addRoute("/upload/fileSubmit", FileController::uploadFile);
            router.addRoute("/auth/sendPreferences", AuthController::sendPreferences);


            // Matches routes??
            router.addRoute("/matches/getPotentialRoommate", MatchController::getNextMatch);
            

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
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                requestLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        // Read in the rest of the request
        StringBuilder body = new StringBuilder();
        if ((method.equals("POST")) && requestLength > 0) {
            char[] buffer = new char[requestLength];
            in.read(buffer, 0, requestLength);
            body.append(buffer);
        }

        Map<String, String> data = Utils.parseJson(body.toString());

        String httpResponse = router.handleRequest(path, data, method);

        out.write(httpResponse.getBytes());
        out.flush();
        client.close();
        connections--;
    }
}
