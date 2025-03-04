import Database.SQLConnection;
import Database.User;
import Database.UserDao;

import java.sql.SQLException;
import java.util.*;

public class Console {

    static HashMap<String, Runnable> commands = new HashMap<>();
    static Scanner scan = new Scanner(System.in);
    public Console() {
        commands.put("createuser", this::createUser);
        commands.put("removeuser", this::removeUser);
        commands.put("help", this::help);
        commands.put("hash", this::hash);
        commands.put("totalconnections", this::totalConnections);
        commands.put("printusers", this::printUsers);
        commands.put("updateusers", this::updateUser);
        commands.put("sendemail", this::sendEmail);
    }

    public void start() {
        String input;
        while (true) { // not sure about this
            System.out.print(">> ");
            input = scan.nextLine().trim();

            for (String command : commands.keySet()) {
                if (input.equals(command)) {
                    Runnable action = commands.get(command);
                    action.run();
                    break;
                }
            }
        }
    }

    private void printUsers() {
        try {
            UserDao ud = new UserDao(SQLConnection.getConnection());

            List<User> usersList = ud.getAllUsers();

            System.out.println("User ID\tEmail");
            for (User u : usersList) {
                System.out.println(u.getUserId() + "\t" + u.getEmail());
            }
            ud.closeConnection();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Console] Unable to establish SQL connection");
        }
    }

    private void createUser() {
        try {
            UserDao ud = new UserDao(SQLConnection.getConnection());

            System.out.print("[Console] Enter username: ");
            String user = scan.nextLine().trim();
            System.out.print("[Console] Enter password: ");
            String pass = scan.nextLine().trim();
            ud.createUser(user, pass);
            System.out.println("[Console] Created new entry for user " + user);
            ud.closeConnection();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Console] Unable to establish SQL connection");
        }

    }

    private void removeUser() {
        try {
            UserDao ud = new UserDao(SQLConnection.getConnection());

            String user = scan.nextLine().trim();
            ud.removeUser(user);
            System.out.print("[Console] Enter username: ");
            ud.closeConnection();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Console] Unable to establish SQL connection");
        }
    }

    private void updateUser() {
        try {
            UserDao ud = new UserDao(SQLConnection.getConnection());

            System.out.print("[Console] Enter username: ");
            String username = scan.nextLine().trim();
            System.out.print("[Console] Enter email: ");
            String email = scan.nextLine().trim();
            System.out.print("[Console] Enter first name: ");
            String fName = scan.nextLine().trim();
            System.out.print("[Console] Enter last name: ");
            String lName = scan.nextLine().trim();
            System.out.print("[Console] Enter about me: ");
            String about_me = scan.nextLine().trim();
            System.out.print("[Console] Enter DOB (YYYY-MM-DD): ");
            String dob = scan.nextLine().trim();

            //ud.updateUserInfo(username, email, fName, lName, about_me, dob);
            System.out.println("[Console] Updated record for user " + email);
            ud.closeConnection();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Console] Unable to establish SQL connection");
        }
    }

    private void hash() {
        System.out.print("[Console] Enter string to be hashed: ");
        String in = scan.nextLine().trim();
        System.out.println("[Console] SHA256 Output: " + Utils.hashSHA256(in));
    }

    private void help() {
        System.out.print("[Console] Available commands: ");
        for (String command : commands.keySet()) {
            System.out.print(command + ", ");
        }
        System.out.print("\n");
    }

    private void totalConnections() {
        System.out.println("[Console] Total active connections: " + Server.connections);
    }

    private void sendEmail() {
        System.out.print("[Console] Enter recipient: ");
        String email = scan.nextLine().trim();
        System.out.print("[Console] Enter subject: ");
        String subject = scan.nextLine().trim();
        System.out.print("[Console] Enter body: ");
        String body = scan.nextLine().trim();

        Mail m = new Mail();
        if (m.send(email, subject, body)) {
            System.out.println("[Console] Sent email!");
        } else {
            System.out.println("[Console] Didn't send email.");
        }

    }
}
