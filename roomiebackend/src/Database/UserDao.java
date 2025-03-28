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
public class UserDao extends Dao{
    private Connection connection;

    /**
     * Takes the connection given to connect to the database
     */
    public UserDao(Connection connection) throws SQLException {
        super(connection);
        this.connection = connection;
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

    /**
     * Returns a list of all users from the database
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, email, first_name, last_name, about_me, date_of_birth, created_at, registered, profile_picture_url FROM Users";

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
                        rs.getBoolean("registered"),
                        rs.getString("profile_picture_url")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving users", e);
        }

        return users;
    }

    /**
     * Returns a list of all users from the database who have a true relationship with the user
     * 
     * @param 
     * @return A list of users that are liked by the current user
     */
    public List<User> getAllLikedUsers(String email) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT shown_user FROM UserMatchInteractions WHERE user = ? AND relationship = true";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                int userID = getIDfromEmail(rs.getString("shown_user"));
                User user = getUserById(userID);

                if(user != null)
                    users.add(user);

            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving liked users", e);
        }
        
        return users;
    }


    // Testing
    public ChatInformation getChatInformation(String email) {
        String query = "SELECT first_name, last_name, profile_picture_url FROM Users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement((query))) {
            stmt.setString(1, email);
            try(ResultSet rs = stmt.executeQuery()) {
                rs.next();
                ChatInformation chatInfo = new ChatInformation(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("profile_picture_url")
                );

                 return chatInfo;
            }
        }catch (SQLException e) {
            throw new RuntimeException("Error retrieving given user's profile picture", e);
        }
    }

    /**
     * Returns a list of all users from the database who belong to the specified school.
     *
     * @param school The school to filter users by.
     * @return A list of users from the same school.
     */
    public List<User> getAllUsersBySchool(String school) {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, email, first_name, last_name, about_me, date_of_birth, created_at, registered, school, profile_picture_url FROM Users WHERE school = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, school); // Set the school parameter
            try (ResultSet rs = stmt.executeQuery()) {
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
                            rs.getBoolean("registered"),
                            rs.getString("profile_picture_url")
                    );
                    // Add the school field to the User object (if your User class supports it)
                    // If not, you can modify the User class to include a school field.
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving users by school", e);
        }

        return users;
    }


    /**
     * Returns a user from the database based on their user_id
     */
    public User getUserById(int userId) {
        String query = "SELECT user_id, username, email, first_name, last_name, about_me, date_of_birth, created_at, profile_picture_url FROM Users WHERE user_id = ?";
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
                            rs.getBoolean("registered"),
                            rs.getString("profile_picture_url")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user by ID", e);
        }
        return user;
    }

    public User getUserByEmail(String email) {
        String query = "SELECT user_id, username, email, first_name, last_name, about_me, date_of_birth, created_at, registered, profile_picture_url FROM Users WHERE email = ?";
        User user = null;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
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
                            rs.getBoolean("registered"),
                            rs.getString("profile_picture_url")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user by email", e);
        }
        return user;
    }

    /**
     * Returns a userID from the database based on their email
     */
    public int getIDfromEmail(String email) {
        String query = "SELECT user_id FROM Users WHERE email = ?";
        int userID = -1; // Default to -1 (or some other invalid value) in case no user is found
    
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email); // Bind email to the query
    
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userID = rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user ID from email", e);
        }
        return userID;
    }
    

    /**
     * Generic method for setting data within the Users table in the database.
     *
     * @param data
     * @param email
     * @return
     */
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
     * Generic method for getting information about a user from the Preferences table.
     *
     * @param columns Fields to be retrieved
     * @param email Unique email for specific user
     * @return A Map of the columns as the keys and their values as the values
     */
    public Map<String, String> getPreferences(List<String> columns, String email) {
        Map<String, String> data = new HashMap<>();
        int userId = getIDfromEmail(email);
        String query = "SELECT " + String.join(", ", columns) + " FROM UserPreferences WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, String.valueOf(userId));
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
            stmt.setString(1, email);
            stmt.setString(2, password);
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
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }


    
    


}


