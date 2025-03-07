import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import Database.*;

public class ImageController {
    private static String getReasonPhrase(int statusCode) {
        switch (statusCode) {
            case 200: return "OK";
            case 204: return "No Content";
            case 400: return "Bad Request";
            case 401: return "Unauthorized";
            case 404: return "Not Found";
            case 405: return "Method Not Allowed";
            case 500: return "Internal Server Error";
            default: return "";
        }
    }

    public static String getUserImages(Map<String, String> data, String method) {
        Map<String, String> responseBody = new HashMap<>();
        int statusCode = 200;

        // Handle preflight OPTIONS request
        if (method.equals("OPTIONS")) {
            return "HTTP/1.1 204 No Content\r\n"
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
            statusCode = 405;
            return formatResponse(statusCode, responseBody);
        }

        // Authentication
        String authHeader = data.get("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            responseBody.put("message", "Unauthorized");
            statusCode = 401;
            return formatResponse(statusCode, responseBody);
        }
        String token = authHeader.substring(7);

        if (!Auth.isValidToken(token)) {
            responseBody.put("message", "Unauthorized");
            statusCode = 401;
            return formatResponse(statusCode, responseBody);
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
                statusCode = 404;
                return formatResponse(statusCode, responseBody);
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

        return formatResponse(statusCode, responseBody);
    }

    private static String formatResponse(int statusCode, Map<String, String> responseBody) {
        String reasonPhrase = getReasonPhrase(statusCode);
        String jsonBody = Utils.assembleJson(responseBody);

        return "HTTP/1.1 " + statusCode + " " + reasonPhrase + "\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Length: " + jsonBody.length() + "\r\n"
                + "\r\n"
                + jsonBody;
    }
}