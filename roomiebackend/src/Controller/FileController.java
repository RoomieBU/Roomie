package Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Base64;
import Database.*;
import Tools.Auth;
import Tools.HTTPResponse;
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
        HTTPResponse response = new HTTPResponse();

        String token = data.get("token");

        String fileType = data.get("fileType");
        String base64Image = data.get("data");
        String isProfilePicture = data.get("isProfilePicture");

        // Remove the prefix if present (base64 prefix for data URLs)
        if (base64Image != null && base64Image.startsWith("data:image/")) {
            base64Image = base64Image.split(",")[1];
        }

        // Check for missing data
        if (base64Image == null || base64Image.isEmpty()) {
            response.setMessage("message", "No image data has been provided.");
            return response.toString();
        }
        if (fileType == null || fileType.isEmpty()) {
            response.setMessage("message", "No file type provided.");
            return response.toString();
        }

        // Define file extension based on the provided file type
        String fileExtension = switch (fileType) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> {
                response.setMessage("message", "Unsupported image format.");
                yield response.toString();
            }
        };

            // Decode Base64
            byte[] decodedImage = Base64.getDecoder().decode(base64Image);

            // Check for large image size
            if (decodedImage.length > 10 * 1024 * 1024) {
                response.setMessage("message", "Image too large.");
                return response.toString();
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
            } catch (IOException e) {
                System.out.println("Error while writing image to file.");
            }

            // Get user ID from token
            String userEmail = Auth.getEmailfromToken(token);
            if (userEmail == null) {
                response.setMessage("message", "Invalid token.");
                return response.toString();
            }

            UserDao userDao = new UserDao(SQLConnection.getConnection());
            Map<String, String> userData = userDao.getData(Collections.singletonList("user_id"), userEmail);
            String userIdStr = userData.get("user_id");

            if (userIdStr == null) {
                response.setMessage("message", "User not found.");
                return response.toString();
            }

            int userId = Integer.parseInt(userIdStr);

            if (isProfilePicture != null && isProfilePicture.equals("True")) {
                // Store profile picture URL in userDao
                Dao dao = new Dao(SQLConnection.getConnection());
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("profile_picture_url", urlPath);
                boolean success = dao.set(dataMap, userEmail, "Users");

                if (!success) {
                    response.setMessage("message", "Failed to update profile picture.");
                    return response.toString();
                }
            } else {
                UserImagesDao userImageDao = new UserImagesDao(SQLConnection.getConnection());
                userImageDao.uploadUserImage(userId, urlPath);
            }

            response.setMessage("message", "File uploaded successfully.");
            response.setMessage("image_url", urlPath);
            response.code = 200;

        return response.toString();
    }

    /**
     * Deletes file from a given post request
     * @param data json payload
     * @param method post request
     * @return http response
     */
    public static String deleteFile(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();

        // Get user email from token
        String token = data.get("token");
        String userEmail = Auth.getEmailfromToken(token);
        if (userEmail == null) {
            response.setMessage("message", "Invalid token.");
            return response.toString();
        }

        // Get user Id
        UserDao userDao = new UserDao(SQLConnection.getConnection());
        Map<String, String> userData = userDao.getData(Collections.singletonList("user_id"), userEmail);
        String userIdStr = userData.get("user_id");
        int userId = Integer.parseInt(userIdStr);


        // get filename and filepath from given url
        String fileURL = data.get("file_url");
        String filePath = "/var/www/images/" + fileURL;

        // delete file
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                response.setMessage("message", "File deleted successfully.");
            } else {
                response.setMessage("message", "File does not exist.");
            }
        }

        // delete from database
        UserImagesDao userImageDao = new UserImagesDao(SQLConnection.getConnection());
        userImageDao.deleteImage(userId, filePath);



        return response.toString();
    }
}
