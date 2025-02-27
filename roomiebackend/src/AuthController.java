import Database.SQLConnection;
import Database.UserDao;

import java.sql.SQLException;
import java.util.Map;

public class AuthController {
    public static String login(Map<String, String> data, String method) {
        if (!method.equals("POST")) {
            return Utils.assembleHTTPResponse(405, "{\"message\": \"Method Not Allowed\"}");
        }

        String email = data.get("email");
        String pass = data.get("password");

        try {
            UserDao DBUser = new UserDao(SQLConnection.getConnection());
            if (DBUser.isUserLogin(email,pass)) {
                return Utils.assembleHTTPResponse(200, "{\"token\": \"" + Auth.getToken(email) + "\"}");
            } else {
                return Utils.assembleHTTPResponse(400, "{\"token\": \"\"}");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[AuthController] Unable to connect to MySQL.");
            return Utils.assembleHTTPResponse(500, "{\"token\": \"\"}");
        }
    }

    public static String register(Map<String, String> data, String method) {
        if (!method.equals("POST")) {
            return Utils.assembleHTTPResponse(405, "{\"message\": \"Method Not Allowed\"}");
        }

        String email = data.get("email");
        String pass = data.get("password");

        try {
            UserDao DBUser = new UserDao(SQLConnection.getConnection());
            if (DBUser.createUser(email, pass)) {
                return Utils.assembleHTTPResponse(200, "{\"token\": \"" + Auth.getToken(email) + "\"}");
            } else {
                return Utils.assembleHTTPResponse(400, "{\"token\": \"\"}");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Auth Controller] Unable to connect to MySQL.");
            return Utils.assembleHTTPResponse(500, "{\"token\": \"\"}");
        }
    }

    public static String logout(Map<String, String> data, String method) {
        if (!method.equals("POST")) {
            return Utils.assembleHTTPResponse(405, "{\"message\": \"Method Not Allowed\"}");
        }

        String token = data.get("token");
        Auth.invalidateToken(token);

        return Utils.assembleHTTPResponse(200, "{\"message\": \"Logout Successful\"}");
    }

    public static String verify(Map<String, String> data, String method) {
        if (!method.equals("POST")) {
            return Utils.assembleHTTPResponse(405, "{\"message\": \"Method Not Allowed\"}");
        }

        String token = data.get("token");
        if (Auth.isValidToken(token)) {
            return Utils.assembleHTTPResponse(200, "{\"message\": \"Token is valid\"}");
        } else {
            return Utils.assembleHTTPResponse(400, "{\"message\": \"Token is not valid\"}");
        }
    }

    public static String isRegistered(Map<String, String> data, String method) {
        if (!method.equals("POST")) {
            return Utils.assembleHTTPResponse(405, "{\"message\": \"Method Not Allowed\"}");
        }

        String token = data.get("token");

        String userEmail = Auth.getEmailfromToken(token);

        try {
            UserDao DBUser = new UserDao(SQLConnection.getConnection());
            if (DBUser.isRegistered(userEmail)) {
                return Utils.assembleHTTPResponse(200, "{\"message\": \"User registered\"}");
            } else {
                return Utils.assembleHTTPResponse(400, "{\"message\": \"User not registered\"}");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Auth Controller] Unable to connect to MySQL.");
            return Utils.assembleHTTPResponse(500, "{\"token\": \"\"}");
        }
    }

    public static String sendRegistration(Map<String, String> data, String method) {
        if (!method.equals("POST")) {
            return Utils.assembleHTTPResponse(405, "{\"message\": \"Method Not Allowed\"}");
        }

        // Get the user from the token value
        String token = data.get("token");
        String user = Auth.getUserfromToken(token);

        // Get the form data to be assigned to user
        String first_name = data.get("first_name");
        String last_name = data.get("last_name");
        String about_me = data.get("about_me");
        String date_of_birth = data.get("date_of_birth");

        // (FUCTIONALITY MISSING)
        // Functionality for accepting profile pictures needs to happen...
        // profile_picture = data.get("profile_picture");

        try {
            UserDao DBUser = new UserDao(SQLConnection.getConnection());
            if (DBUser.updateUserInfo(user, user, first_name, last_name, about_me, date_of_birth)) {
                return Utils.assembleHTTPResponse(200, "{\"token\": \"" + Auth.getToken(user) + "\"}");
            } else {
                return Utils.assembleHTTPResponse(400, "{\"token\": \"\"}");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Auth Controller] Unable to connect to MySQL.");
            return Utils.assembleHTTPResponse(500, "{\"token\": \"\"}");
        }
    }

    
}
