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
            stmt.setString(1, String.valueOf(groupchat_id));
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    Message m = new Message(rs.getString("sender_email"), rs.getInt("groupchat_id"), rs.getString("message"), rs.getString("timestamp"));
                    messageList.add(m);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageList;
    }
}
