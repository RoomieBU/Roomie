import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Console {

    static HashMap<String, Runnable> commands = new HashMap<>();
    static Scanner scan = new Scanner(System.in);
    public Console() {
        commands.put("createuser", this::createUser);
        commands.put("removeuser", this::removeUser);
        commands.put("help", this::help);
        commands.put("hash", this::hash);
    }

    static void start() {

        String input;
        while (true) { // not sure about this
            System.out.print(">> ");
            input = scan.nextLine().trim();
            System.out.println(input);

            for (String command : commands.keySet()) {
                if (input.equals(command)) {
                    Runnable action = commands.get(command);
                    action.run();
                    break;
                }
            }
        }
    }

    private void createUser() {

    }

    private void removeUser() {

    }

    private void hash() {
        String in = scan.nextLine().trim();

        System.out.println("[Console] SHA256 Output: " + Utils.hashSHA256(in));
    }

    private void help() {
        System.out.print("[Console] Available commands: ");
        for (String command : commands.keySet()) {
            System.out.print(command + ", ");
        }
    }

}
