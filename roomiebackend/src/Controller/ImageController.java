package Controller;

import java.sql.Connection;
import java.util.*;
import Database.*;
import Tools.Auth;
import Tools.HTTPResponse;
import Tools.Utils;

public class ImageController {
    public static String getUserImages(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        Connection conn = SQLConnection.getConnection();
        String token = data.get("token");

        String email = Auth.getEmailfromToken(token);
        Map<String, String> user = new UserDao(conn).getData(
                Collections.singletonList("user_id"), email
        );

        if (user == null || user.get("user_id") == null) {
            response.setMessage("message", "User not found.");
            return response.toString();
        }

        int userId = Integer.parseInt(user.get("user_id"));
        List<String> images = new UserImagesDao(conn).getUserImageUrls(userId);

        // If no images are found, return an appropriate response
        if (images.isEmpty()) {
            response.setMessage("message", "No images found.");
            return response.toString();
        }

        // Build response with image URLs
        response.setMessage("images", String.join(",", images));
        response.code = 200;
        return response.toString();
    }
}
