import Database.SQLConnection;
import Database.UserDao;

import java.sql.SQLException;
import java.util.*;

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
        int code = 400; // Default code (in case of sql error)
        Map<String, String> response = new HashMap<>(); // Use this data structure for easier JSON
        if (!method.equals("POST")) {
            response.put("message", "Method not allowed!");
        }

        String token = data.get("token");
        String userEmail = Auth.getEmailfromToken(token);
        try {
            UserDao DBUser = new UserDao(SQLConnection.getConnection());

            ArrayList<String> columns = new ArrayList<>();
            columns.add("registered");

            if (DBUser.getData(columns, userEmail).get("registered").equals("1")) {
                response.put("message", "User Registered");
                code = 200;
            } else {
                response.put("message", "User not registered");
                code = 400;
            }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Auth Controller] Unable to connect to MySQL.");
        }
        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }

    public static String sendRegistration(Map<String, String> data, String method) {
        int code = 400; // Default code (in case of sql error)
        Map<String, String> response = new HashMap<>(); // Use this data structure for easier JSON
        if (!method.equals("POST")) {
            response.put("message", "Method not allowed!");
        }

        // Get the user from the token value
        String token = data.get("token");
        String email = Auth.getEmailfromToken(token);

        // (FUCTIONALITY MISSING)
        // Functionality for accepting profile pictures needs to happen...
        // profile_picture = data.get("profile_picture");

        Map<String, String> formData = new HashMap<>();
        formData.put("first_name", data.get("first_name"));
        formData.put("last_name", data.get("last_name"));
        formData.put("about_me", data.get("about_name"));
        formData.put("date_of_birth", data.get("date_of_birth"));

        try {
            UserDao DBUser = new UserDao(SQLConnection.getConnection());
            if (DBUser.setData(formData, email)) {
                response.put("message", "Set user data for " + email);
                code = 200;
            } else {
                response.put("message", "Unable to set user data for " + email);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Auth Controller] Unable to connect to MySQL.");
            code = 500;
            response.put("token", "");
        }
        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }
}
