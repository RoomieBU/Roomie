package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles establishing connection to the SQL database.
 */
public class SQLConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/Roomie";
    private static final String USER = "database";
    private static final String PASSWORD = System.getenv("SSL_KEY");

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("SQL Connection Error: " + e);
        }
        return null;
    }
}
