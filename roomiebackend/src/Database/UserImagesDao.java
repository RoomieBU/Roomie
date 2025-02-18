package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User images Data Access Object
 * interface for the user images table
 *
 * work in progress, add methods as needed
 */
public class UserImagesDao {
    private Connection connection;

    public UserImagesDao(Connection connection) throws SQLException {
        this.connection = connection;
    }

    public void uploadUserImage(int userId, String imageUrl) {
        String query = "INSERT INTO UserImages (user_id, image_url) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setString(2, imageUrl);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error uploading user image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<UserImages> getAllUserImages() {
        List<UserImages> images = new ArrayList<>();
        String query = "SELECT * FROM UserImages";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                images.add(new UserImages(
                        rs.getInt("image_id"),
                        rs.getInt("user_id"),
                        rs.getString("image_url"),
                        rs.getTimestamp("uploaded_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user images: " + e.getMessage());
            e.printStackTrace();
        }
        return images;
    }
}
