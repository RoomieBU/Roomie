package Controller;

import Database.*;
import Tools.Auth;
import Tools.HTTPResponse;
import Tools.Utils;
import com.google.gson.Gson;

import java.util.List;
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

    public static String getAllAlerts(Map<String, String> data, String method) {
        AlertDao dao = new AlertDao(SQLConnection.getConnection());
        List<Alert> alerts = dao.getAlerts(Integer.parseInt(data.get("groupchat_id")));

        Gson gson = new Gson();
        return Utils.assembleHTTPResponse(200, gson.toJson(alerts));
    }

    public static String getAllAlertResponses(Map<String, String> data, String method) {
        AlertDao dao = new AlertDao(SQLConnection.getConnection());
        List<AlertResponse> responses = dao.getAlertResponses(Integer.parseInt(data.get("alert_id")));

        Gson gson = new Gson();
        return Utils.assembleHTTPResponse(200, gson.toJson(responses));
    }
}
