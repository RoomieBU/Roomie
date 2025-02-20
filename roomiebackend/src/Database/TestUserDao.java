
package Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class TestUserDao {
    public static void main(String[] args) {
        try (Connection connection = SQLConnection.getConnection()) {
            UserDao userDao = new UserDao(connection);

            // adding new user
            userDao.createUser("john_doe", "johndoe@example.com", "hashed_password123", "John", "Doe", "Just a test user.", Date.valueOf("2000-01-01"));

            //retrieving specific user
            List<User> users = userDao.getAllUsers();
            if (!users.isEmpty()) {
                User firstUser = users.get(0);
                System.out.println("First user: " + firstUser);
            } else {
                System.out.println("No users found.");
            }

            // retrieving all users
            System.out.println("\nAll users in the database:");
            for (User user : users) {
                System.out.println(user);
                System.out.println();
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}