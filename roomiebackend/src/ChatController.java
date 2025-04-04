import Database.ChatDao;
import Database.Dao;
import Database.GroupChat;
import Database.SQLConnection;
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
