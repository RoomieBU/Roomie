package Database;

public class AlertResponse {

    private int id;
    private int alert_id;
    private String sender;
    private String comment;
    private int reaction;

    public AlertResponse(int id, int alert_id, String sender, String comment, int reaction) {
        this.id = id;
        this.alert_id = alert_id;
        this.sender = sender;
        this.comment = comment;
        this.reaction = reaction;
    }

    public int getId() {return id;}
    public int getAlertId() {return alert_id;}
    public String getSender() {return sender;}
    public String getComment() {return comment;}
    public int getReaction() {return reaction;}
}
