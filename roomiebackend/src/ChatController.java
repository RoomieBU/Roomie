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

    class CreateGroupChatRequest {
        private String token;
        private List<Integer> groupChatIds;
    
        public String getToken() {
            return token;
        }
    
        public void setToken(String token) {
            this.token = token;
        }
    
        public List<Integer> getGroupChatIds() {
            return groupChatIds;
        }
    
        public void setGroupChatIds(List<Integer> groupChatIds) {
            this.groupChatIds = groupChatIds;
        }
    }

    public static String createGroupChat(Map<String, String> data, String method) {
        ChatDao dao = new ChatDao(SQLConnection.getConnection());
    
        // Use Gson to parse the input data
        Gson gson = new Gson();
        CreateGroupChatRequest request = gson.fromJson(data.get("body"), CreateGroupChatRequest.class);
    
        String email = Auth.getEmailfromToken(request.getToken());
        List<Integer> groupchatIds = request.getGroupChatIds();
    
        try {
            if (groupchatIds == null || groupchatIds.isEmpty()) {
                return Utils.assembleHTTPResponse(400, "No group chat IDs provided.");
            }
    
            // Collect the emails from the groupchat IDs
            Set<String> emails = new LinkedHashSet<>(); // Use LinkedHashSet to maintain order and avoid duplicates
            for (int id : groupchatIds) {
                String otherEmail = dao.getGroupChatEmail(email, id);
                if (otherEmail != null && !otherEmail.equals(email)) {
                    emails.add(otherEmail);
                }
            }
    
            // Add current user's email
            emails.add(email);
    
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
    
            // Log insert data
            System.out.println("Insert Data: " + insertData);
    
            // Insert into the database
            boolean isInserted = dao.insert(insertData, "GroupChats");
    
            if (isInserted) {
                return Utils.assembleHTTPResponse(200, "Group chat created successfully.");
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
