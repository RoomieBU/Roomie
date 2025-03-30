package Database;

/**
 * GroupChat Class
 * serves as a model for the Groupchat entity
 */
public class GroupChat {
    private int groupchatId;
    private String email1;
    private String email2;
    private String email3;
    private String email4;
    private String email5;
    private String email6;
    
    public GroupChat(int id, String email1, String email2, String email3, String email4, String email5, String email6)
    {
        this.groupchatId = id;
        this.email1 = email1;
        this.email2 = email2;
        this.email3 = email3;
        this.email4 = email4;
        this.email5 = email5;
        this.email6 = email6;
    }


    public int getGroupchatId() { return groupchatId; }
    public String getEmail1() { return email1; }
    public String getEmail2() { return email2; }
    public String getEmail3() { return email3; }
    public String getEmail4() { return email4; }
    public String getEmail5() { return email5; }
    public String getEmail6() { return email6; }
}