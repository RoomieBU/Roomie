import Database.Dao;
import Database.SQLConnection;
import Database.UserDao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class ProfileController {

    public static String getProfile(Map<String, String> data, String method) {
        int code = 400;
        Map<String, String> response = new HashMap<>();

        // Validate HTTP method
        if (!method.equals("POST")) {
            response.put("message", "Method not allowed!");
            return Utils.assembleHTTPResponse(405, Utils.assembleJson(response));
        }

        // Validate token
        String token = data.get("token");
        if (!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
        }

        String userEmail = Auth.getEmailfromToken(token);

        try (Connection conn = SQLConnection.getConnection()) { // Use try-with-resources
            Dao dao = new Dao(conn);

            // Fetch user data
            List<String> userDataColumns = new ArrayList<>();
            userDataColumns.add("first_name");
            userDataColumns.add("last_name");
            userDataColumns.add("about_me");
            userDataColumns.add("date_of_birth");
            userDataColumns.add("profile_picture_url");
            userDataColumns.add("school");
            Map<String, String> userData = dao.getData(userDataColumns, userEmail, "Users");

            // Fetch user preferences
            List<String> userPrefDataColumns = new ArrayList<>();
            userPrefDataColumns.add("preferred_gender");
            userPrefDataColumns.add("pet_friendly");
            userPrefDataColumns.add("personality");
            userPrefDataColumns.add("wakeup_time");
            userPrefDataColumns.add("sleep_time");
            userPrefDataColumns.add("quiet_hours");
            Map<String, String> userPrefData = dao.getData(userPrefDataColumns, userEmail, "UserPreferences");


            if (userData != null && userPrefData != null) {
                response.put("message", "Profile found");
                response.put("email", userEmail);

                // Safely extract values
                String firstName = userData.get("first_name");
                String lastName = userData.get("last_name");
                String dateOfBirth = userData.get("date_of_birth");
                String aboutMe = userData.get("about_me");
                String profilePictureUrl = userData.get("profile_picture_url");

                // Safely handle preferences (may be null)
                String preferredGender = userPrefData != null ? userPrefData.get("preferred_gender") : null;
                String petFriendly = userPrefData != null ? userPrefData.get("pet_friendly") : null;
                String personality = userPrefData != null ? userPrefData.get("personality") : null;
                String quietHours = userPrefData != null ? userPrefData.get("quiet_hours") : null;

                // Populate response
                response.put("name", (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
                response.put("date_of_birth", dateOfBirth != null ? dateOfBirth : "N/A");
                response.put("about_me", aboutMe != null ? aboutMe : "N/A");
                response.put("major", "Wumbology (Undergrad)");
                response.put("preferred_gender", preferredGender != null ? preferredGender : "N/A");
                response.put("pet_friendly", petFriendly != null ? petFriendly : "N/A");
                response.put("personality", personality != null ? personality : "N/A");
                response.put("quiet_hours", quietHours != null ? quietHours : "N/A");
                response.put("profile_picture_url", profilePictureUrl != null ? profilePictureUrl : "https://roomie.ddns.net/images/defaultProfilePic.jpg");

                return Utils.assembleHTTPResponse(200, Utils.assembleJson(response));
            } else {
                response.put("message", "No profile found");
                code = 404;
            }
        } catch (Exception e) { // Catch all exceptions
            System.err.println("[ProfileController] Error: " + e.getMessage());
            response.put("message", "Server error");
            code = 500;
        }

        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }

    public static String editProfile(Map<String, String> data, String method) {
        int code = 400;
        Map<String, String> response = new HashMap<>();

        // Validate HTTP method
        if (!method.equals("POST")) {
            response.put("message", "Method not allowed!");
            return Utils.assembleHTTPResponse(405, Utils.assembleJson(response));
        }

        // Validate token
        String token = data.get("token");
        if (!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
        }

        String userEmail = Auth.getEmailfromToken(token);
        String fName = data.get("first_name");
        String lName = data.get("last_name");
        String aboutMe = data.get("about_me");

        // Validate required fields
        if (fName == null || lName == null || aboutMe == null) {
            response.put("message", "Missing required fields (first_name, last_name, about_me).");
            return Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
        }

        // Map of fields to update
        Map<String, String> updateData = new HashMap<>();
        updateData.put("first_name", fName);
        updateData.put("last_name", lName);
        updateData.put("about_me", aboutMe);

        try (Connection conn = SQLConnection.getConnection()) { // Use try-with-resources
            Dao dao = new Dao(conn);
            boolean isUpdated = dao.set(updateData, userEmail, "Users");

            if (isUpdated) {
                response.put("message", "Profile updated successfully!");
                return Utils.assembleHTTPResponse(200, Utils.assembleJson(response));
            } else {
                response.put("message", "Failed to update profile");
                code = 500;
            }
        } catch (Exception e) { // Catch all exceptions
            System.err.println("[ProfileController] Error: " + e.getMessage());
            response.put("message", "Server error");
            code = 500;
        }

        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }
}