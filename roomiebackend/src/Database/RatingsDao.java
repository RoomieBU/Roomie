package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingsDao extends Dao {

    public RatingsDao(Connection connection) {
        super(connection);
    }

    public boolean insertRating(int reviewerId, int ratedId, int ratingValue, String comment) {
        String query = "INSERT INTO UserRatings (reviewer_user, rated_user, rating_value, comment) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE rating_value = VALUES(rating_value), comment = VALUES(comment)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, reviewerId);
            stmt.setInt(2, ratedId);
            stmt.setInt(3, ratingValue);
            stmt.setString(4, comment);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Double getAverageRating(int ratedUserId) throws SQLException {
        String sql = "SELECT AVG(rating_value) AS avg FROM UserRatings WHERE rated_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ratedUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("avg") : null;
            }
        }

    }
}