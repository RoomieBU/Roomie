package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlertDao extends Dao {

    public AlertDao(Connection conn) {
        super(conn);
    }

    public List<Alert> getAlerts(int groupchatId) {
        String query = "SELECT * FROM Alert WHERE groupchat_id = ? ORDER BY start_time DESC";
        List<Alert> alerts = new ArrayList<Alert>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, groupchatId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Alert a = new Alert(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("sender"),
                            rs.getString("description"),
                            groupchatId,
                            rs.getString("start_time"),
                            rs.getString("end_time"),
                            rs.getBoolean("complete")
                    );
                    alerts.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alerts;
    }

    public List<AlertResponse> getAlertResponses(int alert_id) {
        String query = "SELECT * FROM AlertResponse WHERE alert_id = ?";
        List<AlertResponse> responses = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, alert_id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AlertResponse r = new AlertResponse(
                            rs.getInt("id"),
                            alert_id,
                            rs.getString("sender"),
                            rs.getString("comment"),
                            rs.getInt("reaction")
                    );
                    responses.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return responses;
    }


    public boolean setAlertStatus(Boolean status, int id) {

        String query = "UPDATE Alert SET complete = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, status);
            stmt.setInt(2, id);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
