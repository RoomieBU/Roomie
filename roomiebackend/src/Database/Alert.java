package Database;

public class Alert {

    private String name;
    private String sender;
    private String description;
    private int roommateid;
    private String start_time;
    private String end_time;

    // Full constructor
    public Alert(String name, String sender, String description, int roommateid, String start_time, String end_time) {
        this.name = name;
        this.sender = sender;
        this.description = description;
        this.roommateid = roommateid;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getSender() {
        return sender;
    }

    public String getDescription() {
        return description;
    }

    public int getRoommateid() {
        return roommateid;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRoommateid(int roommateid) {
        this.roommateid = roommateid;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
