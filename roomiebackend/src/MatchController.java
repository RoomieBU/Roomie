import Database.SQLConnection;
import Database.User;
import Database.UserDao;
import Database.UserMatchInteractionDao;
import java.sql.SQLException;
import java.util.*;


public class MatchController {

    public static String sendMatchInformation(Map<String, String> data, String method) {
        int code = 400;
        Map<String, String> response = new HashMap<>();
        if(!method.equals("POST")) {
            response.put("message", "Method not allowed!");
        }

        String token = data.get("token");
        if(!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
        }

        String email = Auth.getEmailfromToken(data.get("token"));


        Map<String, String> matchData = new HashMap<>();
        matchData.put("user", email);
        matchData.put("shown_user", data.get("shown_user"));
        matchData.put("relationship", data.get("relationship"));


        try {
            UserMatchInteractionDao DBMatch = new UserMatchInteractionDao(SQLConnection.getConnection());

            if(DBMatch.sendMatchInteraction(matchData, email)) {
                response.put("message", "Send Match Interaction data for " + email);
                code = 200;
            } else {
                response.put("message", "Unable to send Match interaction data for " + email);
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Match Controller] Unable to connect to MySQL.");
            code = 500;
            response.put("token", "");
        }


        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }


    public static String getNextMatch(Map<String, String> data, String method) {
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
        String email = Auth.getEmailfromToken(token);


        try {
            UserDao DBUser = new UserDao(SQLConnection.getConnection());

            // Get the current users school
            List<String> columns = new ArrayList<>();
            columns.add("school");
            Map<String, String> userData = new HashMap<>();
            userData = DBUser.getData(columns, email);
            String school = userData.get("school");


            // get a list of all the users, where the user has the same school as the
            // currently logged in user
            List<User> users = DBUser.getAllUsersBySchool(school);
            User user;
            // Just got now get a random user (does no checking if it's the same user)
            Random rand = new Random();
            do {
                user = users.get(rand.nextInt(0, users.size() - 1));
            } while (!user.getRegistered() || user.getDateOfBirth() == null);

            if (users.get(0) != null) {
                response.put("message", "Next match found");
                /**
                response.put("user", Map.of(
                    "email", user.getEmail(),
                    "name", user.getFirstName() + " " + user.getLastName(),
                    "date_of_birth", user.getDateOfBirth().toString(),
                    "about_me", user.getAboutMe(),
                    "major", user.major
                ));**/
                response.put("email", user.getEmail());
                response.put("name", user.getFirstName() + " " + user.getLastName());
                response.put("date_of_birth", user.getDateOfBirth().toString());
                response.put("about_me", user.getAboutMe());
                response.put("major", "Wumbology (Undergrad)");
                code = 200;
            } else {
                response.put("message", "No registered users found");
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