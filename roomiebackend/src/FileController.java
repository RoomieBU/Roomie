import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Class for controlling what happens from the frontend fileSubmit
 */
public class FileController {
    private static final String UPLOAD_DIRECTORY = "/var/www/images/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB limit for uploads

    /**
     * Handles the uploading of binary file data
     * @param fileData the raw binary data of the file
     * @param contentType the content type of the file (jpeg, png, ...)
     * @return HTTP response as a String
     */
    public static String uploadFile(byte[] fileData, String contentType) {
        // Debug: Check if the upload directory exists and has correct permissions
        File uploadDir = new File(UPLOAD_DIRECTORY);
        if (!uploadDir.exists()) {
            System.out.println("[Debug] Upload directory does not exist. Attempting to create it...");
            if (!uploadDir.mkdirs()) {
                System.out.println("[Error] Failed to create directory: " + UPLOAD_DIRECTORY);
                return Utils.assembleHTTPResponse(500, "{\"status\":\"error\", \"message\":\"Failed to create directory\"}");
            }
        } else {
            System.out.println("[Debug] Upload directory exists.");
        }

        // Debug: Check if the file size is within the allowed limit
        if (fileData.length > MAX_FILE_SIZE) {
            System.out.println("[Error] File is too large. Max allowed size is " + MAX_FILE_SIZE / (1024 * 1024) + " MB");
            return Utils.assembleHTTPResponse(400, "{\"status\":\"error\", \"message\":\"File size exceeds the limit of 10MB\"}");
        } else {
            System.out.println("[Debug] File size is acceptable: " + fileData.length + " bytes.");
        }

        // Generate unique filename
        String filename = UUID.randomUUID().toString() + getFileExtension(contentType);
        File file = new File(uploadDir, filename);

        // Debug: Output the chosen filename and content type
        System.out.println("[Debug] Generated filename: " + filename);
        System.out.println("[Debug] Content type: " + contentType);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(fileData);
            fos.flush();

            // Debug: File written successfully
            System.out.println("[Debug] File uploaded successfully: " + file.getAbsolutePath());

            // return success
            return Utils.assembleHTTPResponse(200, "{\"status\":\"success\", \"filename\":\"" + filename + "\"}");
        } catch (IOException e) {
            System.out.println("[Error] IOException while uploading file: " + e.getMessage());
            e.printStackTrace();
            return Utils.assembleHTTPResponse(500, "{\"status\":\"error\", \"message\":\"File upload failed\"}");
        }
    }

    /**
     * Returns the file extension based on the content type.
     * @param contentType the content type of the file (e.g., image/jpeg)
     * @return the file extension (e.g., .jpg)
     */
    private static String getFileExtension(String contentType) {
        switch (contentType) {
            case "image/png":
                return ".png";
            case "image/jpeg":
                return ".jpg";
            case "image/webp":
                return ".webp";
            default:
                System.out.println("[Warning] Unknown content type: " + contentType + ". Defaulting to .bin extension.");
                return ".bin";
        }
    }
}
