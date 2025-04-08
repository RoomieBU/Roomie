package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlertDao extends Dao {

    public AlertDao(Connection conn) {
        super(conn);
    }

    public List<Alert> getAlerts(int roommateid) {
        String query = "SELECT * FROM Alert WHERE roommateid = ? ORDER BY start_time DESC";
        List<Alert> alerts = new ArrayList<Alert>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, roommateid);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Alert a = new Alert(
                            rs.getString("name"),
                            rs.getString("sender"),
                            rs.getString("description"),
                            roommateid,
                            rs.getString("start_time"),
                            rs.getString("end_time")
                    );
                    alerts.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alerts;
    }
}
