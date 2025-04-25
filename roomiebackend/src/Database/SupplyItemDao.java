package Database;

import Tools.Auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SupplyItemDao extends Dao {
    private Connection connection;

    public SupplyItemDao(Connection connection) {
        super(connection);
        this.connection = connection;
    }

    public int getGroupChatId(String token) {
        // get email from token
        String email = Auth.getEmailfromToken(token);

        System.out.println("[DEBUG] getGroupChatId: email from token = " + email);

        // query for gc
        String query = "SELECT * FROM GroupChats WHERE (email1 = ? OR email2 = ? OR email3 = ? OR email4 = ? OR email5 = ? OR email6 = ?) AND confirmed = 1";
        int id = -1;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, email);
            stmt.setString(3, email);
            stmt.setString(4, email);
            stmt.setString(5, email);
            stmt.setString(6, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
                System.out.println("[DEBUG] getGroupChatId: found group_chat_id = " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return id;
    }

    public int getListId(int groupChatId) {
       String query = "SELECT list_id FROM SupplyList WHERE group_chat_id = ?";
       int listId = -1;
       try (PreparedStatement stmt = connection.prepareStatement(query)) {
           stmt.setInt(1, groupChatId);
           ResultSet rs = stmt.executeQuery();
           if (rs.next()) {
               listId = rs.getInt("list_id");
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return listId;

    }

    /**
     * returns true if successfully created a shared supply in the database
     *  checks to see if a list has already been made, returns true in that case as well
     *  this is for easier handling of instantiation of lists i hope :)
     */
    public boolean createSharedSupply(String token) {
        // get group chat id
        int groupChatId = getGroupChatId(token);
        if (groupChatId < 0) return false;

        // 1) check if already exists
        String checkSql = "SELECT 1 FROM SupplyList WHERE group_chat_id = ? LIMIT 1";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, groupChatId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // already have one, nothing to do
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // 2) not found, insert a new one
        String insertSql = "INSERT INTO SupplyList (group_chat_id) VALUES (?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setInt(1, groupChatId);
            int affected = insertStmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns a list of all the items in a users supply list
     */
    public List<Item> getItems(String token) {
        // get gc id
        int groupChatId = getGroupChatId(token);
        if (groupChatId < 0) return new ArrayList<>();
        // get list id
        int listId = getListId(groupChatId);
        if (listId < 0) return new ArrayList<>();

        String query = "Select * FROM Item WHERE list_id = ?";
        List<Item> items = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Item item = new Item(
                  rs.getInt("item_id"),
                  rs.getInt("list_id"),
                  rs.getString("name"),
                  rs.getInt("amount"),
                  rs.getDate("last_purchased")
                );
                System.out.println("[DEBUG] getItems: retrieved item " + item.getName() + " x" + item.getAmount());
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }



        return items;
    }

    /**
     * returns true if successfully added into database
     */
    public boolean addItem(String token, Map<String, String> data) {
        int groupChatId = getGroupChatId(token);
        if (groupChatId < 0) return false;

        int listId = getListId(groupChatId);
        if (listId < 0) return false;

        String name   = data.get("name");
        String amount = data.get("amount");

        System.out.println("[DEBUG] addItem: name = " + name + ", amount = " + amount);

        String sql = "INSERT INTO Item (list_id, name, amount, last_purchased) VALUES (?, ?, ?, CURRENT_DATE())";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, listId);
            stmt.setString(2, name);
            stmt.setInt(3, Integer.parseInt(amount));
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editItem(String token, Map<String, String> data) {
        int groupChatId = getGroupChatId(token);
        if (groupChatId < 0) return false;

        int listId = getListId(groupChatId);
        if (listId < 0) return false;

        String idStr   = data.get("id");
        String name    = data.get("item");
        String amount  = data.get("amount");

        System.out.println("[DEBUG] editItem: id = " + idStr + ", name = " + name + ", amount = " + amount);

        String sql = "UPDATE Item SET name = ?, amount = ? WHERE item_id = ? AND list_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, Integer.parseInt(amount));
            stmt.setInt(3, Integer.parseInt(idStr));
            stmt.setInt(4, listId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteItem(String token, String idStr) {
        int groupChatId = getGroupChatId(token);
        if (groupChatId < 0) return false;
        int listId = getListId(groupChatId);
        if (listId < 0) return false;

        int id;
        try { id = Integer.parseInt(idStr); } catch (NumberFormatException e) { return false; }

        String sql = "DELETE FROM Item WHERE item_id = ? AND list_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, listId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

}
