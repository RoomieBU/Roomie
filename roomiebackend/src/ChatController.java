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
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ChatController {

    public static String createGroupChat(Map<String, String> data, String method) {
        ChatDao dao = new ChatDao(SQLConnection.getConnection());
    
        String email = Auth.getEmailfromToken(data.get("token"));
        String rawIds = data.get("groupChatIds"); // Should look like "[1, 2, 3]"
        List<Integer> groupchatIds = new ArrayList<>();
    
        try {
            // Remove square brackets and trim extra spaces
            rawIds = rawIds.replaceAll("\\[|\\]", "").trim();
    
            // Check if rawIds is empty or invalid
            if (rawIds.isEmpty()) {
                return Utils.assembleHTTPResponse(400, "No group chat IDs provided.");
            }
    
            // Split the rawIds string into individual group chat IDs
            String[] parts = rawIds.split(",");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    groupchatIds.add(Integer.parseInt(trimmed));
                }
            }
    
            // If no valid group chat IDs were parsed
            if (groupchatIds.isEmpty()) {
                return Utils.assembleHTTPResponse(400, "Invalid group chat IDs.");
            }
    
            // Collect the emails for the group chat IDs
            List<String> emails = new ArrayList<>();
            for (int id : groupchatIds) {
                String otherEmail = dao.getGroupChatEmail(email, id);
                if (otherEmail != null && !otherEmail.isEmpty()) {
                    emails.add(otherEmail);
                }
            }
    
            // If no valid emails are found, return an error response
            if (emails.isEmpty()) {
                return Utils.assembleHTTPResponse(400, "No valid users found to create a group chat.");
            }
    
            // Log the collected emails (debugging)
            System.out.println("Collected Emails: " + emails);
    
            // Create a map to store the email data for insertion
            Map<String, String> insertData = new HashMap<>();
            int i;
            for (i = 1; i <= emails.size(); i++) {
                insertData.put("email" + i, emails.get(i - 1)); // Add other emails
            }
    
            // Add the current user's email as the last one
            insertData.put("email" + (i + 1), email);
    
            // Log the insert data (debugging)
            System.out.println("Insert Data: " + insertData);
    
            // Insert the group chat data into the database
            boolean isInserted = dao.insert(insertData, "GroupChats");
    
            // Check if the insert was successful
            if (isInserted) {
                return Utils.assembleHTTPResponse(200, "Group chat created successfully");
            } else {
                return Utils.assembleHTTPResponse(500, "Failed to insert data into the database.");
            }
    
        } catch (Exception e) {
            e.printStackTrace();
            return Utils.assembleHTTPResponse(500, "Failed to create group chat due to server error.");
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
