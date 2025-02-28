package Database;

import java.sql.Connection;
import java.sql.SQLException;

public class UserMatchInteractionDao {
    private Connection connection;

    /**
     * Takes the connection given to connect to the database
     */
    public UserMatchInteractionDao(Connection connection) throws SQLException {
        this.connection = connection;
    }



}
