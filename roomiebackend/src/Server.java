import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * This is the main server class for the backend of Roomie.
 *
 * Connections are kept alive for a minimal amount of time, and the server won't
 * exceed 10 open connections at the same time (for development, anyway).
 *
 * Utils (will) contain methods for more intricate processes, like assembling HTTP responses,
 * logic for authentication, etc.
 *
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
        if (request == null) {// Invalid HTTP request
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


        /**
         * Authentication logic will be store in Auth
         * Parsing form data logic will be stored in Utils
         */
        if (method.equals("POST") && path.equals("/auth/login")) {
            /**
             * Log in logic
             *
             * Parse form data (new method for this)
             *      - Accepts only username and password fields
             * Open database and compare credentials
             *
             * Returns 200 or 401
             */
        }

        if (method.equals("POST") && path.equals("/auth/logout")) {
            /**
             * Get auth token
             * Invalidate token
             *
             * Returns 200, or 400 (if token bad)
             */
        }

        if (method.equals("POST") && path.equals("/auth/register")) {
            /**
             * Get username and password form data
             * Check if username already exists
             * Hash password
             * Insert username and hash into DB
             *
             * Return 201 or 400
             */
        }

        if (method.equals("POST") && path.equals("/auth/verify-session")) {
            /**
             * Get token and check if it's valid
             *
             * Return 200 or 401
             */
        }

        // This is actually really bad
        if (method.equals("POST") && path.equals("/auth/reset-password")) {
            /**
             * Get form username and password data
             * Check if username exists in db
             * Overwrite with new hashed password
             *
             * Return 200 or 400
             */
        }

        String httpResponse = Utils.assembleHTTPResponse(200, "This is where" +
                " you WOULD see JSON data, but unfortunately, you aren't");


        out.write(httpResponse.getBytes());
        out.flush();
        client.close();
        connections--;
    }
}
