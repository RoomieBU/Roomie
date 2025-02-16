/**
 * Basic information to be associated with a token.
 */
public class TokenInfo {
    String username;
    double expirationTime;

    TokenInfo(String username, double expirationTime) {
        this.username = username;
        this.expirationTime = expirationTime;
    }
}
