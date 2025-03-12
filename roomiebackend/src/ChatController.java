import Database.Dao;
import Database.SQLConnection;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ChatController {

    public static String sendMessage(Map<String, String> data, String method) {
        if (!method.equals("POST")) {
            return Utils.assembleHTTPResponse(405, "{\"message\": \"Method Not Allowed\"}");
        }
        int code = 400;


        try{
            Dao dao = new Dao(SQLConnection.getConnection());

            Map<String, String> insertData = new HashMap<>();

            //dao.insert();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
    }

}
