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
            
            int userID = DBUser.getIDfromEmail(userEmail);

            User user = DBUser.getUserById(userID);

            if (user != null) {
                response.put("message", "Profile found");
                response.put("email", user.getEmail());
                response.put("name", user.getFirstName() + " " + user.getLastName());
                response.put("date_of_birth", user.getDateOfBirth().toString());
                response.put("about_me", user.getAboutMe());
                response.put("major", "Wumbology (Undergrad)");
                code = 200;
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