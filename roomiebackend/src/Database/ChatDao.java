package Database;

import Tools.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDao extends Dao {

    public ChatDao(Connection connection) throws SQLException {
        super(connection);
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
