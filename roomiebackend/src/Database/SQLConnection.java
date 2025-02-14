package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/Roomie";
    private static final String USER = "database";
    private static final String PASSWORD = "Roomie";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
