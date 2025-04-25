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
            // Log the values
            System.out.println("Updating alert with ID " + id + " to status " + status);
    
            // Check if the id exists in the database
            String selectQuery = "SELECT * FROM Alert WHERE id = ?";
            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, id);
                ResultSet rs = selectStmt.executeQuery();
                if (!rs.next()) {
                    System.out.println("No alert found with the given id: " + id);
                    return false;
                }
            }
    
            // Set the boolean status to either 1 or 0 based on the status
            stmt.setInt(1, status ? 1 : 0);  // Convert Boolean to 1/0
            stmt.setInt(2, id);
    
            int rowsAffected = stmt.executeUpdate();
    
            if (rowsAffected > 0) {
                connection.commit(); // Commit the transaction if not using auto-commit
                System.out.println("Alert updated successfully.");
                return true;
            } else {
                System.out.println("No records updated. Check if the id exists or if the status is already set.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
