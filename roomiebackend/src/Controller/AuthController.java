package Controller;

import Database.Dao;
import Database.MatchingPriorityDao;
import Database.SQLConnection;
import Database.UserPreferencesDao;
import Tools.Auth;
import Tools.Mail;
import Tools.Utils;

import java.sql.SQLException;
import java.util.*;

public class AuthController {
    private static final boolean ALLOW_EMAIL_VERIFICATION = false;
    /**
     * Logic for logging in.
     *
     * @param data
     * @param method
     * @return
     */
    public static String login(Map<String, String> data, String method) {
        if (!method.equals("POST")) {
            return Utils.assembleHTTPResponse(405, "{\"message\": \"Method Not Allowed\"}");
        }
        Map<String, String> query = new HashMap<>();
        query.put("email", data.get("email"));
        query.put("hashed_password", Utils.hashSHA256(data.get("password")));

        try {
            Dao dao = new Dao(SQLConnection.getConnection());
            if (dao.exists(query, "Users")) {
                return Utils.assembleHTTPResponse(200, "{\"token\": \"" + Auth.getToken(data.get("email")) + "\"}");
            } else {
                return Utils.assembleHTTPResponse(400, "{\"token\": \"\"}");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Controller.AuthController] Unable to connect to MySQL.");
            return Utils.assembleHTTPResponse(500, "{\"token\": \"\"}");
        }
    }

    /**
     * Creates a new record in the Users table with just email and password
     *
     * @param data
     * @param method
     * @return
     */
    public static String register(Map<String, String> data, String method) {
        int code = 400;
        Map<String, String> response = new HashMap<>();
        if (!method.equals("POST")) {
            response.put("message", "Method not allowed!");
        }

        Map<String, String> query = new HashMap<>();
        query.put("email", data.get("email"));
        query.put("hashed_password", Utils.hashSHA256(data.get("password")));

        try {
            Dao dao = new Dao(SQLConnection.getConnection());
            if (dao.insert(query, "Users")) {
                String verifyCode = Utils.generateVerifyCode();
                Map<String, String> verifyCodeFormatted = new HashMap<>();
                verifyCodeFormatted.put("verify_code", verifyCode);

                dao.set(verifyCodeFormatted, data.get("email"), "Users");
                Mail emailer = new Mail();
                emailer.send(data.get("email"), "Roomie Verification Email", "Here is your verification code: " + verifyCode);

                response.put("token", Auth.getToken(data.get("email")));
                code = 200;
            } else {
                response.put("token", "");
                code = 400;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Tools.Auth Controller] Unable to connect to MySQL.");
            code = 500;
            response.put("message", "Unexpected error");
        }
        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }

    /**
     * Invalidates a user token.
     *
     * @param data
     * @param method
     * @return
     */
    public static String logout(Map<String, String> data, String method) {
        if (!method.equals("POST")) {
            return Utils.assembleHTTPResponse(405, "{\"message\": \"Method Not Allowed\"}");
        }

        String token = data.get("token");
        Auth.invalidateToken(token);

        return Utils.assembleHTTPResponse(200, "{\"message\": \"Logout Successful\"}");
    }

    /**
     * Verifies that a user token is valid.
     *
     * @param data
     * @param method
     * @return
     */
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

