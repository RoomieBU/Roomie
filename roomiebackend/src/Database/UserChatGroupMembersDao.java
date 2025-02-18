package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User chat group members Data Access Object
 * interface for the user chat group members table
 *
 * work in progress, add methods as needed
 */
public class UserChatGroupMembersDao {
    private Connection connection;

    public UserChatGroupMembersDao(Connection connection) throws SQLException {
        this.connection = connection;
    }

    public void addMemberToGroup(int groupId, int userId) {
        String query = "INSERT INTO UserChatGroupMembers (group_id, user_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding member to group: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<UserChatGroupMembers> getAllGroupMembers() {
        List<UserChatGroupMembers> groupMembers = new ArrayList<>();
        String query = "SELECT * FROM UserChatGroupMembers";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                groupMembers.add(new UserChatGroupMembers(
                        rs.getInt("group_id"),
                        rs.getInt("user_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving group members: " + e.getMessage());
            e.printStackTrace();
        }
        return groupMembers;
    }
}
