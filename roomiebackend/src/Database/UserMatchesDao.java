package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User Matches Data Access Object
 * interface for the user matches table
 *
 * work in progress, add methods as needed
 */
public class UserMatchesDao {
    private Connection connection;

    public UserMatchesDao(Connection connection) throws SQLException {
        this.connection = connection;
    }

    /**
     * Creates a user match from the given params
     */
    public void createUserMatches(int user1Id, int user2Id, Timestamp matchDate) {
        String query = "INSERT INTO UserMatches (user1_id, user2_id, match_date) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, user1Id);
            stmt.setInt(2, user2Id);
            stmt.setTimestamp(3, matchDate);

            stmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error creating user matches: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * returns a list of all usermatches from the database
     */
    public List<UserMatches> getAllUserMatches() {
        List<UserMatches> userMatches = new ArrayList<>();
        String query = "SELECT * FROM UserMatches";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                UserMatches userMatch = new UserMatches(
                        rs.getInt("match_id"),
                        rs.getInt("user1_id"),
                        rs.getInt("user2_id"),
                        rs.getTimestamp("match_date")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user matches: " + e.getMessage());
            e.printStackTrace();
        }

        return userMatches;
    }


}
