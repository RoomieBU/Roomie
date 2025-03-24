package Database;

import java.sql.*;
import java.util.*;

/**
 * Consolidated DAO class.
 * Contains all methods needed for inserting, retrieving, setting, and deleting data
 * from a specified table.
 * Allows flexibility in retrieving and setting data fields of different data types.
 */
public class Dao {
    Connection connection;


    public Dao(Connection connection) throws SQLException {
        this.connection = connection;
    }

    /**
     * Method for inserting a row into a table, given the data as a map and table name.
     *
     * @param data
     * @param table
     * @return
     */
    public boolean insert(Map<String, String> data, String table) {
        if (data == null || data.isEmpty()) {
            return false;
        }

        StringBuilder base = new StringBuilder("INSERT INTO " + table);
        String columns = String.join(", ", data.keySet());
        String placeholders = String.join(", ", data.keySet().stream().map(k -> "?").toList());

        String sql = base.append(" (").append(columns).append(") VALUES (").append(placeholders).append(")").toString();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int index = 1;
            for (Object value : data.values()) {
                stmt.setObject(index++, value);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            //throw new RuntimeException("Error updating info: ", e);
            return false;
        }
    }

    /**
     * Generic method for setting data within a table, given the email is the primary key
     * or unique identifier for a record.
     *
     * @param data
     * @param email
     * @return
     */
    public boolean set(Map<String, String> data, String email, String table) {
        if (data.isEmpty()) return false;

        StringBuilder query = new StringBuilder("UPDATE " + table + " SET ");
        int count = 0;

        for (String key : data.keySet()) {
            query.append(key).append(" = ?");
            if (++count < data.size()) {
                query.append(", ");
            }
        }

        query.append(" WHERE email = ?");

        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int index = 1;

            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if ("registered".equals(key)) {
                    // Convert "true"/"false" string to boolean for BIT(1)
                    boolean registeredValue = "true".equalsIgnoreCase(value);
                    stmt.setBoolean(index++, registeredValue);
                } else {
                    stmt.setString(index++, value);
                }
            }

            stmt.setString(index, email);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating info: ", e);
        }
    }

    /**
     * Generic method for setting data within a table, given an int (id) is the primary key
     * or unique identifier for a record.
     *
     * @param data
     * @return
     */
    public boolean set(Map<String, String> data, int id, String table) {
        if (data.isEmpty()) return false;

        StringBuilder query = new StringBuilder("UPDATE " + table + " SET ");
        int count = 0;

        for (String key : data.keySet()) {
            query.append(key).append(" = ?");
            if (++count < data.size()) {
                query.append(", ");
            }
        }

        query.append(" WHERE id = ?");

        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int index = 1;

            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if ("registered".equals(key)) {
                    // Convert "true"/"false" string to boolean for BIT(1)
                    boolean registeredValue = "true".equalsIgnoreCase(value);
                    stmt.setBoolean(index++, registeredValue);
                } else {
                    stmt.setString(index++, value);
                }
            }

            stmt.setInt(index, id);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating info: ", e);
        }
    }

    /**
     * Method for getting data from a record where an email is the unique identifier.
     *
     * @param columns Fields to be retrieved
     * @param email Unique email for specific user
     * @return A Map of the columns as the keys and their values as the values
     */
    public Map<String, String> getData(List<String> columns, String email, String table) {
        Map<String, String> data = new HashMap<>();
        String query = "SELECT " + String.join(", ", columns) + " FROM " + table + " WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    for (String col : columns) {
                        if (col.equals("registered")) {
                            data.put(col, Integer.toString(rs.getInt(col))); // Store as "0" or "1"
                        } else {
                            data.put(col, rs.getString(col));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving info: ", e);
        }

        return data;
    }

    /**
     * Method for getting data from a record where an id is the unique identifier.
     *
     * @param columns Fields to be retrieved
     * @param id Unique id for record
     * @return A Map of the columns as the keys and their values as the values
     */
    public Map<String, String> get(List<String> columns, int id, String table) {
        Map<String, String> data = new HashMap<>();
        String query = "SELECT " + String.join(", ", columns) + " FROM " + table + " WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    for (String col : columns) {
                        if (col.equals("registered")) {
                            data.put(col, Integer.toString(rs.getInt(col))); // Store as "0" or "1"
                        } else {
                            data.put(col, rs.getString(col));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving info: ", e);
        }

        return data;
    }

    /**
     * Method for getting data from a given table, where the email is the unique identifier
     *
     * @param columns Fields to be retrieved
     * @param email Unique email for specific user
     * @return A Map of the columns as the keys and their values as the values
     */
    public Map<String, String> get(List<String> columns, String email, String table) {
        Map<String, String> data = new HashMap<>();
        String query = "SELECT " + String.join(", ", columns) + " FROM " + table + " WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    for (String col : columns) {
                        if (col.equals("registered")) {
                            data.put(col, Integer.toString(rs.getInt(col))); // Store as "0" or "1"
                        } else {
                            data.put(col, rs.getString(col));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving info: ", e);
        }

        return data;
    }

    /**
     * Checks if at least one record exists with a given map of data. Aims to replace
     * Dao methods similar to UserDao.isUserLogin
     *
     * @return
     */
    public boolean exists(Map<String, String> data, String table) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Data map cannot be empty");
        }

        StringBuilder select = new StringBuilder("SELECT * FROM " + table + " WHERE ");
        String[] keys = data.keySet().toArray(new String[0]);
        select.append(String.join(" AND ", // scary
                Arrays.stream(keys).map(k -> k + " = ?").toArray(String[]::new)));

        try (PreparedStatement stmt = connection.prepareStatement(select.toString())) {
            int index = 1;
            for (String key : keys) {
                stmt.setString(index++, data.get(key));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying for record", e);
        }
    }

    /**
     * Removes a record from a given table where the email is the unique identifier
     * @param email
     * @param table
     */
    public void remove(String email, String table) {
        String query = "DELETE FROM " + table + " WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing record", e);
        }
    }

    /**
     * Removes a record from a given table where the id is the unique identifier
     * @param id
     * @param table
     */
    public void remove(int id, String table) {
        String query = "DELETE FROM " + table + " WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing record", e);
        }
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }
}
