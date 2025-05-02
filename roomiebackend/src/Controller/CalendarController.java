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

public class CalendarEventController {

    /**
     * POST /calendar/getEvents
     * Expects JSON body with { "token": "..."}
     * Returns HTTPResponse with code 200 and JSON array of:
     *   [ { "calendarId":…, "groupChatId":…, "eventDate":"YYYY-MM-DD", "events":"…" }, … ]
     */
    public static String getAllEvents(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();

        // authenticate
        String token = data.get("token");
        String email = Auth.getEmailfromToken(token);
        if (email == null) {
            response.setMessage("message", "Invalid token");
            return response.toString();
        }

        CalendarEventDao dao = new CalendarEventDao(SQLConnection.getConnection());
        List<CalendarEvent> list = dao.getAllUsersEvents(data);

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
        return Utils.assembleHTTPResponse(200, json);
    }

    /**
     * POST /calendar/addEvent
     * Expects JSON body with { "token":"…", "eventDate":"YYYY-MM-DD", "event":"Event text" }
     * Returns HTTPResponse code 200 on success, 400 on failure.
     */
    public static String addEvent(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();

        // authenticate
        String token = data.get("token");
        String email = Auth.getEmailfromToken(token);
        if (email == null) {
            response.setMessage("message", "Invalid token");
            return response.toString();
        }

        // validate inputs
        String date  = data.get("eventDate");
        String event = data.get("event");
        if (date == null || event == null) {
            response.setMessage("message", "Missing eventDate or event");
            return response.toString();
        }

        CalendarEventDao dao = new CalendarEventDao(SQLConnection.getConnection());
        boolean ok = dao.storeEvent(data);

        if (ok) {
            response.code = 200;
            response.setMessage("message", "Event saved");
        } else {
            response.code = 400;
            response.setMessage("message", "Failed to save event");
        }
        return response.toString();
    }
}
