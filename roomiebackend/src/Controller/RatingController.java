package Controller;

import Database.RatingsDao;
import Database.SQLConnection;
import Database.UserDao;
import Tools.Auth;
import Tools.HTTPResponse;
import java.sql.Connection;
import java.util.Map;

public class RatingController {

    public static String submitRoommateRating(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        Connection conn = SQLConnection.getConnection();
        RatingsDao ratingsDao = new RatingsDao(conn);
        UserDao userDao = new UserDao(conn);

        String reviewerEmail = Auth.getEmailfromToken(data.get("token"));
        String ratedEmail = data.get("rated_user");
        int ratingValue = Integer.parseInt(data.get("rating_value"));
        String comment = data.get("comment");
        System.out.println("Reviewer email: " + reviewerEmail);
        System.out.println("Rated email: " + ratedEmail);
        System.out.println("Rating value: " + ratingValue);
        System.out.println("Comment: " + comment);

        // Fetch user IDs based on emails
        int reviewerId = userDao.getIDfromEmail(reviewerEmail);
        int ratedId = userDao.getIDfromEmail(ratedEmail);

        // Insert rating into the database
        boolean isInserted = ratingsDao.insertRating(reviewerId, ratedId, ratingValue, comment);

        if (isInserted) {
            response.setMessage("message", "Rating submitted successfully!");
            response.code = 200;
        } else {
            response.setMessage("message", "Failed to submit rating");
            response.code = 500;
        }

        return response.toString();
    }
}