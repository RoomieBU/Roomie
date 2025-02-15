package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * User schedule data access object
 * interfaces with database
 *
 * work in progress, add methods as needed
 */
public class UserScheduleDao {
    private Connection connection;

    public UserScheduleDao(Connection connection) {
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

    public boolean scheduleExists(int userId) throws SQLException {
        String query = "SELECT 1 FROM UserSchedule WHERE user_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if schedule exists
            }
        }
    }

    public void createSchedule(int userId, String schedule) throws SQLException {
        if (!userExists(userId)) {
            throw new IllegalArgumentException("Error: user_id " + userId + " does not exist.");
        }
        if (scheduleExists(userId)) {
            throw new IllegalArgumentException("Error: UserSchedule already exist for user_id " + userId);
        }

        String query = "INSERT INTO UserSchedule (user_id, schedule) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, schedule);

            stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating UserSchedule: " + e.getMessage());
        }
    }

    public List<UserSchedule> getAllSchedules() {
        List<UserSchedule> userSchedule = new ArrayList<>();
        String query = "SELECT * FROM UserSchedule";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserSchedule userSched = new UserSchedule(
                            rs.getInt("user_id"),
                            rs.getString("schedule")
                    );
                    userSchedule.add(userSched);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all UserSchedules: " + e.getMessage());
        }

        return userSchedule;
    }
}
