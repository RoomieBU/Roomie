import java.sql.Connection;
import java.util.*;
import Database.*;

public class ImageController {
    public static String getUserImages(Map<String, String> data, String method) {
        int code = 400;
        Map<String, String> response = new HashMap<>();

        // Validate HTTP method
        if (!method.equals("POST")) {
            response.put("message", "Method not allowed!");
            return Utils.assembleHTTPResponse(405, Utils.assembleJson(response)); // 405 Method Not Allowed
        }

        String token = data.get("token");
        // Validate the token
        if (!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response)); // 401 Unauthorized
        }

        // Fetch images
        try (Connection conn = SQLConnection.getConnection()) {
            String email = Auth.getEmailfromToken(token);
            Map<String, String> user = new UserDao(conn).getData(
                    Collections.singletonList("user_id"), email
            );

            if (user == null || user.get("user_id") == null) {
                response.put("message", "User not found.");
                return Utils.assembleHTTPResponse(404, Utils.assembleJson(response)); // 404 Not Found
            }

            int userId = Integer.parseInt(user.get("user_id"));
            List<String> images = new UserImagesDao(conn).getUserImageUrls(userId);

            // If no images are found, return an appropriate response
            if (images.isEmpty()) {
                response.put("message", "No images found.");
            }

            // Build response with image URLs
            response.put("images", String.join(",", images));
            return Utils.assembleHTTPResponse(200, Utils.assembleJson(response)); // 200 OK

        } catch (Exception e) {
            response.put("message", "Server error.");
            return Utils.assembleHTTPResponse(500, Utils.assembleJson(response)); // 500 Internal Server Error
        }
    }
}
