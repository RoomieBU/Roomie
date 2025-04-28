package Controller;

import Database.ChatDao;
import Database.SQLConnection;
import Tools.Auth;
import Tools.HTTPResponse;

import java.sql.Connection;
import java.util.Map;

public class ChatDeleteController {

    /**
     * Deletes all of a user's unconfirmed group chats and their history
     * @param data Map containing "token"
     * @param method POST
     * @return HTTPResponse with success or failure message
     */
    public static String deleteUnconfirmedGroupchats(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        Connection conn = SQLConnection.getConnection();

        String token = data.get("token");
        if (token == null || token.trim().isEmpty()) {
            response.code = 400;
            response.setMessage("message", "Missing or invalid token.");
            return response.toString();
        }

        String email = Auth.getEmailfromToken(token);
        if (email == null) {
            response.code = 401;
            response.setMessage("message", "Invalid token.");
            return response.toString();
        }

        try {
            ChatDao dao = new ChatDao(conn);
            boolean success = dao.deleteUncomfirmedGroupchats(email);
            if (success) {
                response.code = 200;
                response.setMessage("message", "Unconfirmed group chats deleted successfully.");
            } else {
                response.code = 500;
                response.setMessage("message", "Failed to delete unconfirmed group chats.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.code = 500;
            response.setMessage("message", "Server error occurred.");
        } finally {
            try { conn.close(); } catch (Exception e) { /* Ignore */ }
        }

        return response.toString();
    }


}
