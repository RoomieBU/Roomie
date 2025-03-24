package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MatchingPriorityDao extends Dao{

    public MatchingPriorityDao(Connection connection) throws SQLException {
        super(connection);
    }

    public boolean removeIfExists(String email) {
        String query = "DELETE FROM UserSimilarities WHERE email1 = ? OR email2 = ?";

        try (PreparedStatement stmt = super.connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}
