package Tools;

public class UserMatchInteraction {
    private int id;
    private String user;
    private String shown_user;
    private String relationship; // Either "true" or "false" (true -> match, false -> no match)

    public UserMatchInteraction(int id, String user, String shown_user, String relationship) {
        this.id = id;
        this.user = user;
        this.shown_user = shown_user;
        this.relationship = relationship;
    }

    public int getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getShownUser() {
        return shown_user;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setShownUser(String shown_user) {
        this.shown_user = shown_user;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}
