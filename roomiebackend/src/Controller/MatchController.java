package Controller;

import Database.*;
import Tools.Auth;
import Tools.HTTPResponse;
import Tools.Message;
import Tools.Utils;
import java.util.*;
import javax.xml.crypto.Data;
import com.google.gson.Gson;


public class MatchController {

    public static String sendMatchList(Map<String, String> data, String method) {
        String email = Auth.getEmailfromToken(data.get("token"));
        UserDao dao = new UserDao(SQLConnection.getConnection());
        List<String> matchEmails = dao.getMatchEmails(email);

        Gson gson = new Gson();

        List<User> matchInformation = new ArrayList<>();

        for(String e : matchEmails) {
            matchInformation.add(dao.getUserByEmail(e));
        }

        return Utils.assembleHTTPResponse(200, gson.toJson(matchInformation));
    }

    public static String sendChatInformation(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
    
        // Get email from request data
        String email = data.get("email");
        if (email == null || email.isEmpty()) {
            response.setMessage("message", "Email is required.");
            return response.toString();
        }

        UserDao dao = new UserDao(SQLConnection.getConnection());
        ChatInformation chatInfo = dao.getChatInformation(email);
        response.setMessage("first_name", chatInfo.getFirstName());
        response.setMessage("last_name", chatInfo.getLastName());
        response.setMessage("profile_picture_url", chatInfo.getProfilePicture());

        response.code = 200;

        return response.toString();
    }

    public static String sendMatchInformation(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();

        String email = Auth.getEmailfromToken(data.get("token"));
        Map<String, String> matchData = new HashMap<>();
        matchData.put("user", email);
        matchData.put("shown_user", data.get("shown_user"));
        matchData.put("relationship", data.get("relationship"));

        Dao dao = new Dao(SQLConnection.getConnection());
        if(dao.insert(matchData, "UserMatchInteractions")) {
            response.setMessage("message", "Send Match Interaction data for " + email);
            response.code = 200;
        } else {
            response.setMessage("message", "Unable to send Match interaction data for " + email);
        }

        return response.toString();
    }

    public static String resetMatchInteractions(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();

        String email = Auth.getEmailfromToken(data.get("token"));
        UserMatchInteractionDao dao = new UserMatchInteractionDao(SQLConnection.getConnection());
        if (dao.removeAllForUser(email)) {
            response.setMessage("message", "Match Interactions Reset");
            return response.toString();
        }

        return response.toString();
    }


    public static String getNextMatch(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();

        String token = data.get("token");
        String email = Auth.getEmailfromToken(token);

        UserDao DBUser = new UserDao(SQLConnection.getConnection());

        // Get the current users school
        List<String> columns = new ArrayList<>();
        columns.add("school");
        Map<String, String> userData = DBUser.getData(columns, email);
        User user = getNextSimilarUser(email);

        response.setMessage("message", "Next match found");
        response.setMessage("email", user.getEmail());
        response.setMessage("name", user.getFirstName() + " " + user.getLastName());
        response.setMessage("date_of_birth", user.getDateOfBirth().toString());
        response.setMessage("about_me", user.getAboutMe());
        response.setMessage("major", user.getMajor());
        response.setMessage("profile_picture", user.getProfilePicture());
        response.setMessage("school", user.getSchool());
        response.code = 200;

        return response.toString();
    }

    // Finds similarity score between two users
    public static double getSimilarity(String email1, String email2) {
        double similarity = 0;

        UserPreferencesDao upd = new UserPreferencesDao(SQLConnection.getConnection());

        Map<String, Object> perf1 = upd.getUserPreferencesByEmail(email1);
        Map<String, Object> perf2 = upd.getUserPreferencesByEmail(email2);

        double totalValues = 0;
        int totalCount = 0;

        for (String column : perf1.keySet()) {
            Object value1 = perf1.get(column);
            Object value2 = perf2.get(column);

            // Right now only considers ints and booleans (no gender preferences)
            if (value1 instanceof Number && value2 instanceof Number) {
                // Convert to double for numerical values
                if (isBooleanColumn(column)) { // Handle boolean values stored as Integer (TINYINT)
                    boolean bool1 = ((Number) value1).intValue() != 0;
                    boolean bool2 = ((Number) value2).intValue() != 0;
                    totalValues += (bool1 == bool2) ? 1.0 : 0.0;
                } else {
                    double num1 = ((Number) value1).doubleValue();
                    double num2 = ((Number) value2).doubleValue();
                    totalValues += Utils.getScaledDistance(num1, num2);
                }
                totalCount++;
            }
        }

        if (totalCount > 0) {
            similarity = 1 - (totalValues / totalCount);
        }

        return similarity;
    }

    public static boolean calculateAllSimilaritiesForEmail(String email) {
        List<User> userList;
        UserDao dao = new UserDao(SQLConnection.getConnection());
        userList = dao.getAllUsers();
        for (User b : userList) {
            if (email.equals(b.getEmail())) {continue;}
            dao.insert(
                    Map.of("email1", email,
                            "email2", b.getEmail(),
                            "similarity_score", String.valueOf(MatchController.getSimilarity(email, b.getEmail()))),
                    "UserSimilarities");
        }
        return true;
    }

    public static User getNextSimilarUser(String email) {
        User u = null;
        UserDao userDao = new UserDao(SQLConnection.getConnection());
        MatchingPriorityDao MPDao = new MatchingPriorityDao(SQLConnection.getConnection());
        int step = 0;
        String mostSimilarEmail = MPDao.getMostSimilar(email, step++);
        while (MPDao.exists(Map.of("shown_user", mostSimilarEmail, "user", email), "UserMatchInteractions")) {
            mostSimilarEmail = MPDao.getMostSimilar(email, step++);
        }
        u = userDao.getUserByEmail(mostSimilarEmail);

        return u;
    }

    private static boolean isBooleanColumn(String column) {
        return Set.of("pet_friendly", "smoke", "smoke_okay", "drugs", "drugs_okay").contains(column);
    }
}