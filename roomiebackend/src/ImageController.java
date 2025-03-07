import java.sql.Connection;
import java.util.*;
import Database.*;

public class ImageController {
    public static String getUserImages(Map<String, String> data, String method) {
        Map<String, String> response = new HashMap<>();

        // Simple GET check
        if (!method.equals("GET")) {
            return "HTTP/1.1 405 Method Not Allowed\r\n\r\n";
        }

        // Bearer token check
        String authHeader = data.get("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "HTTP/1.1 401 Unauthorized\r\n\r\n";
        }

        try (Connection conn = SQLConnection.getConnection()) {
            String token = authHeader.substring(7);
            String email = Auth.getEmailfromToken(token);

            // Get user ID
            Map<String, String> user = new UserDao(conn).getData(
                    Collections.singletonList("user_id"), email
            );

            // Get images
            List<String> images = new UserImagesDao(conn)
                    .getUserImageUrls(Integer.parseInt(user.get("user_id")));

            response.put("images", String.join(",", images));
            return "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: application/json\r\n"
                    + "Content-Length: " + response.toString().length() + "\r\n\r\n"
                    + response.toString();

        } catch (Exception e) {
            return "HTTP/1.1 500 Internal Server Error\r\n\r\n";
        }
    }
}