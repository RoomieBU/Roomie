import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Useful methods that support server operations and produce easier to read code.
 */
public class Utils {

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

    public static Map<String, String> parse(String json) {
        Map<String, String> map = new HashMap<>();

        // Remove braces and whitespace
        json = json.trim().replaceAll("[{}\"]", "");

        // Split into key-value pairs
        String[] pairs = json.split(",");

        for (String pair : pairs) {
            String[] entry = pair.split(":");
            if (entry.length == 2) {
                map.put(entry[0].trim(), entry[1].trim());
            }
        }
        return map;
    }

}
