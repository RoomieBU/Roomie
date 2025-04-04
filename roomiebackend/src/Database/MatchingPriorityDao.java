package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MatchingPriorityDao extends Dao{

    public MatchingPriorityDao(Connection connection) {
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

    /**
     * Returns the most similar user to the given email by however many steps away.
     * E.g. 0 steps = most similar user, 1 steps = 2nd most similar user...
     * @param email
     * @param steps
     * @return
     */
    public String getMostSimilar(String email, int steps) {
        String email1 = "";
        String email2 = "";
        String query = "SELECT email1, email2, similarity_score FROM UserSimilarities where email1 = ? OR email2 = ? ORDER BY similarity_score DESC";
        try (PreparedStatement stmt = super.connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, email);
            try (ResultSet rs = stmt.executeQuery()) {
                for (int i = 0; i < steps + 1; i++) {
                    rs.next();
                    email1 = rs.getString("email1");
                    email2 = rs.getString("email2");
                }
            }
        } catch (SQLException e) {
            // something?
        }
        if (email1.equals(email)) return email2;
        return email1;
    }
}
