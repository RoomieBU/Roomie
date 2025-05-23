package Database;

public class ChatInformation {

    private String profilePicture;
    private String firstName;
    private String lastName;
    private String email;

    public ChatInformation(String firstName, String lastName, String profilePicture, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.email = email;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }

    
    // Getter for profilePicture
    public String getProfilePicture() {
        return profilePicture;
    }

    // Setter for profilePicture
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    // Getter for firstName
    public String getFirstName() {
        return firstName;
    }

    // Setter for firstName
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Getter for lastName
    public String getLastName() {
        return lastName;
    }

    // Setter for lastName
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}