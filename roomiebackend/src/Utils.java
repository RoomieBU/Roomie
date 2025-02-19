import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

/**
 * Useful methods that support server operations and produce easier to read code.
 */
public class Utils {

    private static final String BLOCKED_FILENAME = "blocked_ips.txt";

    /**
     * Creates a string that follows a specific format to be used as an HTTP response.
     * @param code
     * @param r
     * @return
     */
    public static String assembleHTTPResponse(int code, String r) {

        String status = switch (code) {
            case 200 -> "200 OK";
            case 201 -> "201 Created";
            case 401 -> "401 Unauthorized";
            case 403 -> "403 Forbidden";
            case 404 -> "404 Not Found";
            case 500 -> "500 Internal Server Error";
            default -> "400 Bad Request";
        };

        return "HTTP/1.1 " + status + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + r.getBytes().length + "\r\n" +  // Fix here
                "Access-Control-Allow-Origin: *\r\n" +  // Allow frontend to access backend
                "Access-Control-Allow-Methods: POST, GET, OPTIONS\r\n" +
                "Access-Control-Allow-Headers: Content-Type\r\n" +
                "\r\n" +
                r;
    }

    /**
     * Hashed a given string with the SHA256 algorithm.
     * @param input
     * @return
     */
    public static String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static void addBlockedAddress(String ip) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(BLOCKED_FILENAME, true))) {
            out.write(ip);
            out.newLine();
            System.out.println("[Security] Blocked Address (bad request): " + ip);
        } catch (IOException e) {
            System.out.println("[Alert] Failed to write to blocklist file: " + e.getMessage());
        }
    }

    public static HashSet<String> getBlockedAddresses() {
        HashSet<String> ips = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BLOCKED_FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ips.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("[Alert] Error reading blocklist file: " + e.getMessage());
        }
        return ips;
    }
}
