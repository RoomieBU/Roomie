package Database;

import java.sql.*;
import java.util.*;

public class UserPreferencesDao {
    private final Connection connection;

    public UserPreferencesDao(Connection connection) {
        this.connection = connection;
    }

    public boolean createUserPreferences(Map<String, String> data, String email) throws SQLException {
        if (data.isEmpty()) return false; // No data, nothing to insert

        // Start building the SQL query dynamically
        StringBuilder columns = new StringBuilder("email, ");
        StringBuilder values = new StringBuilder("?, ");
        StringBuilder updates = new StringBuilder();

        List<Object> params = new ArrayList<>();
        params.add(email); // First parameter is the email

        for (Map.Entry<String, String> entry : data.entrySet()) {
            String column = entry.getKey();
            String value = entry.getValue();

            // Skip email since it's already added
            if (column.equalsIgnoreCase("email")) continue;

            columns.append(column).append(", ");
            values.append("?, ");
            updates.append(column).append(" = VALUES(").append(column).append("), ");

            // Handle TINYINT(1) values (boolean)
            if (isTinyIntColumn(column)) {
                params.add(Boolean.parseBoolean(value)); // Convert to boolean
            } else {
                params.add(value); // Store as-is
            }
        }

        // Remove trailing commas
        columns.setLength(columns.length() - 2);
        values.setLength(values.length() - 2);
        updates.setLength(updates.length() - 2);

        // Construct final query
        String sql = "INSERT INTO UserPreferences (" + columns + ") VALUES (" + values + ") " +
                "ON DUPLICATE KEY UPDATE " + updates;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i)); // Set parameters dynamically
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating UserPreferences: " + e.getMessage(), e);
        }

        return true;
    }

    // Helper method to check if a column is a TINYINT(1)
    private boolean isTinyIntColumn(String column) {
        return Set.of("pet_friendly", "smoke", "smoke_okay", "drugs", "drugs_okay")
                .contains(column);
    }


    public List<Map<String, Object>> getAllUserPreferences() throws SQLException {
        List<Map<String, Object>> userPreferences = new ArrayList<>();
        String query = "SELECT * FROM UserPreferences";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> prefs = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    prefs.put(metaData.getColumnName(i), rs.getObject(i));
                }
                userPreferences.add(prefs);
            }
        }
        return userPreferences;
    }

}
