package Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Base64;
import Database.*;
import Tools.Auth;
import Tools.Utils;

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

        // Debug log: Log method entry
        System.out.println("[FileController] uploadFile called");

        // Ensure the method is POST
        if (!method.equals("POST")) {
            response.put("message", "Method not allowed.");
            // Debug log: Log when the method is not POST
            System.out.println("[FileController] Method is not POST. Responding with 405");
            return Utils.assembleHTTPResponse(405, Utils.assembleJson(response));
        }

        String token = data.get("token");
        if (!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            // Debug log: Log invalid token
            System.out.println("[FileController] Invalid token. Responding with 401");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
        }

        String fileType = data.get("fileType");
        String base64Image = data.get("data");
        String isProfilePicture = data.get("isProfilePicture");

        // Debug log: Log received data
        System.out.println("[FileController] fileType: " + fileType);
        System.out.println("[FileController] isProfilePicture: " + isProfilePicture);

        // Remove the prefix if present (base64 prefix for data URLs)
        if (base64Image != null && base64Image.startsWith("data:image/")) {
            base64Image = base64Image.split(",")[1];
        }

        // Check for missing data
        if (base64Image == null || base64Image.isEmpty()) {
            response.put("message", "No image data has been provided.");
            // Debug log: Log missing base64 data
            System.out.println("[FileController] No image data provided. Responding with 400");
            return Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
        }
        if (fileType == null || fileType.isEmpty()) {
            response.put("message", "No file type provided.");
            // Debug log: Log missing fileType
            System.out.println("[FileController] No file type provided. Responding with 400");
            return Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
        }

        // Define file extension based on the provided file type
        String fileExtension = switch (fileType) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> {
                response.put("message", "Unsupported image format.");
                // Debug log: Log unsupported file type
                System.out.println("[FileController] Unsupported image format. Responding with 400");
                yield Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
            }
        };

        try {
            // Decode Base64
            System.out.println("[FileController] Decoding base64 image data...");
            byte[] decodedImage = Base64.getDecoder().decode(base64Image);

            // Check for large image size
            if (decodedImage.length > 10 * 1024 * 1024) {
                response.put("message", "Image too large.");
                // Debug log: Log image size check
                System.out.println("[FileController] Image too large. Responding with 413");
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
                // Debug log: Log directory creation
                System.out.println("[FileController] Created directory /var/www/images");
            }

            // Write image file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(decodedImage);
                // Debug log: Log successful image writing
                System.out.println("[FileController] Image saved successfully: " + filePath);
            }

            // Get user ID from token
            String userEmail = Auth.getEmailfromToken(token);
            if (userEmail == null) {
                response.put("message", "Invalid token.");
                // Debug log: Log invalid token retrieval
                System.out.println("[FileController] User email is null. Responding with 401");
                return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
            }

            UserDao userDao = new UserDao(SQLConnection.getConnection());
            Map<String, String> userData = userDao.getData(Collections.singletonList("user_id"), userEmail);
            String userIdStr = userData.get("user_id");
            userDao.closeConnection();

            if (userIdStr == null) {
                response.put("message", "User not found.");
                // Debug log: Log user not found
                System.out.println("[FileController] User not found. Responding with 404");
                return Utils.assembleHTTPResponse(404, Utils.assembleJson(response));
            }

            int userId = Integer.parseInt(userIdStr);

            // Debug log: Log user ID retrieval
            System.out.println("[FileController] User ID retrieved: " + userId);

            if (isProfilePicture.equals("True")) {
                // Store profile picture URL in userDao
                Dao dao = new Dao(SQLConnection.getConnection());
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("profile_picture_url", urlPath);
                boolean success = dao.set(dataMap, userEmail, "Users");

                if (!success) {
                    response.put("message", "Failed to update profile picture.");
                    // Debug log: Log failed profile picture update
                    System.out.println("[FileController] Failed to update profile picture. Responding with 500");
                    return Utils.assembleHTTPResponse(500, Utils.assembleJson(response));
                }
                // Debug log: Log successful profile picture update
                System.out.println("[FileController] Profile picture updated successfully for user: " + userEmail);
            } else {
                // Store image path in userImagesDao
                UserImagesDao userImageDao = new UserImagesDao(SQLConnection.getConnection());
                userImageDao.uploadUserImage(userId, urlPath);
                userImageDao.closeConnection();
                // Debug log: Log image upload to user images
                System.out.println("[FileController] Image uploaded for user ID: " + userId);
            }

            response.put("message", "File uploaded successfully.");
            response.put("image_url", urlPath);
            code = 200;
            // Debug log: Log successful upload
            System.out.println("[FileController] File uploaded successfully. Image URL: " + urlPath);

        } catch (IOException e) {
            System.err.println("[Controller.FileController] Error saving image: " + e.getMessage());
            e.printStackTrace();
            response.put("message", "Error saving image.");
            code = 500;
        } catch (Exception e) {
            System.err.println("[Controller.FileController] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            response.put("message", "Unexpected error.");
            code = 500;
        }

        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }
}
