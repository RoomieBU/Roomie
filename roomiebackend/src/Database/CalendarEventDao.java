package Database;

import Tools.Auth;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalendarEventDao extends Dao {

    public CalendarEventDao(Connection connection) {
        super(connection);
        System.out.println("[CalendarEventDao] Initialized with connection: " + connection);
    }

    public boolean storeEvent(Map<String, String> data) {
        System.out.println("[storeEvent] Input data: " + data);
        String token       = data.get("token");
        String eventDate   = data.get("eventDate");
        String eventTitle  = data.get("event");
        String email       = Auth.getEmailfromToken(token);
        System.out.println("[storeEvent] Resolved email: " + email);

        int userId         = getUserId(email);
        int groupChatId    = getGroupChatId(email);
        System.out.println("[storeEvent] userId=" + userId + ", groupChatId=" + groupChatId);
        String dbEvent     = eventTitle + "|" + userId;

        boolean exists = dateHasEvents(eventDate, groupChatId);
        System.out.println("[storeEvent] dateHasEvents(" + eventDate + "," + groupChatId + ") returned " + exists);

        String sql;
        if (exists) {
            String existing = getEvents(eventDate, groupChatId);
            System.out.println("[storeEvent] Existing events: " + existing);
            String combined = existing + "," + dbEvent;
            sql = "UPDATE CalendarEvent SET events = ? WHERE event_date = ? AND group_chat_id = ?";
            System.out.println("[storeEvent] Executing SQL: " + sql + ", combined=" + combined);
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, combined);
                ps.setDate(2, Date.valueOf(eventDate));
                ps.setInt(3, groupChatId);
                int updated = ps.executeUpdate();
                System.out.println("[storeEvent] UPDATE affected rows: " + updated);
                return updated == 1;
            } catch (SQLException e) {
                System.err.println("[storeEvent] SQLException on UPDATE: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } else {
            sql = "INSERT INTO CalendarEvent (group_chat_id, event_date, events) VALUES (?, ?, ?)";
            System.out.println("[storeEvent] Executing SQL: " + sql + ", groupChatId=" + groupChatId + ", date=" + eventDate + ", dbEvent=" + dbEvent);
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, groupChatId);
                ps.setDate(2, Date.valueOf(eventDate));
                ps.setString(3, dbEvent);
                int inserted = ps.executeUpdate();
                System.out.println("[storeEvent] INSERT affected rows: " + inserted);
                return inserted == 1;
            } catch (SQLException e) {
                System.err.println("[storeEvent] SQLException on INSERT: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
    }

    public List<CalendarEvent> getAllUsersEvents(Map<String, String> data) {
        System.out.println("[getAllUsersEvents] Input data: " + data);
        String token    = data.get("token");
        String email    = Auth.getEmailfromToken(token);
        int groupChatId = getGroupChatId(email);
        System.out.println("[getAllUsersEvents] email=" + email + ", groupChatId=" + groupChatId);

        String sql = "SELECT calendar_id, group_chat_id, event_date, events FROM CalendarEvent WHERE group_chat_id = ?";
        System.out.println("[getAllUsersEvents] Executing SQL: " + sql + ", groupChatId=" + groupChatId);
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
                System.out.println("[getAllUsersEvents] Retrieved " + out.size() + " events");
            }
        } catch (SQLException e) {
            System.err.println("[getAllUsersEvents] SQLException: " + e.getMessage());
            e.printStackTrace();
        }
        return out;
    }

    public boolean dateHasEvents(String date, int groupChatId) {
        System.out.println("[dateHasEvents] Checking date=" + date + ", groupChatId=" + groupChatId);
        String sql = "SELECT 1 FROM CalendarEvent WHERE event_date = ? AND group_chat_id = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setInt(2, groupChatId);
            try (ResultSet rs = ps.executeQuery()) {
                boolean exists = rs.next();
                System.out.println("[dateHasEvents] exists=" + exists);
                return exists;
            }
        } catch (SQLException e) {
            System.err.println("[dateHasEvents] SQLException: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String getEvents(String date, int groupChatId) {
        System.out.println("[getEvents] date=" + date + ", groupChatId=" + groupChatId);
        String sql = "SELECT events FROM CalendarEvent WHERE event_date = ? AND group_chat_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setInt(2, groupChatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String events = rs.getString("events");
                    System.out.println("[getEvents] Found events: " + events);
                    return events;
                }
            }
        } catch (SQLException e) {
            System.err.println("[getEvents] SQLException: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("[getEvents] No events found, returning empty string");
        return "";
    }

    public int getUserId(String email) {
        System.out.println("[getUserId] email=" + email);
        int userId = -1;
        try {
            UserDao userDao = new UserDao(connection);
            userId = userDao.getUserByEmail(email).getUserId();
            userDao.closeConnection();
            System.out.println("[getUserId] Resolved userId=" + userId);
        } catch (SQLException e) {
            System.err.println("[getUserId] SQLException: " + e.getMessage());
            e.printStackTrace();
        }
        return userId;
    }

    public int getGroupChatId(String email) {
        System.out.println("[getGroupChatId] email=" + email);
        int groupChatId = -1;
        try {
            ChatDao chatDao = new ChatDao(connection);
            List<GroupChat> gcList = chatDao.getConfirmedRoommates(email);
            groupChatId = gcList.get(0).getGroupchatId();
            chatDao.closeConnection();
            System.out.println("[getGroupChatId] Resolved groupChatId=" + groupChatId);
        } catch (SQLException e) {
            System.err.println("[getGroupChatId] SQLException: " + e.getMessage());
            e.printStackTrace();
        }
        return groupChatId;
    }
}