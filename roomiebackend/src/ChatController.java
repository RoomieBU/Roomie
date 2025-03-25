import Database.ChatDao;
import Database.Dao;
import Database.SQLConnection;
import Tools.Auth;
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
        if (!method.equals("POST")) {
            return Utils.assembleHTTPResponse(405, "{\"message\": \"Method Not Allowed\"}");
        }
        int code = 400;


        try{
            ChatDao dao = new ChatDao(SQLConnection.getConnection());

            Map<String, String> insertData = new HashMap<>();

            /**
             * From the front end we will get...
             * token
             * groupchat_id
             * message
             */
            String email = Auth.getEmailfromToken(data.get("token"));
            insertData.put("sender_email", email);
            insertData.put("groupchat_id", data.get("groupchat_id"));
            insertData.put("message", data.get("message"));

            dao.insert(insertData, "ChatHistory");

            code = 200;
            return Utils.assembleHTTPResponse(code, Utils.assembleJson(Map.of("message", "message sent")));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return Utils.assembleHTTPResponse(400, Utils.assembleJson(Map.of("message", "message not sent")));
        }
    }
    
    public static String sendChatHistory(Map<String, String> data, String method) {
        if (!method.equals("POST")) {
            return Utils.assembleHTTPResponse(405, "{\"message\": \"Method Not Allowed\"}");
        }
        int code = 400;
        HashMap<String, String> response = new HashMap<>();

        try {
            ChatDao dao = new ChatDao(SQLConnection.getConnection());

            /**
             * token
             * groupchat id
             */
            String email = Auth.getEmailfromToken(data.get("token"));
            int groupId = Integer.parseInt(data.get("groupchat_id"));
            List<Message> messageList = dao.getChatHistory(groupId);
            for (Message m : messageList) {
                if (m.getSenderEmail().equals(email)) {
                    m.selfSent();
                }
            }
            Gson gson = new Gson();
            return Utils.assembleHTTPResponse(code, gson.toJson(messageList));
        } catch (SQLException | ClassNotFoundException e) {
            code = 500;
        }
        Gson gson = new Gson();
        return Utils.assembleHTTPResponse(400, Utils.assembleJson(Map.of("message", "message not sent")));
    }
}
