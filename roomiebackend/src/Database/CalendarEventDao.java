package Database;

import Tools.Auth;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CalendarEventDao extends Dao {

    public CalendarEventDao(Connection connection) {
        super(connection);
    }

    public boolean storeEvent(Map<String, String> data) {
        String token = data.get("token");
        String eventDate = data.get("eventDate");
        String eventTitle = data.get("event");
        String email = Auth.getEmailfromToken(token);

        int userId = getUserId(email);
        int groupChatId = getGroupChatId(email);
        String dbEvent = eventTitle + "|" + userId;

        boolean exists = dateHasEvents(eventDate, groupChatId);
        String sql;

        if (exists) {
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

        String sql = "SELECT calendar_id, group_chat_id, event_date, events FROM CalendarEvent WHERE group_chat_id = ?";
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
        int userId = -1;
        try {
            UserDao userDao = new UserDao(connection);
            userId = userDao.getUserByEmail(email).getUserId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userId;
    }

    public int getGroupChatId(String email) {
        String query = "SELECT * FROM GroupChats WHERE (email1 = ? OR email2 = ? OR email3 = ? OR email4 = ? OR email5 = ? OR email6 = ?) AND confirmed = 1";
        int id = -1;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 1; i <= 6; i++) stmt.setString(i, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) id = rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    // in CalendarEventDao.java
    public boolean deleteEvent(Map<String,String> data) {
        System.out.println("[deleteEvent] Input data: " + data);
        String token = data.get("token");
        String date  = data.get("eventDate");
        String title = data.get("event");
        System.out.println("[deleteEvent] token=" + token + ", date=" + date + ", title=" + title);
        String email = Auth.getEmailfromToken(token);
        int userId = getUserId(email);
        int gcId = getGroupChatId(email);
        System.out.println("[deleteEvent] userId=" + userId + ", groupChatId=" + gcId);
        String cell = title + "|" + userId;

        String current = getEvents(date, gcId);
        System.out.println("[deleteEvent] current events CSV: " + current);
        List<String> parts = new ArrayList<>(Arrays.asList(current.split(",")));
        if (!parts.remove(cell)) {
            System.out.println("[deleteEvent] cell not found: " + cell);
            return false;
        }

        if (parts.isEmpty()) {
            String sql = "DELETE FROM CalendarEvent WHERE event_date=? AND group_chat_id=?";
            try (PreparedStatement ps=connection.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(date)); ps.setInt(2, gcId);
                int deleted = ps.executeUpdate();
                System.out.println("[deleteEvent] rows deleted: " + deleted);
                return deleted == 1;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String updated = String.join(",", parts);
            System.out.println("[deleteEvent] updated events CSV: " + updated);
            String sql = "UPDATE CalendarEvent SET events=? WHERE event_date=? AND group_chat_id=?";
            try (PreparedStatement ps=connection.prepareStatement(sql)) {
                ps.setString(1, updated);
                ps.setDate(2, Date.valueOf(date));
                ps.setInt(3, gcId);
                int updatedRows = ps.executeUpdate();
                System.out.println("[deleteEvent] rows updated: " + updatedRows);
                return updatedRows == 1;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
