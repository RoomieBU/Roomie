package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User Data Access Object
 *  interface for the User Table
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
     * doesn't hash password here yet
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
            System.out.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of all users from the database
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, email, first_name, last_name, about_me, date_of_birth, created_at FROM Users";

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
                        rs.getTimestamp("created_at")
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
                            rs.getTimestamp("created_at")
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

    public void closeConnection() throws SQLException {
        connection.close();
    }
}
