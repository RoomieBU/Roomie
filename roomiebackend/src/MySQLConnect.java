import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnect {
    public static String login(String formUser, String formPass) {
        // Database credentials
        String url = "jdbc:mysql://localhost:3306/Roomie"; // Replace with your database name
        String user = "database"; // Replace with your username
        String password = "Roomie"; // Replace with your password

        // Try-with-resources automatically closes the connection
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE username=" + formUser + " AND password=" + formPass + ";")) { // Replace with your table

            // Process query results
            if (rs.next()) {
                return Auth.getToken(formUser);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
