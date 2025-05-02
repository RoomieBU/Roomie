package Database;

import Tools.Auth;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalendarEventDao extends Dao{

    public CalendarEventDao(Connection connection) {
        super(connection);
    }

    public boolean storeEvent(Map<String, String> data) {
        String token       = data.get("token");
        String eventDate   = data.get("eventDate");
        String eventTitle  = data.get("event");
        String email       = Auth.getEmailfromToken(token);

        int userId         = getUserId(email);
        int groupChatId    = getGroupChatId(email);
        String dbEvent     = eventTitle + "|" + userId;

        boolean exists = dateHasEvents(eventDate, groupChatId);

        String sql;
        if (exists) {
            // append to existing events
            String existing = getEvents(eventDate, groupChatId);
            String combined = existing + "," + dbEvent;
            sql = "UPDATE CalendarEvent SET events = ? WHERE event_date = ? AND group_chat_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, combined);
                ps.setDate(2, Date.valueOf(eventDate));
                ps.setInt(3, groupChatId);
                return ps.executeUpdate() == 1;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

        } else {
            // first event for this date → insert row
            sql = "INSERT INTO CalendarEvent (group_chat_id, event_date, events) VALUES (?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, groupChatId);
                ps.setDate(2, Date.valueOf(eventDate));
                ps.setString(3, dbEvent);
                return ps.executeUpdate() == 1;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public List<CalendarEvent> getAllUsersEvents(Map<String, String> data) {
        String token    = data.get("token");
        String email    = Auth.getEmailfromToken(token);
        int groupChatId = getGroupChatId(email);

        String sql = "SELECT calendar_id, group_chat_id, event_date, events "
                + "FROM CalendarEvent WHERE group_chat_id = ?";
        List<CalendarEvent> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, groupChatId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CalendarEvent ce = new CalendarEvent(
                            rs.getInt("calendar_id"),
                            rs.getInt("group_chat_id"),
                            rs.getDate("event_date").toLocalDate(),
                            rs.getString("events")
                    );
                    out.add(ce);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Return true if there is already a row for (date,group).
     */
    public boolean dateHasEvents(String date, int groupChatId) {
        String sql = "SELECT 1 FROM CalendarEvent WHERE event_date = ? AND group_chat_id = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setInt(2, groupChatId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * returns a string of the events
     * events are in the format: event|user_id, event2|user_id ...
     */
    public String getEvents(String date, int groupChatId) {
        String sql = "SELECT events FROM CalendarEvent WHERE event_date = ? AND group_chat_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setInt(2, groupChatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("events");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getUserId(String email) {
        // get user id
        int userId = -1;
        try {
            UserDao userDao = new UserDao(connection);
            userId = userDao.getUserByEmail(email).getUserId();
            userDao.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }

    public int getGroupChatId(String email) {
        // get group chat id
        int groupChatId = -1;
        try {
            ChatDao chatDao = new ChatDao(connection);
            List<GroupChat> gcList = chatDao.getConfirmedRoommates(email);
            groupChatId = gcList.get(0).getGroupchatId();
            chatDao.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupChatId;
    }
}
