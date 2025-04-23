package Database;

import java.sql.Date;

public class Item {
    private int listId;
    private String name;
    private int amount;
    private Date lastPurchased;

    public Item(int listId, String name, int amount, Date lastPurchased) {
        this.listId = listId;
        this.name = name;
        this.amount = amount;
        this.lastPurchased = lastPurchased;
    }

    // getters
    public int getListId() {return listId; }
    public String getName() {return name; }
    public int getAmount() {return amount; }
    public Date getLastPurchased() {return lastPurchased; }
}
