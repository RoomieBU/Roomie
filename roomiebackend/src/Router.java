import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * This class is used to create a streamlined approach to handling routes and mapping them
 * to their appropriate controllers.
 */

public class Router {

    /**
     * I KNOW this looks insane but check this out:
     * BiFunction takes two arguments and produces some result (of data type of the 3rd parameter)
     * BiFunction will end up being login or register classes within auth controller
     *
     * Map<String, String> is the type for the received form data.
     */
    // Modified to accept Object as the data type so it can handle both Map and byte[]
    private Map<String, BiFunction<Object, String, String>> routes = new HashMap<>();

    public void addRoute(String path, BiFunction<Object, String, String> handler) {
        routes.put(path, handler);
    }

    public String handleRequest(String path, Object data, String method) {
        BiFunction<Object, String, String> handler = routes.get(path);
        if (handler != null) {
            return handler.apply(data, method);
        }
        return Utils.assembleHTTPResponse(404, "{\"message\": \"Not Found\"}");
    }
}