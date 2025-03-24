package Tools;

/**
 * Basic information to be associated with a token.
 */
public class TokenInfo {
    String email;
    double expirationTime;

    TokenInfo(String email, double expirationTime) {
        this.email = email;
        this.expirationTime = expirationTime;
    }
}
