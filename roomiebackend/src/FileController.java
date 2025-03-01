import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import Database.*;

/**
 * Class for controlling what happens from the frontend FileUpload
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
            String imageData = base64Image.split(",")[1];
            byte[] decodedImage = Base64.getDecoder().decode(imageData);

            // Save image locally
            String fileName = UUID.randomUUID().toString() + ".jpg";
            String filePath = "/var/www/images/" + fileName;
            // Ensure directory exists
            File directory = new File("/var/www/images" + fileName);
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
            int userId = DBuser.getData(column, user).size();
            DBuser.closeConnection();
            // put into Images table
            UserImagesDao userImageDao = new UserImagesDao(SQLConnection.getConnection());
            userImageDao.uploadUserImage(userId, filePath);
            userImageDao.closeConnection();


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
