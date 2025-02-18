package Database;

import java.sql.Timestamp;

/**
 * UserChatGroups Class
 * serves as a model for the user ratings entity, holding all the relevant information
 * provides getters for each attribute, and toString for readable version
 *
 * interacts with the database
 */
public class UserChatGroups {
    private int groupId;
    private String groupName;
    private Timestamp createdAt;

    public UserChatGroups(int groupId, String groupName, Timestamp createdAt) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
    public Timestamp getCreatedAt() { return createdAt; }

}
