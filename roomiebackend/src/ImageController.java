import java.sql.Connection;
import java.util.*;
import Database.*;

public class ImageController {
    public static String getUserImages(Map<String, String> data, String method) {
        // Validate method
        if (!method.equals("GET")) {
            return "HTTP/1.1 405 Method Not Allowed\r\n"
                    + "Content-Type: application/json\r\n"
                    + "Content-Length: 0\r\n"
                    + "\r\n";
        }

        // Validate Authorization header
        String authHeader = data.get("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "HTTP/1.1 401 Unauthorized\r\n"
                    + "Content-Type: application/json\r\n"
                    + "Content-Length: 0\r\n"
                    + "\r\n";
        }

        // Extract token
        String token = authHeader.substring(7);

        // Validate token
        if (!Auth.isValidToken(token)) {
            return "HTTP/1.1 401 Unauthorized\r\n"
                    + "Content-Type: application/json\r\n"
                    + "Content-Length: 0\r\n"
                    + "\r\n";
        }

        // Fetch images
        try (Connection conn = SQLConnection.getConnection()) {
            String email = Auth.getEmailfromToken(token);
            Map<String, String> user = new UserDao(conn).getData(
                    Collections.singletonList("user_id"), email
            );

            if (user == null || user.get("user_id") == null) {
                return "HTTP/1.1 404 Not Found\r\n"
                        + "Content-Type: application/json\r\n"
                        + "Content-Length: 0\r\n"
                        + "\r\n";
            }

            int userId = Integer.parseInt(user.get("user_id"));
            List<String> images = new UserImagesDao(conn).getUserImageUrls(userId);

            // Build response
            String responseBody = "{\"images\":\"" + String.join(",", images) + "\"}";
            return "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: application/json\r\n"
                    + "Content-Length: " + responseBody.length() + "\r\n"
                    + "\r\n"
                    + responseBody;

        } catch (Exception e) {
            return "HTTP/1.1 500 Internal Server Error\r\n"
                    + "Content-Type: application/json\r\n"
                    + "Content-Length: 0\r\n"
                    + "\r\n";
        }
    }
}