import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * This is the main server class for the backend of Roomie.
 */
public class Server {
    static private final boolean VERBOSE_OUTPUT = true;
    static private final int MAX_CONNECTIONS = 10;
    static private int connections = 0;

    public static void main(String[] args) throws IOException {
        int port = 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Notice] Server is running on port " + port);

            while (true) {
                if (connections <= MAX_CONNECTIONS) {
                    Socket client = serverSocket.accept();
                    new Thread(() -> {
                        try {
                            handleClient(client);
                            connections++;
                            System.out.println("[Notice] Incoming connection from " + client.getInetAddress());
                        } catch (IOException e) {
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

    public static void handleClient(Socket client) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        OutputStream out = client.getOutputStream();

        // Recieve HTTP request as text
        String request = in.readLine();
        if (request == null) { // Invalid HTTP request
            connections--;
            return;
        }

        String[] sections = request.split(" ");
        if (sections.length < 2) { // Invalid HTTP request
            connections--;
            return;
        }
        String method = sections[0]; // POST or GET
        String path = sections[1];

        if (path.equals("/favicon.ico")) {
            if (VERBOSE_OUTPUT) System.out.println("[Alert] favicon request... terminating" +
                    " connection.");
            connections --;
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

        // For testing only, this should be removed eventually
        if (VERBOSE_OUTPUT) {
            System.out.println("[Info] Form data: " + body);
        }

        String[] attribs = body.toString().split("&");
        String httpResponse = "";

        if (method.equals("POST") && path.equals("/auth/login") && attribs.length == 2) {
            String user = attribs[0].split("=")[1];
            String pass = attribs[1].split("=")[1];

            // Check username & password against database

            httpResponse = Utils.assembleHTTPResponse(200, "{\"message\": \"Login Successful\"}");
        }

        if (method.equals("POST") && path.equals("/auth/logout") && attribs.length == 1) {
            String token = attribs[0].split("=")[1];

            Auth.invalidateToken(token);

            httpResponse = Utils.assembleHTTPResponse(200, "{\"message\": \"Logout Successful\"}");
        }

        if (method.equals("POST") && path.equals("/auth/register")) {
            String user = attribs[0].split("=")[1];
            String pass = attribs[1].split("=")[1];

            // Check if username already exists
            // Add username and password to DB

            httpResponse = Utils.assembleHTTPResponse(201, "{\"message\": \"Successfully Registered\"}");
        }

        if (method.equals("POST") && path.equals("/auth/verify-session")) {
            String token = attribs[0].split("=")[1];
            if (Auth.isValidToken(token)) {
                httpResponse = Utils.assembleHTTPResponse(200, "{\"message\": \"Session Verified\"}");
            } else {
                httpResponse = Utils.assembleHTTPResponse(401, "{\"message\": \"Invalid Session\"}");
            }
        }

        if (method.equals("POST") && path.equals("/auth/reset-password")) {
            String user = attribs[0].split("=")[1];
            String pass = attribs[1].split("=")[1];

            // Check if username is good and overwrite password

            httpResponse = Utils.assembleHTTPResponse(201, "{\"message\": \"Password Reset\"}");
        }

        out.write(httpResponse.getBytes());
        out.flush();
        client.close();
        connections--;
    }
}
