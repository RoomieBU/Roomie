package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        String query = "INSERT INTO Users (username, hashed_password) VALUES (?, ?)";

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
        String query = "UPDATE Users SET email = ?, first_name = ?, last_name = ?, about_me = ?, date_of_birth = ? WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, first_name);
            stmt.setString(3, last_name);
            stmt.setString(4, about_me);
            stmt.setString(5, DOB);
            stmt.setString(6, username);

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

    /*
     * Returns if a user is registered or not, for redirection
     */
    public boolean isRegistered(String email) {
        boolean val = false;
        String query = "SELECT registered FROM Users WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    val = rs.getBoolean("registered");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user", e);
        }

        return val;
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

    /**
     * Checks if the given credentials are valid.
     * @param username
     * @param password
     * @return
     */
    public boolean isUserLogin(String username, String password) {
        String query = "SELECT user_id FROM Users WHERE username = ? AND hashed_password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);  // <-- Set first parameter
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

    public void removeUser(String username) {
        String query = "DELETE FROM Users WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing user (Does user exist?)", e);
        }
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }
}
