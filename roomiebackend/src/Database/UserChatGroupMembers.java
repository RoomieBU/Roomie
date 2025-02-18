package Database;

public class UserChatGroupMembers {
    private int groupId;
    private int userId;

    public UserChatGroupMembers(int groupId, int userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public int getGroupId() { return groupId; }
    public int getUserId() { return userId; }
}
