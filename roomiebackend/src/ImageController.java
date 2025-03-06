import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import Database.*;

public class ImageController {
    public static String getUserImages(Map<String, String> data, String method) {
        Map<String, String> responseBody = new HashMap<>();
        int statusCode = 200;

        // Handle preflight OPTIONS request
        if (method.equals("OPTIONS")) {
            return "HTTP/1.1 200 OK\r\n"
                    + "Access-Control-Allow-Origin: *\r\n"
                    + "Access-Control-Allow-Methods: GET, OPTIONS\r\n"
                    + "Access-Control-Allow-Headers: Authorization, Content-Type\r\n"
                    + "Access-Control-Max-Age: 3600\r\n"
                    + "Content-Length: 0\r\n"
                    + "\r\n";
        }

        // Validate method
        if (!method.equals("GET")) {
            responseBody.put("message", "Method not allowed");
            return "HTTP/1.1 405 Method Not Allowed\r\n"
                    + "Content-Type: application/json\r\n"
                    + "Content-Length: " + Utils.assembleJson(responseBody).length() + "\r\n"
                    + "\r\n"
                    + Utils.assembleJson(responseBody);
        }

        // Authentication
        String token = data.get("token");
        if (!Auth.isValidToken(token)) {
            responseBody.put("message", "Unauthorized");
            return "HTTP/1.1 401 Unauthorized\r\n"
                    + "Content-Type: application/json\r\n"
                    + "Content-Length: " + Utils.assembleJson(responseBody).length() + "\r\n"
                    + "\r\n"
                    + Utils.assembleJson(responseBody);
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
                return "HTTP/1.1 404 Not Found\r\n"
                        + "Content-Type: application/json\r\n"
                        + "Content-Length: " + Utils.assembleJson(responseBody).length() + "\r\n"
                        + "\r\n"
                        + Utils.assembleJson(responseBody);
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

        return "HTTP/1.1 " + statusCode + " OK\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Length: " + Utils.assembleJson(responseBody).length() + "\r\n"
                + "\r\n"
                + Utils.assembleJson(responseBody);
    }
}