package Database;

import Tools.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDao extends Dao {

    public ChatDao(Connection connection) {
        super(connection);
    }

    public String getRoommateRequestStatus(String email, int groupchatId) {
        // First, check if there are any records for the given groupchat_id
    
        String checkQuery = "SELECT COUNT(*) AS total_requests FROM UserRoommateRequests WHERE groupchat_id = ?";
    
        // Default to "No request yet"
        String status = "No Request Yet";
    
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, groupchatId);
    
            try (ResultSet checkRs = checkStmt.executeQuery()) {
                if (checkRs.next() && checkRs.getInt("total_requests") == 0) {
                    // No records for this groupchat_id, return immediately
                    return status;
                }
            }
    
            // If there are records, check the user's response status
            String query = "SELECT CASE WHEN accepted = 1 THEN 'Accepted' WHEN accepted = 0 THEN 'Declined' ELSE 'Pending' END AS response_status FROM UserRoommateRequests WHERE sender = ? AND groupchat_id = ?";

    
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                stmt.setInt(2, groupchatId);
    
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        status = rs.getString("response_status");  // Get the status if record exists
                    }
                }
    
            } catch (SQLException e) {
                e.printStackTrace();
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return status;
    }


    public List<Message> getChatHistory(int groupchat_id) {
        String query = "SELECT * FROM ChatHistory WHERE groupchat_id = ? ORDER BY timestamp ASC";

        List<Message> messageList = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupchat_id);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    Message m = new Message(
                            rs.getString("sender_email"),
                            rs.getInt("groupchat_id"),
                            rs.getString("message"),
                            rs.getString("timestamp"));
                    messageList.add(m);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    public List<GroupChat> getGroupchats(String email) {
        String query = "SELECT * FROM GroupChats WHERE email1 = ? OR email2 = ? OR email3 = ? OR email4 = ? OR email5 = ? OR email6 = ?";

        List<GroupChat> chatList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, email);
            stmt.setString(3, email);
            stmt.setString(4, email);
            stmt.setString(5, email);
            stmt.setString(6, email);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GroupChat gc = new GroupChat(
                        rs.getInt("id"), 
                        rs.getString("email1"), 
                        rs.getString("email2"), 
                        rs.getString("email3"), 
                        rs.getString("email4"), 
                        rs.getString("email5"), 
                        rs.getString("email6"));
                    chatList.add(gc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatList;
    }
}
