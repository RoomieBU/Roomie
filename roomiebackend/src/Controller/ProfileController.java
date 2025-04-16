package Controller;

import Database.Dao;
import Database.MatchingPriorityDao;
import Database.SQLConnection;
import Tools.Auth;
import Tools.HTTPResponse;
import java.sql.Connection;
import java.util.*;

public class ProfileController {

    public static String sendUserEmail(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        String token = data.get("token");
        String userEmail = Auth.getEmailfromToken(token);

        response.setMessage("email", userEmail);
        response.code = 200;
        return response.toString();
    }

    public static String getProfile(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        String token = data.get("token");
        String userEmail = Auth.getEmailfromToken(token);

        Connection conn = SQLConnection.getConnection();
        MatchingPriorityDao dao = new MatchingPriorityDao(conn);

        // Fetch user data
        List<String> userDataColumns = new ArrayList<>();
        userDataColumns.add("first_name");
        userDataColumns.add("last_name");
        userDataColumns.add("about_me");
        userDataColumns.add("date_of_birth");
        userDataColumns.add("profile_picture_url");
        userDataColumns.add("school");
        Map<String, String> userData = dao.get(userDataColumns, userEmail, "Users");

        // Fetch user preferences (UPDATED TO MATCH YOUR SCHEMA)
        List<String> userPrefDataColumns = new ArrayList<>();
        userPrefDataColumns.add("preferred_gender");
        userPrefDataColumns.add("pet_friendly");
        userPrefDataColumns.add("introvert");
        userPrefDataColumns.add("extrovert");
        userPrefDataColumns.add("prefer_quiet");
        Map<String, String> userPrefData = dao.get(userPrefDataColumns, userEmail, "UserPreferences");

        if (userData != null && userPrefData != null) {
            response.setMessage("message", "Profile found");
            response.setMessage("email", userEmail);

            // Extract user data
            String firstName = userData.get("first_name");
            String lastName = userData.get("last_name");
            String dateOfBirth = userData.get("date_of_birth");
            String aboutMe = userData.get("about_me");
            String profilePictureUrl = userData.get("profile_picture_url");
            String school = userData.get("school"); // Added school

            // Extract preferences
            String preferredGender = userPrefData.get("preferred_gender");
            String petFriendly = userPrefData.get("pet_friendly");
            String introvert = userPrefData.get("introvert");
            String extrovert = userPrefData.get("extrovert");
            String preferQuiet = userPrefData.get("prefer_quiet");

            // Build response
            response.setMessage("name", (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
            response.setMessage("date_of_birth", dateOfBirth != null ? dateOfBirth : "N/A");
            response.setMessage("about_me", aboutMe != null ? aboutMe : "N/A");
            response.setMessage("school", school != null ? school : "N/A"); // Include school
            response.setMessage("profile_picture_url", profilePictureUrl != null ? profilePictureUrl : "https://roomie.ddns.net/images/defaultProfilePic.jpg");

            // Preference fields
            response.setMessage("preferred_gender", preferredGender != null ? preferredGender : "N/A");
            response.setMessage("pet_friendly", petFriendly != null ? petFriendly : "N/A");
            response.setMessage("introvert", introvert != null ? introvert : "N/A");
            response.setMessage("extrovert", extrovert != null ? extrovert : "N/A");
            response.setMessage("prefer_quiet", preferQuiet != null ? preferQuiet : "N/A");

            response.code = 200;
        } else {
            response.setMessage("message", "No profile found");
            response.code = 404;
        }

        return response.toString();
    }

    public static String editProfile(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        String token = data.get("token");

        String userEmail = Auth.getEmailfromToken(token);
        String fName = data.get("first_name");
        String lName = data.get("last_name");
        String aboutMe = data.get("about_me");

        response.setMessage("received_data", data.toString());
        response.setMessage("received_first_name", fName);
        response.setMessage("received_last_name", lName);
        response.setMessage("received_about_me", aboutMe);

        // Validate required fields
        if (fName == null || lName == null || aboutMe == null) {
            response.setMessage("message", "Missing required fields (first_name, last_name, about_me).");
            response.code = 400;
            return response.toString();
        }

        // Map of fields to update
        Map<String, String> updateData = new HashMap<>();
        updateData.put("first_name", fName);
        updateData.put("last_name", lName);
        updateData.put("about_me", aboutMe);

        Connection conn = SQLConnection.getConnection();
        Dao dao = new Dao(conn);
        boolean isUpdated = dao.set(updateData, userEmail, "Users");

        if (isUpdated) {
            response.setMessage("message", "Profile updated successfully!");
            response.code = 200;
            return response.toString();
        } else {
            response.setMessage("message", "Failed to update profile");
            response.code = 400;
        }

        return response.toString();
    }

    public static String getUserNameByEmail(String email) {
        Connection conn = SQLConnection.getConnection();
        Dao dao = new Dao(conn);

        // Fetch user data
        List<String> userDataColumns = new ArrayList<>();
        userDataColumns.add("first_name");
        userDataColumns.add("last_name");
        Map<String, String> userData = dao.get(userDataColumns, email, "Users");

        if (userData != null) {
            String firstName = userData.get("first_name");
            String lastName = userData.get("last_name");
            return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "").trim();
        } else {
            return ""; // Return an empty string if user not found
        }
    }
}