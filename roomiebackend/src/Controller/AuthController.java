package Controller;

import Database.Dao;
import Database.MatchingPriorityDao;
import Database.SQLConnection;
import Database.UserPreferencesDao;
import Tools.Auth;
import Tools.HTTPResponse;
import Tools.Mail;
import Tools.Utils;

import java.sql.Connection;
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
        HTTPResponse response = new HTTPResponse();

        Map<String, String> query = new HashMap<>();
        query.put("email", data.get("email"));
        query.put("hashed_password", Utils.hashSHA256(data.get("password")));

        Dao dao = new Dao(SQLConnection.getConnection());
        if (dao.exists(query, "Users")) {
            response.code = 200;
            response.setMessage("token", Auth.getToken(data.get("email")));
        } else {
            response.code = 401;
        }
        return response.toString();
    }

    /**
     * Creates a new record in the Users table with just email and password
     *
     * @param data
     * @param method
     * @return
     */
    public static String register(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();

        Map<String, String> query = new HashMap<>();
        query.put("email", data.get("email"));
        query.put("hashed_password", Utils.hashSHA256(data.get("password")));

        Dao dao = new Dao(SQLConnection.getConnection());
        if (dao.insert(query, "Users")) {
            String verifyCode = Utils.generateVerifyCode();
            Map<String, String> verifyCodeFormatted = new HashMap<>();
            verifyCodeFormatted.put("verify_code", verifyCode);

            dao.set(verifyCodeFormatted, data.get("email"), "Users");
            Mail emailer = new Mail();
            emailer.send(data.get("email"), "Roomie Verification Email", "Here is your verification code: " + verifyCode);

            response.setMessage("token", Auth.getToken(data.get("email")));
            response.code = 200;
        } else {
            response.code = 400;
        }

        return response.toString();
    }

    // Invalidate a User Token
    public static String logout(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        String token = data.get("token");
        Auth.invalidateToken(token);
        return response.toString();
    }

    // Check validity of a token
    public static String verify(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        String token = data.get("token");
        if (Auth.isValidToken(token)) {
            response.code = 200;
        } else {
            response.code = 400;
        }
        return response.toString();
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
        HTTPResponse response = new HTTPResponse();

        String token = data.get("token");
        String userEmail = Auth.getEmailfromToken(token);
        Dao dao = new Dao(SQLConnection.getConnection());

        ArrayList<String> columns = new ArrayList<>();
        columns.add("registered");

        if (dao.get(columns, userEmail, "Users").get("registered").equals("1")) {
            response.code = 200;
        } else {
            response.code = 400;
        }

        return response.toString();
    }

    /**
     * Updates the database with additional profile information (name, about me, DOB)
     *
     * @param data
     * @param method
     * @return
     */
    public static String sendRegistration(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();

        String token = data.get("token");
        String email = Auth.getEmailfromToken(token);

        Map<String, String> formData = new HashMap<>();
        formData.put("first_name", data.get("first_name"));
        formData.put("last_name", data.get("last_name"));
        formData.put("about_me", data.get("about_me"));
        formData.put("date_of_birth", data.get("date_of_birth"));
        formData.put("registered", "true");
        formData.put("school", data.get("school"));
        formData.put("major", data.get("major"));
        formData.put("profile_picture_url", data.get("photo"));

        String userEnteredVerifyCode = data.get("code");

        Dao dao = new Dao(SQLConnection.getConnection());
        if (ALLOW_EMAIL_VERIFICATION) {
            Map<String, String> codeReturn = dao.get(List.of("verify_code"), email, "Users");
            String verifyCode = codeReturn.get("verify_code");

            if (!userEnteredVerifyCode.equals(verifyCode)) {
                response.code = 422;
                return response.toString();
            }
        }

        if (dao.set(formData, email, "Users")) {response.code = 200;} else {response.code = 400;}

        return response.toString();
    }

    public static String sendPreferences(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();

        String token = data.get("token");
        String email = Auth.getEmailfromToken(token);
        data.remove("token");

        UserPreferencesDao DBUser = new UserPreferencesDao(SQLConnection.getConnection());
        MatchingPriorityDao MPDao = new MatchingPriorityDao(SQLConnection.getConnection());

        if (DBUser.createUserPreferences(data, email)) {
            MPDao.removeIfExists(email);
            response.code = 200;
        }
        return response.toString();
    }

    public static String hasPreferences(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        String token = data.get("token");
        String email = Auth.getEmailfromToken(token);
        Dao dao = new Dao(SQLConnection.getConnection());
        if (dao.exists(Map.of("email", email), "UserPreferences")) {
            response.code = 200;} else {response.code = 400;}

        return response.toString();
    }

    public static String getStatus(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        String token = data.get("token");
        String email = Auth.getEmailfromToken(token);
        Dao dao = new Dao(SQLConnection.getConnection());
        String status = dao.get(List.of("status"), email, "Users").get("status");
        response.code = 200;
        response.setMessage("status", status);
        return response.toString();
    }
}
