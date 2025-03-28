package Controller;

import Database.*;
import Tools.Auth;
import Tools.Utils;
import java.sql.SQLException;
import java.util.*;


public class MatchController {

    public static String sendProfilePicture(Map<String, String> data, String method) {
        int code = 400;
        Map<String, String> response = new HashMap<>();
        if(!method.equals("POST")) {
            response.put("message", "Method not allowed!");
        }

        String email = data.get("email");
        List<String> columns = new ArrayList<>();
        columns.add("profile_picture_url");

        try {
            Dao dao = new Dao(SQLConnection.getConnection());
            response = dao.getData(columns, email, "Users");

            if(response != null) {
                response.put("message", "Send profile url");
                code = 200;
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Match Controller] Unable to connect to MySQL.");
            code = 500;
            response.put("email", "");
        }

        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }

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
            Dao dao = new Dao(SQLConnection.getConnection());
            if(dao.insert(matchData, "UserMatchInteractions")) {
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

    public static String resetMatchInteractions(Map<String, String> data, String method) {
        int code = 400;
        Map<String, String> response = new HashMap<>();
        if(!method.equals("POST")) {
            response.put("message", "Method not allowed!");
        }

        String email = Auth.getEmailfromToken(data.get("token"));
        try {
            UserMatchInteractionDao dao = new UserMatchInteractionDao(SQLConnection.getConnection());
            if (dao.removeAllForUser(email)) {
                response.put("message", "Match Interactions Reset");
                return Utils.assembleHTTPResponse(200, Utils.assembleJson(response));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Match Controller] Unable to connect to MySQL");
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
            // This must be DBUser to get list of users
            // ** Don't change this to dao class
            UserDao DBUser = new UserDao(SQLConnection.getConnection());

            // Get the current users school
            List<String> columns = new ArrayList<>();
            columns.add("school");
            Map<String, String> userData = new HashMap<>();
            userData = DBUser.getData(columns, email);
            String school = userData.get("school");

/**         //For debugging: This block just sends a random user
 *
            // get a list of all the users, where the user has the same school as the
            List<User> users = DBUser.getAllUsersBySchool(school);
            User user;
            Random rand = new Random();
            do {
                user = users.get(rand.nextInt(0, users.size() - 1));
            } while (!user.getRegistered() || user.getDateOfBirth() == null || user.getEmail().equals(email));
**/

            User user = getNextSimilarUser(email);

            response.put("message", "Next match found");
            response.put("email", user.getEmail());
            response.put("name", user.getFirstName() + " " + user.getLastName());
            response.put("date_of_birth", user.getDateOfBirth().toString());
            response.put("about_me", user.getAboutMe());
            response.put("major", "Ski ball (Undergrad)");
            response.put("profile_picture", user.getProfilePicture());
            code = 200;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Tools.Auth Controller] Unable to connect to MySQL.");
            response.put("message", "Database error");
            code = 500;
        }
        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }

    // public static String getLikedList(Map<String, String> data, String method) {
    //     int code = 400;
    //     Map<String, String> response = new HashMap<>();
    //     if(!method.equals("POST")) {
    //         response.put("message", "Method not allowed!");
    //     }

    //     String token = data.get("token");
    //     if (!Auth.isValidToken(token)) {
    //         response.put("message", "Unauthorized");
    //         return Utils.assembleHTTPResponse(401, Utils.assembleJson(response));
    //     }
    //     String email = Auth.getEmailfromToken(token);


    //     try {
    //         // This must be DBUser to get list of users
    //         // ** Don't change this to dao class
    //         UserDao DBUser = new UserDao(SQLConnection.getConnection());

    //         // Get liked users
    //         List<String> columns = new ArrayList<>();
    //         columns.add("shown_user");
    //         Map<String, String> userData = new HashMap<>();
    //         String  user = userData.get("shown_user");

    //         List<User> users = DBUser.getAllLikedUsers(email);

    //         if(!users.isEmpty()) {
    //             List<Map<String, String>> userMatches = new ArrayList<>();

    //             for(User u : users) {
    //                 Map<String, String> userInfo = new HashMap<>();
    //                 userInfo.put("email", u.getEmail());
    //                 userInfo.put("first_name", u.getFirstName());
    //                 userInfo.put("last_name", u.getLastName());
    //                 userInfo.put("profile_picture_url", u.getProfilePicture());

    //                 userMatches.add(userInfo);
    //             }

    //             response.put("message", "Liked found");
    //             response.put("matches", Utils.assembleJson(userMatches));
    //         }
            
            

            
    //     } catch (SQLException | ClassNotFoundException e) {
    //         System.out.println("[Tools.Auth Controller] Unable to connect to MySQL.");
    //         response.put("message", "Database error");
    //         code = 500;
    //     }
    //     return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    // }


    /**
     * The complexity of this is awful
     * @param email1
     * @param email2
     * @return
     */
    public static double getSimilarity(String email1, String email2) {
        double similarity = 0;

        try {
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
                similarity = 1 - (totalValues / totalCount); // Normalize similarity score
            }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Match Controller] Unable to connect to MySQL.");
        }
        return similarity;
    }

    /**
     * Calculates all similarities from one user to every other user and adds it to the database.
     *
     * Note: This is for testing, and actually takes a really long time to calculate.
     *
     * @param email
     * @return
     */
    public static boolean calculateAllSimilaritiesForEmail(String email) {
        try {
            List<User> userList;
            UserDao uDao = new UserDao(SQLConnection.getConnection());
            Dao dao = new Dao(SQLConnection.getConnection());
            userList = uDao.getAllUsers();
            for (User b : userList) {
                if (email.equals(b.getEmail())) {
                    continue;
                }
                dao.insert(
                        Map.of("email1", email,
                                "email2", b.getEmail(),
                                "similarity_score", String.valueOf(MatchController.getSimilarity(email, b.getEmail()))),
                        "UserSimilarities");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static User getNextSimilarUser(String email) {
        User u = null;
        try {
            UserDao userDao = new UserDao(SQLConnection.getConnection());
            MatchingPriorityDao MPDao = new MatchingPriorityDao(SQLConnection.getConnection());
            int step = 0;
            String mostSimilarEmail = MPDao.getMostSimilar(email, step++);
            while (MPDao.exists(Map.of("shown_user", mostSimilarEmail, "user", email), "UserMatchInteractions")) {
                mostSimilarEmail = MPDao.getMostSimilar(email, step++);
            }
            u = userDao.getUserByEmail(mostSimilarEmail);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return u;
    }

    private static boolean isBooleanColumn(String column) {
        return Set.of("pet_friendly", "smoke", "smoke_okay", "drugs", "drugs_okay").contains(column);
    }
}