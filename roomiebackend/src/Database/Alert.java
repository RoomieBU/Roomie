package Database;

public class Alert {

    private String name;
    private String sender;
    private String description;
    private int groupchatId;
    private String start_time;
    private String end_time;

    public Alert(String name, String sender, String description, int groupchatId, String start_time, String end_time) {
        this.name = name;
        this.sender = sender;
        this.description = description;
        this.groupchatId = groupchatId;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public String getName() {
        return name;
    }

    public String getSender() {
        return sender;
    }

    public String getDescription() {
        return description;
    }

    public int getGroupchatId() {
        return groupchatId;
    }

    public String getStartTime() {
        return start_time;
    }

    public String getEndTime() {
        return end_time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGroupchatId(int roommateid) {
        this.groupchatId = groupchatId;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
