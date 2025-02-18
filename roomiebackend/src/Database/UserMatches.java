package Database;

import java.sql.Timestamp;

/**
 * UserMatches Class
 * serves as a model for the user matches entity, holding all relevant information
 * provides getters for each attribute, and toString for readable version
 *
 * interacts with the database
 */
public class UserMatches {
    private int matchId;
    private int user1Id;
    private int user2Id;
    private Timestamp matchDate;

    public UserMatches(int matchId, int user1Id, int user2Id, Timestamp matchDate) {
        this.matchId = matchId;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.matchDate = matchDate;
    }

    // Getters
    public int getMatchId() { return matchId; }
    public int getUser1Id() { return user1Id; }
    public int getUser2Id() { return user2Id; }
    public Timestamp getMatchDate() { return matchDate; }

    @Override
    public String toString() {
        return "UserMatches{" +
                "matchId='" + matchId + +'\'' +
                ", user1Id='" + user1Id + '\'' +
                ", user2Id='" + user2Id +'\'' +
                ", matchDate= " + matchDate +
                "}";
    }
}
