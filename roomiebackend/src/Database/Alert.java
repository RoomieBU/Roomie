package Database;

import java.sql.Timestamp;

public class Alert {

    private int id;
    private String name;
    private String sender;
    private String description;
    private int groupchatId;
    private Timestamp start_time;
    private Timestamp end_time;
    private Boolean complete;

    public Alert(int id, String name, String sender, String description, int groupchatId, Timestamp start_time, Timestamp end_time, Boolean complete) {
        this.id = id;
        this.name = name;
        this.sender = sender;
        this.description = description;
        this.groupchatId = groupchatId;
        this.start_time = start_time;
        this.end_time = end_time;
        this.complete = complete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Timestamp getStartTime() {
        return start_time;
    }

    public Timestamp getEndTime() {
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

    public void setStart_time(Timestamp start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(Timestamp end_time) {
        this.end_time = end_time;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public Boolean getComplete() {
        return complete;
    }
}
