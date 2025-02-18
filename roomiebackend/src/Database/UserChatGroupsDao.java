package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User Chat Group Data Access Object
 * interface for the user chat groups table
 *
 * work in progress, add methods as needed
 */
public class UserChatGroupsDao {
    private Connection connection;

    public UserChatGroupsDao(Connection connection) throws SQLException {
        this.connection = connection;
    }

    public void createUserChatGroup(String groupName) {
        String query = "INSERT INTO UserChatGroups (group_name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, groupName);
            stmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error creating user chat group: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<UserChatGroups> getAllUserChatGroups() {
        List<UserChatGroups> chatGroups = new ArrayList<>();
        String query = "SELECT * FROM UserChatGroups";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                chatGroups.add(new UserChatGroups(
                        rs.getInt("group_id"),
                        rs.getString("group_name"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user chat groups: " + e.getMessage());
            e.printStackTrace();
        }
        return chatGroups;
    }
}
