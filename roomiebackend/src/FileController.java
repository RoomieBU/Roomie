import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import Database.*;

/**
 * Class for controlling what happens from the frontend fileSubmit
 */
public class FileController {
    /**
     * Saves the file locally and puts the URL into Images
     * @param data base64 image file
     * @param method HTTP method
     * @return HTTP response
     */
    public static String uploadFile(Map<String, String> data, String method) {
        System.out.println("Made it to FileController.java");
        System.out.println("Received data: " + data);

        int code = 400;
        Map<String, String> response = new HashMap<>();

        if (!method.equals("POST")) {
            response.put("message", "Method not allowed.");
            return Utils.assembleHTTPResponse(405, Utils.assembleJson(response));
        }

        String token = data.get("token");
        if (!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
        }

        String base64Image = data.get("image");
        String fileType = data.get("fileType");  

        if (base64Image == null || base64Image.isEmpty()) {
            response.put("message", "No image data provided.");
            return Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
        }
        if (fileType == null || fileType.isEmpty()) {
            response.put("message", "No file type provided.");
            return Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
        }

        // Extract file extension from MIME type
        String fileExtension = "";
        switch (fileType) {
            case "image/jpeg":
            case "image/jpg":
                fileExtension = "jpg";
                break;
            case "image/png":
                fileExtension = "png";
                break;
            case "image/webp":
                fileExtension = "webp";
                break;
            default:
                response.put("message", "Unsupported image format.");
                return Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
        }

        try {
            // Decode Base64
            String[] parts = base64Image.split(",");
            if (parts.length < 2) {
                response.put("message", "Invalid image data.");
                return Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
            }

            byte[] decodedImage = Base64.getDecoder().decode(parts[1]);
            System.out.println("Decoded image length: " + decodedImage.length + " bytes");

            // Save image locally
            String fileName = UUID.randomUUID() + "." + fileExtension;
            String filePath = "/var/www/images/" + fileName;

            // Ensure directory exists
            File directory = new File("/var/www/images");
            if (!directory.exists()) {
                directory.mkdirs();
                System.out.println("Created images directory: /var/www/images");
            }

            // Write image file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(decodedImage);
                System.out.println("Image saved at: " + filePath);
            }

            // Get user ID from token
            String userEmail = Auth.getEmailfromToken(token);
            if (userEmail == null) {
                response.put("message", "Invalid token.");
                return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
            }

            UserDao userDao = new UserDao(SQLConnection.getConnection());
            Map<String, String> userData = userDao.getData(Collections.singletonList("user_id"), userEmail);
            userDao.closeConnection();

            String userIdStr = userData.get("user_id");
            if (userIdStr == null) {
                response.put("message", "User not found.");
                return Utils.assembleHTTPResponse(404, Utils.assembleJson(response));
            }

            int userId = Integer.parseInt(userIdStr);

            // Store image path in database
            UserImagesDao userImageDao = new UserImagesDao(SQLConnection.getConnection());
            userImageDao.uploadUserImage(userId, filePath);
            userImageDao.closeConnection();

            response.put("message", "File uploaded successfully.");
            response.put("image_url", "/images/" + fileName);
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
