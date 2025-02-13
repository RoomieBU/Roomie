import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Managing authentication tokens
 */

public class Auth {
    private static final Map<String, TokenInfo> tokenStore = new HashMap<>();
    private static final double EXP_TIME = 1800000; // 30 min. in milliseconds

    public static String getToken(String username) {
        String token = UUID.randomUUID().toString();
        double exp = System.currentTimeMillis() + EXP_TIME;

        tokenStore.put(token, new TokenInfo(username, exp));
        return token;
    }

    public static boolean isValidToken(String token) {
        TokenInfo tInfo = tokenStore.get(token);
        if (tInfo == null) return false; // No active token

        // Invalidate tokens on the fly
        if (System.currentTimeMillis() > tInfo.expirationTime) {
            tokenStore.remove(token);
            return false;
        }

        return true;
    }

    public static void invalidateToken(String token) {
        tokenStore.remove(token);
    }

}
