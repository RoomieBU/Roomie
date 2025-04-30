package Database;

import java.sql.*;
import java.util.*;

public class UserPreferencesDao extends Dao{
    private final Connection connection;

    public UserPreferencesDao(Connection connection) {
        super(connection);
        this.connection = connection;
    }

    public boolean createUserPreferences(Map<String, String> data, String email) {
        if (data.isEmpty()) return false;

        StringBuilder columns = new StringBuilder("email, ");
        StringBuilder values = new StringBuilder("?, ");
        StringBuilder updates = new StringBuilder();

        List<Object> params = new ArrayList<>();
        params.add(email);

        for (Map.Entry<String, String> entry : data.entrySet()) {
            String column = entry.getKey();
            String value = entry.getValue();

            if (column.equalsIgnoreCase("email")) continue;

            columns.append(column).append(", ");
            values.append("?, ");
            updates.append(column).append(" = VALUES(").append(column).append("), ");

            if (isTinyIntColumn(column)) {
                params.add(Boolean.parseBoolean(value));
            } else {
                params.add(value);
            }
        }

        columns.setLength(columns.length() - 2);
        values.setLength(values.length() - 2);
        updates.setLength(updates.length() - 2);

        String sql = "INSERT INTO UserPreferences (" + columns + ") VALUES (" + values + ") " +
                "ON DUPLICATE KEY UPDATE " + updates;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating UserPreferences: " + e.getMessage(), e);
        }

        return true;
    }

    private boolean isTinyIntColumn(String column) {
        return Set.of("pet_friendly", "smoke", "smoke_okay", "drugs", "drugs_okay")
                .contains(column);
    }

    public List<Map<String, Object>> getAllUserPreferences() {
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
        } catch (SQLException e) {
            return userPreferences;
        }
        return userPreferences;
    }


    public Map<String, Object> getUserPreferencesByEmail(String email) {
        Map<String, Object> userPreferences = new HashMap<>();
        String query = "SELECT * FROM UserPreferences WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userPreferences.put("email", rs.getString("email"));
                    userPreferences.put("pet_friendly", rs.getBoolean("pet_friendly"));
                    userPreferences.put("smoke", rs.getBoolean("smoke"));
                    userPreferences.put("smoke_okay", rs.getBoolean("smoke_okay"));
                    userPreferences.put("drugs", rs.getBoolean("drugs"));
                    userPreferences.put("drugs_okay", rs.getBoolean("drugs_okay"));
                    userPreferences.put("morning_person", rs.getInt("morning_person"));
                    userPreferences.put("night_owl", rs.getInt("night_owl"));
                    userPreferences.put("introvert", rs.getInt("introvert"));
                    userPreferences.put("extrovert", rs.getInt("extrovert"));
                    userPreferences.put("guests_often", rs.getInt("guests_often"));
                    userPreferences.put("okay_with_guests", rs.getInt("okay_with_guests"));
                    userPreferences.put("prefer_quiet", rs.getInt("prefer_quiet"));
                    userPreferences.put("neat_tidy", rs.getInt("neat_tidy"));
                    userPreferences.put("okay_with_mess", rs.getInt("okay_with_mess"));
                    userPreferences.put("out_late", rs.getInt("out_late"));
                    userPreferences.put("play_instruments", rs.getInt("play_instruments"));
                    userPreferences.put("gamer", rs.getInt("gamer"));
                    userPreferences.put("gender", rs.getString("gender"));
                    userPreferences.put("preferred_gender", rs.getString("preferred_gender"));
                }
            }
        } catch (SQLException e) {
            return new HashMap<>();
        }
        return userPreferences;
    }

}
