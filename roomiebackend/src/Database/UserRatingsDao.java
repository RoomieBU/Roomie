package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User Ratings Data Access Object
 * interface for the UserRating Table
 *
 * work in progress, add methods as needed
 */
public class UserRatingsDao {
    private Connection connection;

    /**
     * Takes the connection given to connect to the database
     */
    public UserRatingsDao(Connection connection) throws SQLException {
        this.connection = connection;
    }

    /**
     * Creates a UserRating from the given params
     */
    public void createUserRatings(int ratedUser, int reviewerUser, int ratingValue, String comment) {
        String query = "INSERT INTO UserRatings (rated_user, reviewer_user, rating_value, comment) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ratedUser);
            stmt.setInt(2, reviewerUser);
            stmt.setInt(3, ratingValue);
            stmt.setString(4, comment);
        } catch (SQLException e) {
            System.err.println("Error creating User Rating: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of all User Ratings from the database
     */
    public List<UserRatings> getAllUserRatings() throws SQLException {
        List<UserRatings> userRatings = new ArrayList<>();
        String query = "SELECT * FROM UserRatings";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserRatings userRating = new UserRatings(
                        rs.getInt("user_id"),
                        rs.getInt("rated_user"),
                        rs.getInt("reviewer_user"),
                        rs.getInt("rating_value"),
                        rs.getString("comment")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting all user ratings: " + e.getMessage());
            e.printStackTrace();
        }

        return userRatings;
    }
}
