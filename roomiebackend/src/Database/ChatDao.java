package Database;

import Tools.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDao extends Dao {

    public ChatDao(Connection connection) {
        super(connection);
    }

    public List<String> getGroupChatEmails(String email, int groupchatId) {
        List<String> emails = new ArrayList<>();
        String query = "SELECT id, email1, email2, email3, email4, email5, email6 FROM GroupChats WHERE id = ?";
        
        try (
            Connection conn = SQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, groupchatId);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                System.out.println("Found group chat with ID " + groupchatId);
                // Loop through each email column and add any non-null email that isn't the current user
                for (int i = 1; i <= 6; i++) {
                    String currentEmail = rs.getString("email" + i);
                    System.out.println("Column email" + i + " contains: " + currentEmail);
                    if (currentEmail != null && !currentEmail.isEmpty() && !currentEmail.equals(email)) {
                        emails.add(currentEmail);
                        System.out.println("Added email: " + currentEmail);
                    }
                }
            } else {
                System.out.println("No group chat found with ID " + groupchatId);
            }
        } catch (SQLException e) {
            System.out.println("SQL error when fetching group chat " + groupchatId + ": " + e.getMessage());
            e.printStackTrace();
        }
    
        System.out.println("Total emails found in chat " + groupchatId + ": " + emails.size());
        return emails;
    }

    public boolean deleteRoommateRequest(String sender, int groupchatId) {
        String query = "DELETE FROM UserRoommateRequests WHERE sender = ? AND groupchat_id = ?";
    
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, sender);
            stmt.setInt(2, groupchatId);
    
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getRoommateRequestStatus(String email, int groupchatId) {
        String checkQuery = "SELECT COUNT(*) AS total_requests FROM UserRoommateRequests WHERE groupchat_id = ?";
        String status = "No Request Yet";
    
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, groupchatId);
            try (ResultSet checkRs = checkStmt.executeQuery()) {
                if (checkRs.next()) {
                    int count = checkRs.getInt("total_requests");
                    System.out.println("Found " + count + " records for groupchatId: " + groupchatId);
                    if (count == 0) return status;
                }
            }
    
            String query = """
                SELECT CASE
                           WHEN accepted = 1 THEN 'Accepted'
                           WHEN accepted = 0 THEN 'Declined'
                           ELSE 'Pending'
                       END AS response_status
                FROM UserRoommateRequests
                WHERE sender = ? AND groupchat_id = ?
            """;
    
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                stmt.setInt(2, groupchatId);
    
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        status = rs.getString("response_status");
                        System.out.println("User " + email + " responded: " + status);
                    } else {
                        System.out.println("No record found for user " + email + " in group " + groupchatId);
                    }
                }
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
