package Controller;

import Database.Dao;
import Database.SQLConnection;
import Tools.Auth;
import Tools.HTTPResponse;

import java.util.Map;

public class AlertController {

    public static String createAlert(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();

        Dao dao = new Dao(SQLConnection.getConnection());
        String email = Auth.getEmailfromToken(data.get("token"));

        data.remove("token");
        data.put("sender", email);
        dao.insert(data, "Alert");
        response.code = 200;
        return response.toString();
    }

    public static String addAlertReaction(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();

        String email = Auth.getEmailfromToken(data.get("token"));
        data.remove("token");
        data.put("sender", email);

        Dao dao = new Dao(SQLConnection.getConnection());
        dao.insert(data, "AlertReaction");

        response.code = 200;
        return response.toString();
    }

}