    /**
     * Since the registration / additional user info page requires the user to be logged
     * in, the token needs to be checked before it's used
     *
     * If the user sends a null or invalid token, code 401 unauthorized will be used.
     *      * Null tokens will cause the thread to crash
     *
     * @param data
     * @param method
     * @return
     */
    public static String isRegistered(Map<String, String> data, String method) {
        int code = 400; // Default code (in case of sql error)
        Map<String, String> response = new HashMap<>(); // Use this data structure for easier JSON
        if (!method.equals("POST")) {
            response.put("message", "Method not allowed!");
        }

        String token = data.get("token");

        // Valid token check moment
        if (!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
        }

        String userEmail = Auth.getEmailfromToken(token);
        List<String> query = List.of("registered");
        try {
            Dao dao = new Dao(SQLConnection.getConnection());

            ArrayList<String> columns = new ArrayList<>();
            columns.add("registered");

            if (dao.get(columns, userEmail, "Users").get("registered").equals("1")) {
                response.put("message", "User Registered");
                code = 200;
            } else {
                response.put("message", "User not registered");
                code = 400;
            }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Tools.Auth Controller] Unable to connect to MySQL.");
        }

        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }

    /**
     * Updates the database with additional profile information (name, about me, DOB)
     *
     * @param data
     * @param method
     * @return
     */
    public static String sendRegistration(Map<String, String> data, String method) {
        int code = 400; // Default code (in case of sql error)
        Map<String, String> response = new HashMap<>(); // Use this data structure for easier JSON
        if (!method.equals("POST")) {
            response.put("message", "Method not allowed!");
        }

        // Get the user from the token value
        String token = data.get("token");

        if (!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
        }

        String email = Auth.getEmailfromToken(token);

        // This is maybe where profile picture happens too?

        Map<String, String> formData = new HashMap<>();
        formData.put("first_name", data.get("first_name"));
        formData.put("last_name", data.get("last_name"));
        formData.put("about_me", data.get("about_me"));
        formData.put("date_of_birth", data.get("date_of_birth"));
        formData.put("registered", "true");
        formData.put("school", data.get("school"));

        String userEnteredVerifyCode = data.get("code");

        try {
            Dao dao = new Dao(SQLConnection.getConnection());
            if (ALLOW_EMAIL_VERIFICATION) {
                Map<String, String> codeReturn = dao.get(List.of("verify_code"), email, "Users");
                String verifyCode = codeReturn.get("verify_code");

                if (!userEnteredVerifyCode.equals(verifyCode)) {
                    response.put("message", "Verification code incorrect. Please check your email.");
                    code = 422;
                    return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
                }
            }

            if (dao.set(formData, email, "Users")) {
                response.put("message", "Set user data for " + email);
                code = 200;
            } else {
                response.put("message", "Unable to set user data for " + email);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Tools.Auth Controller] Unable to connect to MySQL.");
            code = 500;
            response.put("token", "");
        }
        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }

    public static String sendPreferences(Map<String, String> data, String method) {
        int code = 400; // Default code (in case of sql error)
        Map<String, String> response = new HashMap<>(); // Use this data structure for easier JSON
        if (!method.equals("POST")) {
            response.put("message", "Method not allowed!");
        }

        // Get the user from the token value
        String token = data.get("token");

        if (!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
        }

        String email = Auth.getEmailfromToken(token);

        data.remove("token");

        try {
            UserPreferencesDao DBUser = new UserPreferencesDao(SQLConnection.getConnection());
            MatchingPriorityDao MPDao = new MatchingPriorityDao(SQLConnection.getConnection());

            if (DBUser.createUserPreferences(data, email)) {
                response.put("message", "Set user data for " + email);
                MPDao.removeIfExists(email);
                code = 200;
            } else {
                response.put("message", "Unable to set user data for " + email);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Tools.Auth Controller] Unable to connect to MySQL.");
            code = 500;
            response.put("token", "");
        }
        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }

    public static String hasPreferences(Map<String, String> data, String method) {
        int code = 400; // Default code (in case of sql error)
        Map<String, String> response = new HashMap<>(); // Use this data structure for easier JSON
        if (!method.equals("POST")) {
            response.put("message", "Method not allowed!");
        }

        // Get the user from the token value
        String token = data.get("token");

        if (!Auth.isValidToken(token)) {
            response.put("message", "Unauthorized");
            return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
        }
        String email = Auth.getEmailfromToken(token);

        try {
            Dao dao = new Dao(SQLConnection.getConnection());
            if (dao.exists(Map.of("email", email), "UserPreferences")) {
                response.put("message", "User preferences exist.");
                return Utils.assembleHTTPResponse(200, Utils.assembleJson(response));
            }
        } catch (SQLException | ClassNotFoundException e) {
            return Utils.assembleHTTPResponse(400, Utils.assembleJson(response));
        }

        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }
}
