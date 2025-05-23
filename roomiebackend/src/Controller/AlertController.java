package Controller;

import Database.*;
import Tools.Auth;
import Tools.HTTPResponse;
import Tools.Utils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.smartcardio.ResponseAPDU;
import java.sql.Timestamp;

public class AlertController {

    public static String createAlert(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();

        Dao dao = new Dao(SQLConnection.getConnection());
        String email = Auth.getEmailfromToken(data.get("token"));

        data.remove("token");
        data.put("sender", email);

        // Convert start_time and end_time to Timestamp
        try {
            if (data.containsKey("start_time")) {
                data.put("start_time", Timestamp.valueOf(data.get("start_time")).toString());
            }
            if (data.containsKey("end_time")) {
                data.put("end_time", Timestamp.valueOf(data.get("end_time")).toString());
            }
        } catch (IllegalArgumentException e) {
            response.code = 400;
            response.setMessage("Invalid date format", "Ensure 'start_time' and 'end_time' are in the correct format.");
            return response.toString();
        }

        // Log the raw request body
        System.out.println("Raw Request Body: " + data.toString());
        
        dao.insert(data, "Alert");
        response.code = 200;
        return response.toString();
    }

    public static String updateAlertStatus(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
    
        AlertDao alertDao = new AlertDao(SQLConnection.getConnection());
    
        try {
            int id = Integer.parseInt(data.get("id"));  // Parsing ID
            boolean status = Boolean.parseBoolean(data.get("status"));  // Parsing the status
    
            // If setAlertStatus returns true, the status was updated
            if (alertDao.setAlertStatus(status, id)) {
                response.code = 200;
                response.setMessage("success", "Alert status updated successfully.");  // Use setMessage here
            } else {
                response.code = 400;
                response.setMessage("setting status fail", "Failed to update alert status. Check if the alert exists and try again.");  // Use setMessage here
            }
        } catch (NumberFormatException e) {
            response.code = 400;
            response.setMessage("Number Fail", "Invalid ID format. Ensure 'id' is a valid integer.");  // Use setMessage here
        } catch (Exception e) {
            response.code = 500;
            response.setMessage("FAIL", "An unexpected error occurred: " + e.getMessage());  // Use setMessage here
        }
    
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
