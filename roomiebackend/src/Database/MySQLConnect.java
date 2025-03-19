package Database;

import Tools.Auth;
import Tools.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLConnect {
    public static String login(String formUser, String formPass) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        // Wait a minute... what are these credentials doing here???
        String url = "jdbc:mysql://localhost:3306/Roomie";
        String user = "database";
        String password = "Roomie";

        String query = "SELECT * FROM Users WHERE username = ? AND hashed_password = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Anti injection measures
            pstmt.setString(1, formUser);
            pstmt.setString(2, Utils.hashSHA256(formPass));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { // If anything appears, login is good
                    return Auth.getToken(formUser);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String registerUser(String username, String pass, String email, String firstName, String lastName, String aboutMe, String dob) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String url = "jdbc:mysql://localhost:3306/Roomie";
        String user = "database";
        String password = "Roomie";

        // Check if the user already exists
        String checkUserQuery = "SELECT * FROM Users WHERE username = ?";
        String insertQuery = "INSERT INTO Users (username, hashed_password, email, first_name, last_name, about_me, date_of_birth) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery)) {

            checkStmt.setString(1, username);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return null;
                }
            }

            // Hash password before storing
            String hashedPassword = Utils.hashSHA256(pass);

            // Insert user into the database
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, hashedPassword);
                insertStmt.setString(3, email);
                insertStmt.setString(4, firstName);
                insertStmt.setString(5, lastName);
                insertStmt.setString(6, aboutMe);
                insertStmt.setString(7, dob);

                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows > 0) {
                    return Auth.getToken(username); // Return token upon successful registration
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
