import Controller.*;
import Tools.Console;
import Tools.Router;
import Tools.Utils;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.*;

/**
 * This is the main server class for the backend of Roomie.
 */
public class Server {
    static private final boolean VERBOSE_OUTPUT = true;
    static private final boolean DEV_CONSOLE = true;
    static public final boolean SYNC_OPS = true;
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
        try {
            SSLServerSocketFactory sslServerSocketFactory = getSSLServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

            System.out.println("[Notice] Server is running on port " + port);

            // Authentication routes
            router.addRoute("/auth/login", AuthController::login);
            router.addRoute("/auth/logout", AuthController::logout);
            router.addRoute("/auth/register", AuthController::register);
            router.addRoute("/auth/verify", AuthController::verify);
            router.addRoute("/auth/isregistered", AuthController::isRegistered);
            router.addRoute("/auth/sendRegistration", AuthController::sendRegistration);
            router.addRoute("/auth/sendPreferences", AuthController::sendPreferences);
            router.addRoute("/auth/hasPreferences", AuthController::hasPreferences);

            // supply list routes
            router.addRoute("/checkAndCreateSupplyList", SharedSupplyController::checkAndCreateSupplyList);
            router.addRoute("/getItems", SharedSupplyController::getItems);
            router.addRoute("/editItem", SharedSupplyController::editItem);
            router.addRoute("/addItem", SharedSupplyController::addItem);


            // Image Routes
            router.addRoute("/upload/fileSubmit", FileController::uploadFile);
            router.addRoute("/user/images", ImageController::getUserImages);
            router.addRoute("/user/deleteImage", FileController::deleteFile);

            // Matches routes
            router.addRoute("/matches/getPotentialRoommate", MatchController::getNextMatch);
            router.addRoute("/matches/sendMatchInteraction", MatchController::sendMatchInformation);
            router.addRoute("/matches/getChatInformation", MatchController::sendChatInformation); // <---- HERE

            router.addRoute("/matches/resetMatchInteractions", MatchController::resetMatchInteractions);
            router.addRoute("/matches/getMatchList", MatchController::sendMatchList);
            router.addRoute("/matches/isUserCurrentRoommate", MatchController::isUserCurrentRoommate);

            // Profile Info Route
            router.addRoute("/profile/getProfile", ProfileController::getProfile);
            router.addRoute("/profile/editProfile", ProfileController::editProfile);
            router.addRoute("/profile/getUserEmail", ProfileController::sendUserEmail);

            // Messaging routes
            /**
             * /messages/chathistorys
             *      * Gets all chats when you open a chat / groupchat
             * /messages/sendchat
             *      * Sends a chat to go groupchat / person
             */
            router.addRoute("/chat/sendMessage", ChatController::receiveMessage);
            router.addRoute("/chat/getGroupchats", ChatController::sendGroupChats);
            router.addRoute("/chat/getConfirmedRoommates", ChatController::sendConfirmedRoommates);
            router.addRoute("/chat/getChatHistory", ChatController::sendChatHistory);
            router.addRoute("/chat/requestRoommate", ChatController::receiveRoommateRequest);
            router.addRoute("/chat/getRoommateRequestStatus", ChatController::sendRoommateRequestStatus);
            router.addRoute("/chat/resetRoommateRequestChoice", ChatController::resetRoommateRequestChoice);
            router.addRoute("/chat/createGroupChat", ChatController::createGroupChat);
            router.addRoute("/chat/getAllUserInformation", ChatController::sendAllUserInformation);

            // Alerts
            router.addRoute("/alert/addAlert", AlertController::createAlert);
            router.addRoute("/alert/addAlertReaction", AlertController::addAlertReaction);
            router.addRoute("/alert/getAllAlerts", AlertController::getAllAlerts);
            router.addRoute("/alert/getAllResponses", AlertController::getAllAlertResponses);

            // Rating routes
            router.addRoute("/rating/submit", RatingController::submitRoommateRating);

            if (DEV_CONSOLE) {
                System.out.println("[Notice] Development console is active. Type 'help' for commands list");
                new Thread(() -> {
                    Console c = new Console();
                    c.start();
                }).start();
            }

            if (SYNC_OPS) {
                System.out.println("[Notice] Sync operations are active.");
                new Thread(() -> {
                    try {
                        SyncController sc = new SyncController();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Manages obtaining certs for decrypting
     * @return
     * @throws Exception
     */
    private static SSLServerSocketFactory getSSLServerSocketFactory() throws Exception {
        String keystoreFile = "/home/backdev/keystore.p12";
        String keystorePassword = System.getenv("SSL_KEY");

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream keyFile = new FileInputStream(keystoreFile)) {
            keyStore.load(keyFile, keystorePassword.toCharArray());
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        return sslContext.getServerSocketFactory();
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
        // Read the body in chunks to handle large files
        StringBuilder body = new StringBuilder();
        char[] buffer = new char[4096];  // Read in chunks of 4KB (adjust if needed)
        int bytesRead;
        int totalBytesRead = 0;

        while (totalBytesRead < requestLength && (bytesRead = in.read(buffer)) != -1) {
            body.append(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
        }

        // If body size is still not fully read, you may want to log or handle that case
        if (totalBytesRead < requestLength) {
            System.out.println("[Warning] Body was not completely read, size mismatch.");
        }

        Map<String, String> data = Utils.parseJson(body.toString());

        if (method.equals("POST")) {
            String httpResponse = router.handleRequest(path, data, method);
            out.write(httpResponse.getBytes());
        }
        out.flush();
        client.close();
        connections--;
    }
}
