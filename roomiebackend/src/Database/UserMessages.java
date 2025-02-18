package Database;

import java.sql.Timestamp;

/**
 * UserMessages Class
 * serves as a model for the user ratings entity, holding all the relevant information
 * provides getters for each attribute, and toString for readable version
 *
 * interacts with the database
 */
public class UserMessages {
    private int messageId;
    private int senderId;
    private int groupId;
    private String content;
    private Timestamp sentAt;

    public UserMessages(int messageId, int senderId, int groupId, String content, Timestamp sentAt) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.groupId = groupId;
        this.content = content;
        this.sentAt = sentAt;
    }

    public int getMessageId() { return messageId; }
    public int getSenderId() { return senderId; }
    public int getGroupId() { return groupId; }
    public String getContent() { return content; }
    public Timestamp getSentAt() { return sentAt; }
}
