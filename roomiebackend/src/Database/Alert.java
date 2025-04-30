package Database;

import java.sql.Timestamp;

public class Alert {

    private int id;
    private String name;
    private String sender;
    private String description;
    private int groupchatId;
    private Boolean complete;

    public Alert(int id, String name, String sender, String description, int groupchatId, Boolean complete) {
        this.id = id;
        this.name = name;
        this.sender = sender;
        this.description = description;
        this.groupchatId = groupchatId;
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


    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public Boolean getComplete() {
        return complete;
    }
}
