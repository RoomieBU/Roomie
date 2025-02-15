package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public void createUserPreferences(int userId, String preferredGender, boolean petFriendly, String personality, Time wakeupTime, Time sleepTime, String quietHours) throws SQLException {
        if (!userExists(userId)) {
            throw new IllegalArgumentException("Error: user_id " + userId + " does not exist.");
        }
        if (preferencesExist(userId)) {
            throw new IllegalArgumentException("Error: UserPreferences already exist for user_id " + userId);
        }

        // Insert into UserPreferences
        String query = "INSERT INTO UserPreferences (user_id, preferred_gender, pet_friendly, personality, wakeup_time, sleep_time, quiet_hours) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, preferredGender);
            stmt.setBoolean(3, petFriendly);
            stmt.setString(4, personality);
            stmt.setTime(5, wakeupTime);
            stmt.setTime(6, sleepTime);
            stmt.setString(7, quietHours);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating UserPreferences: " + e.getMessage());
        }
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
