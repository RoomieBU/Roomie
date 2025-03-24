package Tools;

import Controller.MatchController;
import Database.Dao;
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
        commands.put("similarity", this::similarity);
        commands.put("populatesimilarity", this::populateSimilarityTable);
        commands.put("populatesimilarityforuser", this::simForEmail);
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
            System.out.println("[Tools.Console] Unable to establish SQL connection");
        }
    }

    private void createUser() {
        try {
            UserDao ud = new UserDao(SQLConnection.getConnection());

            System.out.print("[Tools.Console] Enter username: ");
            String user = scan.nextLine().trim();
            System.out.print("[Tools.Console] Enter password: ");
            String pass = scan.nextLine().trim();
            ud.createUser(user, pass);
            System.out.println("[Tools.Console] Created new entry for user " + user);
            ud.closeConnection();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Tools.Console] Unable to establish SQL connection");
        }

    }

    private void removeUser() {
        try {
            UserDao ud = new UserDao(SQLConnection.getConnection());
            System.out.print("[Tools.Console] Enter username: ");
            String user = scan.nextLine().trim();
            ud.removeUser(user);
            ud.closeConnection();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Tools.Console] Unable to establish SQL connection");
        }
    }

    private void updateUser() {
        try {
            UserDao ud = new UserDao(SQLConnection.getConnection());

            System.out.print("[Tools.Console] Enter username: ");
            String username = scan.nextLine().trim();
            System.out.print("[Tools.Console] Enter email: ");
            String email = scan.nextLine().trim();
            System.out.print("[Tools.Console] Enter first name: ");
            String fName = scan.nextLine().trim();
            System.out.print("[Tools.Console] Enter last name: ");
            String lName = scan.nextLine().trim();
            System.out.print("[Tools.Console] Enter about me: ");
            String about_me = scan.nextLine().trim();
            System.out.print("[Tools.Console] Enter DOB (YYYY-MM-DD): ");
            String dob = scan.nextLine().trim();

            //ud.updateUserInfo(username, email, fName, lName, about_me, dob);
            System.out.println("[Tools.Console] Updated record for user " + email);
            ud.closeConnection();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Tools.Console] Unable to establish SQL connection");
        }
    }

    private void hash() {
        System.out.print("[Tools.Console] Enter string to be hashed: ");
        String in = scan.nextLine().trim();
        System.out.println("[Tools.Console] SHA256 Output: " + Utils.hashSHA256(in));
    }

    private void help() {
        System.out.print("[Tools.Console] Available commands: ");
        for (String command : commands.keySet()) {
            System.out.print(command + ", ");
        }
        System.out.print("\n");
    }

    private void totalConnections() {
        System.out.println("[Tools.Console] Total active connections: ?");
    }

    private void sendEmail() {
        System.out.print("[Tools.Console] Enter recipient: ");
        String email = scan.nextLine().trim();
        System.out.print("[Tools.Console] Enter subject: ");
        String subject = scan.nextLine().trim();
        System.out.print("[Tools.Console] Enter body: ");
        String body = scan.nextLine().trim();

        Mail m = new Mail();
        if (m.send(email, subject, body)) {
            System.out.println("[Tools.Console] Sent email!");
        } else {
            System.out.println("[Tools.Console] Didn't send email.");
        }

    }

    private void similarity() {
        System.out.print("Enter user email 1: ");
        String email1 = scan.nextLine().trim();
        System.out.print("Enter user email 2: ");
        String email2 = scan.nextLine().trim();

        System.out.println("Similarity between users: " + MatchController.getSimilarity(email1, email2));
    }

    private void populateSimilarityTable() {
        long startTime = System.nanoTime();

        try {
            List<User> userList;
            UserDao uDao = new UserDao(SQLConnection.getConnection());
            Dao dao = new Dao(SQLConnection.getConnection());
            userList = uDao.getAllUsers();
            for (User u : userList) {
                for (User b : userList) {
                    if (u.getEmail().equals(b.getEmail())) {
                        continue;
                    }
                    dao.insert(
                            Map.of("email1", u.getEmail(),
                                    "email2", b.getEmail(),
                                    "similarity_score", String.valueOf(MatchController.getSimilarity(u.getEmail(), b.getEmail()))),
                            "UserSimilarities");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000.0;
        System.out.println("Populated in " + duration + " milliseconds");
    }

    private void simForEmail() {
        System.out.print("Enter user email 1: ");
        String email = scan.nextLine().trim();
        long startTime = System.nanoTime();
        MatchController.calculateAllSimilaritiesForEmail(email); // Maybe there's a better name for this?
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000.0;
        System.out.println("Calculated in " + duration + " milliseconds");
    }
}
