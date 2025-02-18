package Database;

/**
 * UserRatings Class
 * serves as a model for the user ratings entity, holding all the relevant information
 * provides getters for each attribute, and toString for readable version
 *
 * interacts with the database
 */
public class UserRatings {
    private int ratingId;
    private int ratedUser;
    private int reviewerUser;
    private int ratingValue;
    private String comment;

    public UserRatings(int ratingId, int ratedUser, int reviewerUser, int ratingValue, String comment) {
        this.ratingId = ratingId;
        this.ratedUser = ratedUser;
        this.reviewerUser =  reviewerUser;
        this.ratingValue = ratingValue;
        this.comment = comment;
    }

    // Getters
    public int getRatingId() { return ratingId; }
    public int getRatedUser() { return ratedUser; }
    public int getReviewerUser() { return reviewerUser; }
    public int getRatingValue() { return ratingValue; }
    public String getComment() { return comment; }

    @Override
    public String toString() {
        return "UserRating{" +
                "ratingId='" + ratingId + '\'' +
                ", ratedUser='" + ratedUser + '\'' +
                ", reviewerUser='" + reviewerUser + '\'' +
                ", ratingValue='" + ratingValue + '\'' +
                ", comment=" + comment +
                "}";
    }
}
