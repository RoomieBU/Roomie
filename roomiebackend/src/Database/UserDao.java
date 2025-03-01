package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User Data Access Object
 *  interface for the User Table
 *
 *  Work in progress, add methods as needed
 */
public class UserDao {
    private Connection connection;

    /**
     * Takes the connection given to connect to the database
     */
    public UserDao(Connection connection) throws SQLException {
        this.connection = connection;
    }

    /**
     * Creates a user from the given params
     */
    public void createUser(String username, String email, String hashedPassword, String firstName, String lastName, String aboutMe, Date dob) {
        String query = "INSERT INTO Users (username, email, hashed_password, first_name, last_name, about_me, date_of_birth) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, firstName);
            stmt.setString(5, lastName);
            stmt.setString(6, aboutMe);
            stmt.setDate(7, dob);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inserts a new user into the database with just username and email.
     * @param email
     * @param hashedPassword
     */
    public boolean createUser(String email, String hashedPassword) {
        String query = "INSERT INTO Users (email, hashed_password) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, email);
            stmt.setString(2, hashedPassword);

            stmt.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean updateUserInfo(String username, String email, String first_name, String last_name, String about_me, String DOB) {
        String query = "UPDATE Users SET username = ?, first_name = ?, last_name = ?, about_me = ?, date_of_birth = ?, registered = 1 WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, first_name);
            stmt.setString(3, last_name);
            stmt.setString(4, about_me);
            stmt.setString(5, DOB);
            stmt.setString(6, email);

            stmt.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns a list of all users from the database
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, email, first_name, last_name, about_me, date_of_birth, created_at, registered FROM Users";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("about_me"),
                        rs.getDate("date_of_birth"),
                        rs.getTimestamp("created_at"),
                        rs.getBoolean("registered")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving users", e);
        }

        return users;
    }


    /**
     * Returns a user from the database based on their user_id
     */
    public User getUserById(int userId) {
        String query = "SELECT user_id, username, email, first_name, last_name, about_me, date_of_birth, created_at FROM Users WHERE user_id = ?";
        User user = null;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("about_me"),
                            rs.getDate("date_of_birth"),
                            rs.getTimestamp("created_at"),
                            rs.getBoolean("registered")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user by ID", e);
        }

        return user;
    }

    public boolean setData(Map<String, String> data, String email) {
        if (data.isEmpty()) return false;

        StringBuilder query = new StringBuilder("UPDATE Users SET ");
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

                // Handle the "registered" column separately
                if ("registered".equals(key)) {
                    // Convert the value to an integer (0 or 1)
                    int registeredValue = "true".equalsIgnoreCase(value) ? 1 : 0;
                    stmt.setByte(index++, (byte) 1);
                } else {
                    stmt.setString(index++, value);
                }
            }

            stmt.setString(index, email);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user info: ", e);
        }
    }

    /**
     * Generic method for getting information about a user from the Users table.
     * Aims to replace making new methods for specific data requests.
     *
     * @param columns Fields to be retrieved
     * @param email Unique email for specific user
     * @return A Map of the columns as the keys and their values as the values
     */
    public Map<String, String> getData(List<String> columns, String email) {
        Map<String, String> data = new HashMap<>();
        String query = "SELECT " + String.join(", ", columns) + " FROM Users WHERE email = ?";

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
            throw new RuntimeException("Error retrieving user info: ", e);
        }

        return data;
    }

    /**
     * Checks if the given credentials are valid.
     * @param email
     * @param password
     * @return
     */
    public boolean isUserLogin(String email, String password) {
        String query = "SELECT user_id FROM Users WHERE email = ? AND hashed_password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);  // <-- Set first parameter
            stmt.setString(2, password);  // <-- Set second parameter
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying for user", e);
        }
    }


    public void removeUser(String email) {
        String query = "DELETE FROM Users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing user (Does user exist?)", e);
        }
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }
}
