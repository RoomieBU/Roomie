import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Base64;
import Database.*;

/**
 * Class for controlling what happens from the frontend fileSubmit
 */
public class FileController {
    /**
     * Saves the file locally and puts the URL into Images or User profile
     * @param data base64 image file
     * @param method HTTP method
     * @return HTTP response
     */
    public static String uploadFile(Map<String, String> data, String method) {
        int code = 400;
        Map<String, String> response = new HashMap<>();

        // Ensure the method is POST
        if (!method.equals("POST")) {
            response.put("message", "Method not allowed.");
            return Utils.assembleHTTPResponse(405, Utils.assembleJson(response));
        }

        String token = data.get("token");
        if (!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
        }

        String fileType = data.get("fileType");
        String base64Image = data.get("data");
        String isProfilePicture = data.get("isProfilePicture");

        // Remove the prefix if present (base64 prefix for data URLs)
        if (base64Image != null && base64Image.startsWith("data:image/")) {
            base64Image = base64Image.split(",")[1];
        }

        // Check for missing data
        if (base64Image == null || base64Image.isEmpty()) {
            response.put("message", "No image data has been provided.");
            return Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
        }
        if (fileType == null || fileType.isEmpty()) {
            response.put("message", "No file type provided.");
            return Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
        }

        // Define file extension based on the provided file type
        String fileExtension = switch (fileType) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> {
                response.put("message", "Unsupported image format.");
                yield Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
            }
        };

        try {
            // Decode Base64
            byte[] decodedImage = Base64.getDecoder().decode(base64Image);

            // Check for large image size
            if (decodedImage.length > 10 * 1024 * 1024) {
                response.put("message", "Image too large.");
                return Utils.assembleHTTPResponse(413, Utils.assembleJson(response));
            }

            // Save image locally
            String fileName = UUID.randomUUID() + "." + fileExtension;
            String filePath = "/var/www/images/" + fileName;
            String urlPath = "https://roomie.ddns.net/images/" + fileName;

            // Ensure directory exists
            File directory = new File("/var/www/images");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Write image file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(decodedImage);
            }

            // Get user ID from token
            String userEmail = Auth.getEmailfromToken(token);
            if (userEmail == null) {
                response.put("message", "Invalid token.");
                return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
            }

            UserDao userDao = new UserDao(SQLConnection.getConnection());
            Map<String, String> userData = userDao.getData(Collections.singletonList("user_id"), userEmail);
            String userIdStr = userData.get("user_id");
            userDao.closeConnection();

            if (userIdStr == null) {
                response.put("message", "User not found.");
                return Utils.assembleHTTPResponse(404, Utils.assembleJson(response));
            }

            int userId = Integer.parseInt(userIdStr);

            if (isProfilePicture.equals("True")) {
                // Store profile picture URL in userDao
                Dao dao = new Dao(SQLConnection.getConnection());
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("profile_picture_url", urlPath);
                boolean success = dao.set(dataMap, userEmail, "Users");

                if (!success) {
                    response.put("message", "Failed to update profile picture.");
                    return Utils.assembleHTTPResponse(500, Utils.assembleJson(response));
                }
            } else {
                // Store image path in userImagesDao
                UserImagesDao userImageDao = new UserImagesDao(SQLConnection.getConnection());
                userImageDao.uploadUserImage(userId, urlPath);
                userImageDao.closeConnection();
            }

            response.put("message", "File uploaded successfully.");
            response.put("image_url", urlPath);
            code = 200;

        } catch (IOException e) {
            System.err.println("[FileController] Error saving image: " + e.getMessage());
            e.printStackTrace();
            response.put("message", "Error saving image.");
            code = 500;
        } catch (Exception e) {
            System.err.println("[FileController] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            response.put("message", "Unexpected error.");
            code = 500;
        }

        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }
}
