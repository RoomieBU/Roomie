package Database;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * User Class
 * serves as a model for the user entity, holding all the relevant information
 * provides getters for each attribute, and toString for readable version
 *
 * interacts with the database
 */
public class User {
    private int userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String aboutMe;
    private Date dateOfBirth;
    private Timestamp createdAt;
    private Boolean registered;
    private String school;
    private String profilePicture;

    public User(int userId, String username, String email, String firstName, String lastName, String aboutMe, Date dateOfBirth, Timestamp createdAt, Boolean registered, String profilePicture) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.aboutMe = aboutMe;
        this.dateOfBirth = dateOfBirth;
        this.createdAt = createdAt;
        this.registered = registered;
        this.school = school;

        this.profilePicture = profilePicture;
        // "https://i.natgeofe.com/n/548467d8-c5f1-4551-9f58-6817a8d2c45e/NationalGeographic_2572187_3x2.jpg"
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAboutMe() { return aboutMe; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Boolean getRegistered() { return registered; }
    public String getSchool() { return school; }
    public String getProfilePicture() {return profilePicture;}

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", createdAt=" + createdAt +
                ", registered =" + registered +
                ", school =" + school +
                ", profileUrl =" + profilePicture + '}';
    }

}
