package Database;


import Tools.UserMatchInteraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserMatchInteractionDao extends Dao{
    private static Connection connection;

    /**
     * Takes the connection given to connect to the database
     */
    public UserMatchInteractionDao(Connection connection) throws SQLException {
        super(connection);
        this.connection = connection;
    }

    public Boolean sendMatchInteraction(Map<String, String> data, String email) {
        String query = "INSERT INTO UserMatchInteractions (user, shown_user, relationship) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, data.get("user"));
            pstmt.setString(2, data.get("shown_user"));
            pstmt.setString(3, data.get("relationship"));
    
            int rowsInserted = pstmt.executeUpdate();
            System.out.println(rowsInserted);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getUserSimilarity(String email1, String email2) {
        // Get some users similarities
        String query = "SELECT similarity_score FROM UserSimilarities WHERE (email1 = ? AND email2 = ?) OR (email1 = ? AND email2 = ?)";
        double sim = 2;

        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, email1);
            pstmt.setString(2, email2);
            pstmt.setString(3, email1);
            pstmt.setString(4, email2);

            ResultSet rs = pstmt.executeQuery(query);
            if (rs.next()) {sim = rs.getDouble("similarity_score");}

        } catch (SQLException e) {
            e.printStackTrace();
            return sim;
        }


        return sim;
    }

    /**
     * Gets pairs of users that have matched with each other
     * @return
     */
    public List<UserMatchInteraction> getAllMatchedUsers() {
        List<UserMatchInteraction> total = new ArrayList<>();
        String query = "SELECT um1.* FROM UserMatchInteractions um1 JOIN UserMatchInteractions um2 " +
                "ON um1.user = um2.shown_user AND um1.shown_user = um2.user WHERE um1.relationship " +
                "= 'true' AND um2.relationship = 'true'";
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery(query);
            while (rs.next()) {
                UserMatchInteraction current = new UserMatchInteraction(
                        rs.getInt("id"),
                        rs.getString("user"),
                        rs.getString("shown_user"),
                        rs.getString("relationship")
                );
                total.add(current);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public boolean removeAllForUser(String email) {
        String query = "DELETE FROM UserMatchInteractions WHERE user = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
