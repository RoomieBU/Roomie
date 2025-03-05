package Database;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class UserMatchInteractionDao {
    private static Connection connection;

    /**
     * Takes the connection given to connect to the database
     */
    public UserMatchInteractionDao(Connection connection) throws SQLException {
        this.connection = connection;
    }

    public static String sendMatchInteraction(Map<String, String> data, String email) {
        String query = "INSERT INTO UserMatchInteractions (user, shown_user, relationship) VALUES (?, ?, ?)";
    
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, data.get("user"));
            pstmt.setString(2, data.get("shown_user"));
            pstmt.setString(3, data.get("relationship"));
    
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0 ? "success" : "failure"; // Return a String instead of boolean
        } catch (SQLException e) {
            e.printStackTrace();
            return "error: " + e.getMessage(); // Return a String error message
        }
    }
    

}
