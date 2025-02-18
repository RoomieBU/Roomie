package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User Messages Data Access Object
 * interface for the user Messages table
 *
 * work in progress, add methods as needed
 */
public class UserMessagesDao {
    private Connection connection;

    public UserMessagesDao(Connection connection) throws SQLException {
        this.connection = connection;
    }

    public void createUserMessage(int senderId, int groupId, String content) {
        String query = "INSERT INTO UserMessages (sender_id, group_id, content) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, groupId);
            stmt.setString(3, content);
            stmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error creating user message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<UserMessages> getAllUserMessages() {
        List<UserMessages> messages = new ArrayList<>();
        String query = "SELECT * FROM UserMessages";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new UserMessages(
                        rs.getInt("message_id"),
                        rs.getInt("sender_id"),
                        rs.getInt("group_id"),
                        rs.getString("content"),
                        rs.getTimestamp("sent_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user messages: " + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }
}
