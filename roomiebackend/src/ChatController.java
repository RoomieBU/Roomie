import Database.ChatDao;
import Database.Dao;
import Database.GroupChat;
import Database.SQLConnection;
import Database.User;
import Database.UserDao;
import Tools.Auth;
import Tools.HTTPResponse;
import Tools.Utils;
import com.google.gson.Gson;
import Tools.Message;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

public class ChatController {

    public static String createGroupChat(Map<String, String> data, String method) {
        ChatDao dao = new ChatDao(SQLConnection.getConnection());
        String email = Auth.getEmailfromToken(data.get("token"));

        System.out.println("Received ids: " + data.get("groupChatIds"));
        List<String> groupchatIdsRaw = List.of(data.get("groupChatIds"));
        List<Integer> groupchatIds = new ArrayList<>();
        for (String id : groupchatIdsRaw) {
            groupchatIds.add(Integer.parseInt(id)); // error
        }

        System.out.println("Processing IDs: " + groupchatIds);

        if (groupchatIds.isEmpty()) {
            return Utils.assembleHTTPResponse(400, "No group chat IDs provided.");
        }

        // Collect all emails from the groupchat IDs
        Set<String> emails = new LinkedHashSet<>(); // Use LinkedHashSet to maintain order and avoid duplicates

        for (int id : groupchatIds) {
            List<String> chatEmails = dao.getGroupChatEmails(email, id);
            System.out.println("Found emails for ID " + id + ": " + chatEmails);
            emails.addAll(chatEmails);
        }

        // Add current user's email
        emails.add(email);

        System.out.println("Final emails list: " + emails);

        // Check if there's at least 2 participants
        if (emails.size() < 2) {
            return Utils.assembleHTTPResponse(400, "A group chat requires at least two distinct users.");
        }

        // Limit to 6 emails max due to schema
        if (emails.size() > 6) {
            return Utils.assembleHTTPResponse(400, "Group chat cannot have more than 6 members.");
        }

        // Prepare data for insertion
        Map<String, String> insertData = new HashMap<>();
        int i = 1;
        for (String e : emails) {
            insertData.put("email" + i, e);
            i++;
        }

        // Fill remaining email fields with null
        while (i <= 6) {
            insertData.put("email" + i, null);
            i++;
        }

        // Log insert data
        System.out.println("Insert Data: " + insertData);

        // Insert into the database
        boolean isInserted = dao.insert(insertData, "GroupChats");
        if (isInserted) {
            return Utils.assembleHTTPResponse(200, "Group chat created successfully.");
        } else {
            return Utils.assembleHTTPResponse(500, "Failed to insert data into the database.");
        }
    } 
    


    public static String resetRoommateRequestChoice(Map<String, String> data, String method) {
        ChatDao dao = new ChatDao(SQLConnection.getConnection());

        String email = Auth.getEmailfromToken(data.get("token"));
        int groupchatId = Integer.parseInt(data.get("groupchat_id"));

        boolean deleted = dao.deleteRoommateRequest(email, groupchatId);

        if (deleted) {
            return Utils.assembleHTTPResponse(200, "Request deleted");
        } else {
            return Utils.assembleHTTPResponse(404, "No matching request found");
        }
    }

    public static String receiveRoommateRequest(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        ChatDao dao = new ChatDao(SQLConnection.getConnection());
        

        Map<String, String> insertData = new HashMap<>();

        String email = Auth.getEmailfromToken(data.get("token"));
        insertData.put("sender", email);
        insertData.put("groupchat_id", data.get("groupchat_id"));
        insertData.put("accepted", data.get("response"));

        dao.insert(insertData, "UserRoommateRequests");

        response.code = 200;
        response.setMessage("message", "request stored");

        return response.toString();
    }

    public static String receiveMessage(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        ChatDao dao = new ChatDao(SQLConnection.getConnection());

        Map<String, String> insertData = new HashMap<>();

        String email = Auth.getEmailfromToken(data.get("token"));
        insertData.put("sender_email", email);
        insertData.put("groupchat_id", data.get("groupchat_id"));
        insertData.put("message", data.get("message"));

        dao.insert(insertData, "ChatHistory");

        response.code = 200;
        return response.toString();
    }

    public static String sendRoommateRequestStatus(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        ChatDao dao = new ChatDao(SQLConnection.getConnection());

        String email = Auth.getEmailfromToken(data.get("token"));
        int groupId = Integer.parseInt(data.get("groupchat_id"));
        String status = dao.getRoommateRequestStatus(email, groupId);

        response.setMessage("message", "status found");
        response.setMessage("status", status);
        response.code = 200;
        
        return response.toString();
    }
    
    public static String sendChatHistory(Map<String, String> data, String method) {
        ChatDao dao = new ChatDao(SQLConnection.getConnection());

        String email = Auth.getEmailfromToken(data.get("token"));
        int groupId = Integer.parseInt(data.get("groupchat_id"));
        List<Message> messageList = dao.getChatHistory(groupId);
        for (Message m : messageList) {
            if (m.getSenderEmail().equals(email)) {
                m.selfSent();
            }
        }
        Gson gson = new Gson();
        return Utils.assembleHTTPResponse(200, gson.toJson(messageList));
    }


    public static String sendGroupChats(Map<String, String> data, String method) {
        ChatDao dao = new ChatDao(SQLConnection.getConnection());

        String email = Auth.getEmailfromToken(data.get("token"));
        List<GroupChat> groupChats = dao.getGroupchats(email);
        Gson gson = new Gson();

        return Utils.assembleHTTPResponse(200, gson.toJson(groupChats));
    }
}
