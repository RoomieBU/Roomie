package Database;
/**
 * UserSchedule Class
 * serves as a model for the user entity, holding all the relevant information
 * provides getters for each attribute, and toString for readable version
 *
 * interacts with the database
 */
public class UserSchedule {
    private int userId;
    private String schedule;

    public UserSchedule(int userId, String schedule) {
        this.userId = userId;
        this.schedule = schedule;
    }

    // getters
    public int getUserId() { return userId; }
    public String getSchedule() { return schedule; }

    @Override
    public String toString() {
        return "UserSchedule{" +
                "user_id=" + userId +
                ", schedule='" + schedule + '\'' +
                '}';
    }
}
