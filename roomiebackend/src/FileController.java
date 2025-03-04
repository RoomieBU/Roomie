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
     * @param method
     * @return
     */
    public static String uploadFile(Map<String, String> data, String method) {
        System.out.println("Made it to FileController.java");
        System.out.println("Received data: " + data);

        int code = 400;
        Map<String, String> response = new HashMap<>();

        // CORS Handling
        String corsHeader = "Access-Control-Allow-Origin: *\n" +
                "Access-Control-Allow-Methods: POST\n" +
                "Access-Control-Allow-Headers: Content-Type, Authorization\n";

        if (!method.equals("POST")) {
            response.put("message", "Method not allowed.");
            return Utils.assembleHTTPResponse(405, Utils.assembleJson(response)) + corsHeader;
        }

        String token = data.get("token");
        if (!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response)) + corsHeader;
        }

        String base64Image = data.get("image");
        if (base64Image == null || base64Image.isEmpty()) {
            response.put("message", "No image data provided.");
            return Utils.assembleHTTPResponse(400, Utils.assembleJson(response)) + corsHeader;
        }

        try {
            // Decode base64 and remove the prefix if it is still there
            String[] parts = base64Image.split(",");
            if (parts.length < 2) {
                response.put("message", "Invalid image data.");
                return Utils.assembleHTTPResponse(400, Utils.assembleJson(response)) + corsHeader;
            }
            String imageData = parts[1];
            byte[] decodedImage = Base64.getDecoder().decode(imageData);

            // Log decoded image byte length
            System.out.println("Decoded image length: " + decodedImage.length + " bytes");

            // Save image locally
            // Extract "jpeg", "png", etc.
            String fileExtension = parts[0].split("/")[1].split(";")[0];
            if (!fileExtension.matches("jpg|jpeg|png|webp")) {
                response.put("message", "Unsupported image format.");
                return Utils.assembleHTTPResponse(400, Utils.assembleJson(response)) + corsHeader;
            }
            String fileName = UUID.randomUUID() + "." + fileExtension;
            String filePath = "/var/www/images/" + fileName;

            // Ensure directory exists
            File directory = new File("/var/www/images");
            if (!directory.exists()) {
                directory.mkdirs();
                System.out.println("Created images directory: /var/www/images");
            }

            // Write the image bytes to a file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(decodedImage);
                System.out.println("Image saved at: " + filePath);
            }

            // Save the file path into the database
            String user = Auth.getEmailfromToken(token);
            UserDao DBuser = new UserDao(SQLConnection.getConnection());
            List<String> column = new ArrayList<>();
            column.add("user_id");

            // Retrieve the user_id from the database
            Map<String, String> userData = DBuser.getData(column, user);
            String userIdStr = userData.get("user_id");
            if (userIdStr == null) {
                response.put("message", "User not found.");
                return Utils.assembleHTTPResponse(404, Utils.assembleJson(response)) + corsHeader;
            }
            int userId = Integer.parseInt(userIdStr);
            DBuser.closeConnection();

            // Put into Images table
            UserImagesDao userImageDao = new UserImagesDao(SQLConnection.getConnection());
            userImageDao.uploadUserImage(userId, filePath);
            userImageDao.closeConnection();

            response.put("message", "File uploaded successfully.");
            response.put("image_url", "/images/" + fileName);
            code = 200;

        } catch (IOException e) {
            System.out.println("[FileController] Error while saving the image: " + e.getMessage());
            response.put("message", "Error saving image.");
            code = 500;
        } catch (Exception e) {
            System.out.println("[FileController] Unexpected error: " + e.getMessage());
            response.put("message", "Unexpected error");
            code = 500;
        }

        // Combine the CORS header with the response body
        String finalResponse = Utils.assembleHTTPResponse(code, Utils.assembleJson(response)) + corsHeader;
        return finalResponse;
    }
}
