import Database.SQLConnection;
import Database.User;
import Database.UserDao;
import java.sql.SQLException;
import java.util.*;


public class ProfileController {

    public static String getProfile(Map<String, String> data, String method) {
        int code = 400;
        Map<String, String> response = new HashMap<>();
        if(!method.equals("POST")) {
            response.put("message", "Method not allowed!");
        }

        String token = data.get("token");
        if (!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
        }
        String userEmail = Auth.getEmailfromToken(token);

        try {
            UserDao DBUser = new UserDao(SQLConnection.getConnection());
                    
            // Fetch user data as a Map
            Map<String, String> userDataQuery = DBUser.getData(
                List.of("first_name", "last_name", "date_of_birth", "about_me"), userEmail
            );

            // Fetch user preferences
            Map<String, String> userPreferences = DBUser.getPreferences(
                List.of("preferred_gender", "pet_friendly", "personality", "quiet_hours"), userEmail
            );
        
            if (userDataQuery != null) {
                response.put("message", "Profile found");
                response.put("email", userEmail); // We already have the email
        
                String firstName = userDataQuery.get("first_name");
                String lastName = userDataQuery.get("last_name");
                String dateOfBirth = userDataQuery.get("date_of_birth");
                String aboutMe = userDataQuery.get("about_me");
                String preferredGender = userPreferences.get("preferred_gender");
                String petFriendly = userPreferences.get("pet_friendly");
                String personality = userPreferences.get("personality");
                String quietHours = userPreferences.get("quiet_hours");


                response.put("name", (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
                response.put("date_of_birth", dateOfBirth != null ? dateOfBirth : "N/A");
                response.put("about_me", aboutMe != null ? aboutMe : "N/A");
                response.put("major", "Wumbology (Undergrad)");
                response.put("preferred_gender", preferredGender != null ? preferredGender : "N/A");
                response.put("pet_friendly", petFriendly != null ? petFriendly : "N/A");
                response.put("personality", personality != null ? personality : "N/A");
                response.put("quiet_hours", quietHours != null ? quietHours : "N/A");
        
                return Utils.assembleHTTPResponse(200, Utils.assembleJson(response)); // Return the response
            } else {
                response.put("message", "No profile found");
                code = 404;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Auth Controller] Unable to connect to MySQL.");
            response.put("message", "Database error");
            code = 500;
        }
    return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }
}