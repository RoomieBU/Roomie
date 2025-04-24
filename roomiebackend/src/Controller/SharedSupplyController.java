package Controller;

import Database.Item;
import Database.SQLConnection;
import Database.SupplyItemDao;
import Tools.Auth;
import Tools.HTTPResponse;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedSupplyController {
    /**
     * Returns a string of all items of a users supply list
     * @param data user token
     * @param method POST
     * @return http response with string containing all items and counts of a shared supply
     */
    public static String getItems(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        Connection conn = SQLConnection.getConnection();

        // Get a list of items
        String token = data.get("token");
        SupplyItemDao dao = new SupplyItemDao(conn);
        List<Item> items = dao.getItems(token);

        if (items == null || items.isEmpty()) {
            response.setMessage("message", "No items found.");
            response.code = 404;
            return response.toString();
        }

        // Build a simple CSV of items: id|name|amount|lastPurchased
        StringBuilder sb = new StringBuilder();
        for (Item it : items) {
            sb.append(it.getId())
                    .append("|")
                    .append(it.getName())
                    .append("|")
                    .append(it.getAmount())
                    .append("|")
                    .append(it.getLastPurchased())
                    .append(",");
        }
        // remove trailing comma
        if (sb.length() > 0) sb.setLength(sb.length() - 1);

        response.setMessage("items", sb.toString());
        response.code = 200;
        return response.toString();
    }

    /**
     * Adds an item into the database
     * @param data token, item, count
     * @param method POST
     * @return http response
     */
    public static String addItem(Map<String, String> data, String method){
        HTTPResponse response = new HTTPResponse();
        Connection conn = SQLConnection.getConnection();

        // Add item
        String token = data.get("token");
        String item = data.get("item");
        String amount = data.get("amount");

        // put data into database
        Map<String, String> itemData = new HashMap<>();
        itemData.put("name", item);
        itemData.put("amount", amount);

        SupplyItemDao dao = new SupplyItemDao(conn);
        boolean ok = dao.addItem(token, itemData);

        if (!ok) {
            response.setMessage("message", "Failed to add item.");
            response.code = 500;
        } else {
            response.setMessage("message", "Item added successfully.");
            response.code = 201;
        }
        return response.toString();

    }

    /**
     * edits an existing item count
     * @param data token, item, new count
     * @param method POST
     * @return http response
     */
    public static String editItem(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        Connection conn = SQLConnection.getConnection();

        String token = data.get("token");
        String id = data.get("id");
        String name = data.get("item");
        String amountStr = data.get("amount");

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            response.setMessage("message", "Invalid amount.");
            response.code = 400;
            return response.toString();
        }

        SupplyItemDao dao = new SupplyItemDao(conn);
        boolean ok;
        if (amount == 0) {
            ok = dao.deleteItem(token, id);
            if (ok) {
                response.setMessage("message", "Item deleted successfully.");
                response.code = 200;
            } else {
                response.setMessage("message", "Failed to delete item.");
                response.code = 500;
            }
        } else {
            Map<String, String> itemData = new HashMap<>();
            itemData.put("id", id);
            itemData.put("name", name);
            itemData.put("amount", amountStr);
            ok = dao.editItem(token, itemData);
            if (ok) {
                response.setMessage("message", "Item updated successfully.");
                response.code = 200;
            } else {
                response.setMessage("message", "Failed to edit item.");
                response.code = 500;
            }
        }
        return response.toString();
    }

    /** ensures a SharedSupply row exists for this user’s group */
    public static String checkAndCreateSupplyList(Map<String,String> data, String method) {
        HTTPResponse resp = new HTTPResponse();
        Connection conn = SQLConnection.getConnection();
        String token = data.get("token");
        boolean ok = new SupplyItemDao(conn).createSharedSupply(token);
        if (ok) {
            resp.setMessage("message","Supply list ready.");
            resp.code = 200;
        } else {
            resp.setMessage("message","Could not initialize supply list.");
            resp.code = 500;
        }
        return resp.toString();
    }

}
