package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * user preferences data access object
 * interfaces with the database
 *
 * work in progress, add methods as needed
 */
public class UserPreferencesDao {
    private Connection connection;

    public UserPreferencesDao(Connection connection) {
        this.connection = connection;
    }

    public boolean userExists(int userId) throws SQLException {
        String query = "SELECT 1 FROM Users WHERE user_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if user exists
            }
        }
    }

    public boolean preferencesExist(int userId) throws SQLException {
        String query = "SELECT 1 FROM UserPreferences WHERE user_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if prefs exist
            }
        }
    }

    public boolean createUserPreferences(Map<String, String> data, String email) throws SQLException {
        // I don't think we need this check. User's should be able to change their preferences
        // if (!userExists(userId)) {
        //     throw new IllegalArgumentException("Error: user_id " + userId + " does not exist.");
        // }
        // if (preferencesExist(userId)) {
        //     throw new IllegalArgumentException("Error: UserPreferences already exist for user_id " + userId);
        // }

        if (data.isEmpty()) return false;

        int userId;

        // Try to find the user by email
        String selectUserQuery = "SELECT user_id FROM Users WHERE email = ?";
        PreparedStatement selectUserStmt = connection.prepareStatement(selectUserQuery);
        selectUserStmt.setString(1, email);
        ResultSet rs = selectUserStmt.executeQuery();

        if (rs.next()) {
            // User exists, retrieve user_id
            userId = rs.getInt("user_id");
        } else {
            // User doesn't exist, throw an error
            throw new SQLException("User with email " + email + " does not exist.");
        }

        // Step 2: Upsert into the UserPreferences table using the retrieved user_id
        String upsertQuery = 
        "INSERT INTO UserPreferences " +
        "(user_id, preferred_gender, pet_friendly, personality, wakeup_time, sleep_time, quiet_hours) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE " +
        "preferred_gender = VALUES(preferred_gender), " +
        "pet_friendly = VALUES(pet_friendly), " +
        "personality = VALUES(personality), " +
        "wakeup_time = VALUES(wakeup_time), " +
        "sleep_time = VALUES(sleep_time), " +
        "quiet_hours = VALUES(quiet_hours)";

        // Deal with wakeup time
        // Get wakeup_time from your data map
        String wakeupTimeStr = data.get("wakeup_time").toString();

        // Check if the time string is in "HH:MM" format (length 5) and append ":00" if needed.
        if (wakeupTimeStr != null && wakeupTimeStr.length() == 5) {
            wakeupTimeStr += ":00";
        }

        // Now convert to a java.sql.Time object
        java.sql.Time wakeupTime = java.sql.Time.valueOf(wakeupTimeStr);

        // Insert into UserPreferences
        try (PreparedStatement upsertStmt = connection.prepareStatement(upsertQuery)) {
            upsertStmt.setInt(1, userId);
            upsertStmt.setString(3, data.get("preferred_gender").toString());
            upsertStmt.setBoolean(4, Boolean.parseBoolean(data.get("pet_friendly").toString()));
            upsertStmt.setString(5, data.get("personality").toString());
            // Assuming wakeup_time and sleep_time are in the proper format for a Time column:
            upsertStmt.setTime(6, wakeupTime);
            upsertStmt.setTime(7, java.sql.Time.valueOf(data.get("sleep_time").toString()));
            upsertStmt.setString(8, data.get("quiet_hours").toString());
            upsertStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating UserPreferences: " + e.getMessage());
        }

        return true;
    }

    public List<UserPreferences> getAllUserPreferences() {
        List<UserPreferences> userPreferences = new ArrayList<>();
        String query = "SELECT * FROM UserPreferences";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
             try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserPreferences userPrefs = new UserPreferences(
                            rs.getInt("user_id"),
                            rs.getString("preferred_gender"),
                            rs.getBoolean("pet_friendly"),
                            rs.getString("personality"),
                            rs.getTime("wakeup_time"),
                            rs.getTime("sleep_time"),
                            rs.getString("quiet_hours")
                    );
                    userPreferences.add(userPrefs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user preferences", e);
        }


        return userPreferences;
    }

}
