import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import Database.*;

public class ImageController {
    public static String getUserImages(Map<String, String> data, String method) {
        Map<String, String> responseBody = new HashMap<>();
        int statusCode = 200;

        // Validate method
        if (!method.equals("GET")) {
            responseBody.put("message", "Method not allowed");
            return Utils.assembleHTTPResponse(405, Utils.assembleJson(responseBody));
        }

        // Authentication
        String token = data.get("token");
        if (!Auth.isValidToken(token)) {
            responseBody.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(responseBody));
        }

        try (Connection connection = SQLConnection.getConnection()) {
            // Get user ID
            String email = Auth.getEmailfromToken(token);
            UserDao userDao = new UserDao(connection);
            Map<String, String> userData = userDao.getData(
                    Collections.singletonList("user_id"),
                    email
            );

            String userIdStr = userData.get("user_id");
            if (userIdStr == null) {
                responseBody.put("message", "User not found");
                return Utils.assembleHTTPResponse(404, Utils.assembleJson(responseBody));
            }

            // Get images
            int userId = Integer.parseInt(userIdStr);
            UserImagesDao imagesDao = new UserImagesDao(connection);
            List<String> imageUrls = imagesDao.getUserImageUrls(userId);

            // Convert List<String> to comma-separated String
            String imagesString = String.join(",", imageUrls);
            responseBody.put("images", imagesString);
            responseBody.put("status", "success");
        } catch (SQLException e) {
            responseBody.put("message", "Database error");
            statusCode = 500;
        } catch (Exception e) {
            responseBody.put("message", "Server error");
            statusCode = 500;
        }

        return Utils.assembleHTTPResponse(statusCode, Utils.assembleJson(responseBody));
    }
}