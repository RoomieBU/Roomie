package Controller;

import Database.CalendarEvent;
import Database.CalendarEventDao;
import Database.SQLConnection;
import Tools.Auth;
import Tools.HTTPResponse;
import Tools.Utils;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalendarController {

    /**
     * POST /calendar/getEvents
     * Expects JSON body with { "token": "..."}
     * Returns HTTPResponse with code 200 and JSON array of:
     *   [ { "calendarId":…, "groupChatId":…, "eventDate":"YYYY-MM-DD", "events":"…" }, … ]
     */
    public static String getAllEvents(Map<String, String> data, String method) {
        System.out.println("Called getAllEvents with method: " + method);
        System.out.println("Incoming data: " + data);

        HTTPResponse response = new HTTPResponse();

        // authenticate
        String token = data.get("token");
        System.out.println("Extracted token: " + token);
        String email = Auth.getEmailfromToken(token);
        System.out.println("Authenticated email: " + email);
        if (email == null) {
            System.out.println("Authentication failed.");
            response.setMessage("message", "Invalid token");
            return response.toString();
        }

        CalendarEventDao dao = new CalendarEventDao(SQLConnection.getConnection());
        List<CalendarEvent> list = dao.getAllUsersEvents(data);
        System.out.println("Fetched " + list.size() + " calendar events.");

        // convert to simple DTOs / maps
        List<Map<String, String>> out = list.stream()
                .map(ev -> Map.of(
                        "calendarId",    Integer.toString(ev.getCalendarId()),
                        "groupChatId",   Integer.toString(ev.getGroupChatId()),
                        "eventDate",     ev.getEventDate().toString(),
                        "events",        ev.getEvents()
                ))
                .collect(Collectors.toList());

        String json = new Gson().toJson(out);
        System.out.println("Returning JSON: " + json);
        return Utils.assembleHTTPResponse(200, json);
    }

    /**
     * POST /calendar/addEvent
     * Expects JSON body with { "token":"…", "eventDate":"YYYY-MM-DD", "event":"Event text" }
     * Returns HTTPResponse code 200 on success, 400 on failure.
     */
    public static String addEvent(Map<String, String> data, String method) {
        System.out.println("Called addEvent with method: " + method);
        System.out.println("Incoming data: " + data);

        HTTPResponse response = new HTTPResponse();

        // authenticate
        String token = data.get("token");
        System.out.println("Extracted token: " + token);
        String email = Auth.getEmailfromToken(token);
        System.out.println("Authenticated email: " + email);
        if (email == null) {
            System.out.println("Authentication failed.");
            response.setMessage("message", "Invalid token");
            return response.toString();
        }

        // validate inputs
        String date  = data.get("eventDate");
        String event = data.get("event");
        System.out.println("Parsed eventDate: " + date + ", event: " + event);
        if (date == null || event == null) {
            System.out.println("Validation failed: missing date or event.");
            response.setMessage("message", "Missing eventDate or event");
            return response.toString();
        }

        CalendarEventDao dao = new CalendarEventDao(SQLConnection.getConnection());
        boolean ok = dao.storeEvent(data);
        System.out.println("storeEvent returned: " + ok);

        if (ok) {
            response.code = 200;
            response.setMessage("message", "Event saved");
        } else {
            response.code = 400;
            response.setMessage("message", "Failed to save event");
        }
        return response.toString();
    }

    public static String deleteEvent(Map<String,String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        String token = data.get("token"), email = Auth.getEmailfromToken(token);
        if (email==null) {
            response.setMessage("message","Invalid token"); return response.toString();
        }
        String date = data.get("eventDate"), ev = data.get("event");
        if (date==null||ev==null) {
            response.setMessage("message","Missing eventDate or event"); response.code=400; return response.toString();
        }
        CalendarEventDao dao = new CalendarEventDao(SQLConnection.getConnection());
        boolean ok = dao.deleteEvent(data);
        response.code = ok?200:400;
        response.setMessage("message", ok?"Deleted":"Failed to delete");
        return response.toString();
    }

}
