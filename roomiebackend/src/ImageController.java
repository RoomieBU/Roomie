import java.sql.Connection;
import java.util.*;
import Database.*;

public class ImageController {
    public static String getUserImages(Map<String, String> data, String method) {
        Map<String, String> response = new HashMap<>();

        // Validate method
        if (!method.equals("GET")) {
            response.put("message", "Method not allowed.");
            return Utils.assembleHTTPResponse(405, Utils.assembleJson(response));
        }

        // Validate Authorization header
        String authHeader = data.get("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
        }

        // Extract token
        String token = authHeader.substring(7);

        // Validate token
        if (!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
        }

        // Fetch images
        try (Connection conn = SQLConnection.getConnection()) {
            String email = Auth.getEmailfromToken(token);
            Map<String, String> user = new UserDao(conn).getData(
                    Collections.singletonList("user_id"), email
            );

            if (user == null || user.get("user_id") == null) {
                response.put("message", "User not found.");
                return Utils.assembleHTTPResponse(404, Utils.assembleJson(response));
            }

            int userId = Integer.parseInt(user.get("user_id"));
            List<String> images = new UserImagesDao(conn).getUserImageUrls(userId);

            // Build response
            response.put("images", String.join(",", images));
            return Utils.assembleHTTPResponse(200, Utils.assembleJson(response));

        } catch (Exception e) {
            response.put("message", "Server error.");
            return Utils.assembleHTTPResponse(500, Utils.assembleJson(response));
        }
    }
}