package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * user housing data access object
 * interfaces with database
 *
 * work in progress, add methods as needed
 */
public class UserHousingDao {
    private Connection connection;

    public UserHousingDao(Connection connection) {
        this.connection = connection;
    }

    public boolean userExists(int userId) throws SQLException {
        String query = "SELECT 1 FROM Users WHERE user_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if user user exists
            }
        }
    }

    public boolean housingExists(int userId) throws SQLException {
        String query = "SELECT 1 FROM UserHousing WHERE user_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if housing exists
            }
        }
    }

    public void createHousing(int userId, String location) throws SQLException {
        if (!userExists(userId)) {
            throw new IllegalArgumentException("Error: user_id " + userId + " does not exist.");
        }
        if (housingExists(userId)) {
            throw new IllegalArgumentException("Error: UserHouse already exist for user_id " + userId);
        }

        String query = "INSERT INTO UserHousing (user_id, location) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, location);

            stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating UserSchedule: " + e.getMessage());
        }
    }

    public List<UserHousing> getAllHousing() {
        List<UserHousing> userHousing = new ArrayList<>();
        String query = "SELECT * FROM UserHousing";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserHousing userHouse = new UserHousing(
                            rs.getInt("user_id"),
                            rs.getString("location")
                    );
                    userHousing.add(userHouse);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all UserHousing: " + e.getMessage());
        }

        return userHousing;
    }
}
