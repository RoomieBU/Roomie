package Tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Useful methods that support server operations and produce easier to read code.
 */
public class Utils {

    public final static String corsResponse = "HTTP/1.1 204 No Content\r\n" +
            "Access-Control-Allow-Origin: *\r\n" +
            "Access-Control-Allow-Methods: POST, GET, OPTIONS\r\n" +
            "Access-Control-Allow-Headers: Content-Type\r\n" +
            "Content-Length: 0\r\n\r\n";

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

    public static Map<String, String> parseJson(String json) {
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

    public static String assembleJson(Map<String, String> data) {
        StringBuilder json = new StringBuilder("{");

        int count = 0;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\": ");

            if (entry.getValue() == null) {
                json.append("null");
            } else {
                json.append("\"").append(entry.getValue().replace("\"", "\\\"")).append("\"");
            }

            if (++count < data.size()) {
                json.append(", ");
            }
        }
        json.append("}");
        return json.toString();
    }

    public static String generateVerifyCode() {
        Random random = new Random();
        int code = 10000 + random.nextInt(90000); // Ensures a 5-digit number (10000 - 99999)
        return String.valueOf(code);
    }

    public static double getScaledDistance(double x,double y) {
        float dist = Math.abs( (float) x - (float) y );
        return dist / 5.0;
    }

    public static double getScaledDistance(boolean x, boolean y) {
        if (x == y) return 1;
        return 0;
    }
}
