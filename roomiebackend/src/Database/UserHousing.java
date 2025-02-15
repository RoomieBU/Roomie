package Database;
/**
 * UserHousing Class
 * serves as a model for the user entity, holding all the relevant information
 * provides getters for each attribute, and toString for readable version
 *
 * interacts with the database
 */
public class UserHousing {
    private int userId;
    private String location;

    public UserHousing(int userId, String location) {
        this.userId = userId;
        this.location = location;
    }

    // getters
    public int getUserId() { return userId; }
    public String getLocation() { return location; }

    @Override
    public String toString() {
        return "UserHousing{" +
                "user_id=" + userId +
                ", location='" + location + '\'' +
                '}';
    }

}
