import Controller.MatchController;
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

import Controller.ProfileController;
import Database.ChatInformation;
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
    
        System.out.println("Received data: " + data.toString());
        List<Integer> groupchatIds = new ArrayList<>();
    
        try {
            if (data.get("groupChatId0") != null) {
                groupchatIds.add(Integer.parseInt(data.get("groupChatId0")));
            }
            if (data.get("groupChatId1") != null) {
                groupchatIds.add(Integer.parseInt(data.get("groupChatId1")));
            }
            if (data.get("groupChatId2") != null) {
                groupchatIds.add(Integer.parseInt(data.get("groupChatId2")));
            }
            if (data.get("groupChatId3") != null) {
                groupchatIds.add(Integer.parseInt(data.get("groupChatId3")));
            }
            if (data.get("groupChatId4") != null) {
                groupchatIds.add(Integer.parseInt(data.get("groupChatId4")));
            }
            if (data.get("groupChatId5") != null) {
                groupchatIds.add(Integer.parseInt(data.get("groupChatId5")));
            }
        } catch (Exception e) {
            // There's nothing to do here
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
    
        // Prepare data map for checking if the group chat already exists
        Map<String, String> existsData = new HashMap<>();
        int i = 1;
        for (String e : emails) {
            existsData.put("email" + i, e);
            i++;
        }
    
        // Fill remaining email fields with null
        while (i <= 6) {
            existsData.put("email" + i, null);
            i++;
        }
    
        // Check if the group chat already exists
        if (dao.exists(existsData, "GroupChats")) {
            return Utils.assembleHTTPResponse(400, "A group chat with these emails already exists.");
        }
    
        // Prepare data for insertion
        Map<String, String> insertData = new HashMap<>();
        i = 1;
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

        dao.storeMessage(insertData);

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

    // send all user info
    public static String sendAllUserInformation(Map<String, String> data, String method) {
        ChatDao dao = new ChatDao(SQLConnection.getConnection());
        UserDao userDao = new UserDao(SQLConnection.getConnection());

        int groupId = Integer.parseInt(data.get("groupchat_id"));
        String email = Auth.getEmailfromToken(data.get("token"));
        List<String> emails = dao.getGroupChatEmails(email, groupId);

        List<ChatInformation> informationList = new ArrayList<>();
        for(String e : emails) {
            informationList.add(userDao.getChatInformation(e));
        }

        Gson gson = new Gson();

        return Utils.assembleHTTPResponse(200, gson.toJson(informationList));
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

    public static String sendConfirmedRoommates(Map<String, String> data, String method) {
        ChatDao dao = new ChatDao(SQLConnection.getConnection());

        String email = Auth.getEmailfromToken(data.get("token"));
        List<GroupChat> groupChats = dao.getConfirmedRoommates(email);
        Gson gson = new Gson();

        return Utils.assembleHTTPResponse(200, gson.toJson(groupChats));
    }
}
