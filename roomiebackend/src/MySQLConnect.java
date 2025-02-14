import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLConnect {
    public static String login(String formUser, String formPass) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        // Database credentials
        String url = "jdbc:mysql://localhost:3306/Roomie"; // Replace with your database name
        String user = "database"; // Replace with your username
        String password = "Roomie"; // Replace with your password

        String query = "SELECT * FROM Users WHERE username = ? AND hashed_password = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set parameters safely
            pstmt.setString(1, formUser);
            pstmt.setString(2, formPass);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { // If a row exists, credentials are correct
                    return Auth.getToken(formUser);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
