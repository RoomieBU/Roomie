package Tools;

public class Message {
    private int id;
    private String senderEmail;
    private int groupChatId;
    private String message;
    private String timestamp;
    private boolean sentBySelf = false;

    public Message(String senderEmail, int groupChatId, String message, String timestamp) {
        this.senderEmail = senderEmail;
        this.groupChatId = groupChatId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public int getGroupChatId() {
        return groupChatId;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void selfSent() {
        sentBySelf = true;
    }

    public boolean getSendBySelf() {
        return sentBySelf;
    }
}
