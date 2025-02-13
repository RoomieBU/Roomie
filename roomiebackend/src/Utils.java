public class Utils {

    public static String assembleHTTPResponse(int code, String r) {

        String status = switch (code) {
            case 200 -> "200 OK";
            case 201 -> "201 Created";
            case 401 -> "401 Unauthorized";
            case 403 -> "403 Forbidden";
            case 404 -> "404 Not Found";
            case 500 -> "500 Internal Server Error";
            default -> "400 Bad Request";
        };

        return "HTTP/1.1 " + status + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + r.length() + "\r\n" +
                "\r\n" +
                r;
    }
}
