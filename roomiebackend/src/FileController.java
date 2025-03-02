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
     * Saves the file locally and  puts the url into Images
     * @param data base64 image file
     * @param method
     * @return
     */
    public static String uploadFile(Map<String, String> data, String method) {
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
        if (base64Image == null || base64Image.isEmpty()) {
            response.put("message", "No image data provided.");
            return Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
        }

        try {
            // Decode base64 and remove the prefix if it is still there
            String[] parts = base64Image.split(",");
            if (parts.length < 2) {
                response.put("message", "Invalid image data.");
                return Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
            }
            String imageData = parts[1];
            byte[] decodedImage = Base64.getDecoder().decode(imageData);


            // Save image locally
            // Extract "jpeg", "png", etc.
            String fileExtension = parts[0].split("/")[1].split(";")[0];
            if (!fileExtension.matches("jpg|jpeg|png|webp")) {
                response.put("message", "Unsupported image format.");
                return Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
            }
            String fileName = UUID.randomUUID() + "." + fileExtension;

            String filePath = "/var/www/images/" + fileName;
            // Ensure directory exists
            File directory = new File("/var/www/images");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            // write the image bytes to a file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(decodedImage);
            }

            // Save the filepath into database
            // get user_id first
            String user = Auth.getEmailfromToken(token);
            UserDao DBuser = new UserDao(SQLConnection.getConnection());
            List<String> column = new ArrayList<>();
            column.add("user_id");
            // Retrieve the user_id from the database
            Map<String, String> userData = DBuser.getData(column, user);
            String userIdStr = userData.get("user_id");
            if (userIdStr == null) {
                response.put("message", "User not found.");
                return Utils.assembleHTTPResponse(404, Utils.assembleJson(response));
            }
            int userId = Integer.parseInt(userIdStr);
            DBuser.closeConnection();
            // put into Images table
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
            System.out.println("[File Controller ] Unexpected Error.");
            response.put("message", "Unexpected error");
            code = 500;
        }
        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }
}
