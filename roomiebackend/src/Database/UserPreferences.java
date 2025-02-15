package Database;

import java.sql.Time;

/**
 * UserPreferences Class
 * serves as a model for the user entity, holding all the relevant information
 * provides getters for each attribute, and toString for readable version
 *
 * interacts with the database
 */
public class UserPreferences {
    private int userId;
    private String preferredGender;
    private boolean petFriendly;
    private String personality;
    private Time wakeupTime;
    private Time sleepTime;
    private String quietHours;

    public UserPreferences(int userId, String preferredGender, boolean petFriendly, String personality, Time wakeupTime, Time sleepTime, String quietHours) {
        this.userId = userId;
        this.preferredGender = preferredGender;
        this.petFriendly = petFriendly;
        this.personality = personality;
        this.wakeupTime = wakeupTime;
        this.sleepTime = sleepTime;
        this.quietHours = quietHours;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getPreferredGender() { return preferredGender; }
    public boolean getPetFriendly() { return petFriendly; }
    public String getPersonality() { return personality; }
    public Time getWakeupTime() { return wakeupTime; }
    public Time getSleepTime() { return sleepTime; }
    public String getQuietHours() { return quietHours; }

    @Override
    public String toString() {
        return "UserPreferences{" +
                "user_id=" + userId +
                ", preferred_gender='" + preferredGender + '\'' +
                ", pet_friendly=" + petFriendly +
                ", personality='" + personality + '\'' +
                ", wakeup_time=" + wakeupTime +
                ", sleep_time=" + sleepTime +
                ", quiet_hours='" + quietHours + '\'' +
                '}';
    }

}
