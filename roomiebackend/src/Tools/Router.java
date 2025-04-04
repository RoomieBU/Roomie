package Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * This class is used to create a streamlined approach to handling routes and mapping them
 * to their appropriate controllers.
 */

public class Router {
    private Map<String, BiFunction<Map<String, String>, String, String>> routes = new HashMap<>();

    public void addRoute(String path, BiFunction<Map<String, String>, String, String> handler) {
        routes.put(path, handler);
    }

    public String handleRequest(String path, Map<String, String> data, String method) {
        BiFunction<Map<String, String>, String, String> handler = routes.get(path);
        if (handler != null) {
            return handler.apply(data, method);
        }
        return Utils.assembleHTTPResponse(404, "{\"message\": \"Not Found\"}");
    }
}
