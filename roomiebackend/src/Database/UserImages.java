package Database;

import java.sql.Timestamp;

/**
 * UserImages Class
 * serves as a model for the user ratings entity, holding all the relevant information
 * provides getters for each attribute, and toString for readable version
 *
 * interacts with the database
 */
public class UserImages {
    private int imageId;
    private int userId;
    private String imageUrl;
    private Timestamp uploadedAt;

    public UserImages(int imageId, int userId, String imageUrl, Timestamp uploadedAt) {
        this.imageId = imageId;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.uploadedAt = uploadedAt;
    }

    public int getImageId() { return imageId; }
    public int getUserId() { return userId; }
    public String getImageUrl() { return imageUrl; }
    public Timestamp getUploadedAt() { return uploadedAt; }
}
