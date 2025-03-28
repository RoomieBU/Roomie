package Controller;

import Database.Dao;
import Database.MatchingPriorityDao;
import Database.SQLConnection;
import Tools.Auth;
import Tools.Utils;

import java.sql.Connection;
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

        try (Connection conn = SQLConnection.getConnection()) {
            MatchingPriorityDao dao = new MatchingPriorityDao(conn);

            // Fetch user data
            List<String> userDataColumns = new ArrayList<>();
            userDataColumns.add("first_name");
            userDataColumns.add("last_name");
            userDataColumns.add("about_me");
            userDataColumns.add("date_of_birth");
            userDataColumns.add("profile_picture_url");
            userDataColumns.add("school");
            Map<String, String> userData = dao.getData(userDataColumns, userEmail, "Users");

            // Fetch user preferences (UPDATED TO MATCH YOUR SCHEMA)
            List<String> userPrefDataColumns = new ArrayList<>();
            userPrefDataColumns.add("preferred_gender");
            userPrefDataColumns.add("pet_friendly");
            userPrefDataColumns.add("introvert");
            userPrefDataColumns.add("extrovert");
            userPrefDataColumns.add("prefer_quiet");
            Map<String, String> userPrefData = dao.getData(userPrefDataColumns, userEmail, "UserPreferences");

            if (userData != null && userPrefData != null) {
                response.put("message", "Profile found");
                response.put("email", userEmail);

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
                response.put("name", (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
                response.put("date_of_birth", dateOfBirth != null ? dateOfBirth : "N/A");
                response.put("about_me", aboutMe != null ? aboutMe : "N/A");
                response.put("school", school != null ? school : "N/A"); // Include school
                response.put("profile_picture_url", profilePictureUrl != null ? profilePictureUrl : "https://roomie.ddns.net/images/defaultProfilePic.jpg");

                // Preference fields
                response.put("preferred_gender", preferredGender != null ? preferredGender : "N/A");
                response.put("pet_friendly", petFriendly != null ? petFriendly : "N/A");
                response.put("introvert", introvert != null ? introvert : "N/A");
                response.put("extrovert", extrovert != null ? extrovert : "N/A");
                response.put("prefer_quiet", preferQuiet != null ? preferQuiet : "N/A");

                return Utils.assembleHTTPResponse(200, Utils.assembleJson(response));
            } else {
                response.put("message", "No profile found");
                code = 404;
            }
        } catch (Exception e) {
            System.err.println("[Controller.ProfileController] Error: " + e.getMessage());
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

        response.put("received_data", data.toString());
        response.put("received_first_name", fName);
        response.put("received_last_name", lName);
        response.put("received_about_me", aboutMe);

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
            System.err.println("[Controller.ProfileController] Error: " + e.getMessage());
            response.put("message", "Server error");
            code = 500;
        }

        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }
}