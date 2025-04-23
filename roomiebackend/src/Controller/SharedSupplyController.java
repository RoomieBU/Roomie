package Controller;

import Tools.HTTPResponse;

import java.util.Map;

public class SharedSupplyController {
    /**
     * Returns a string of all items of a users supply list
     * @param data user token
     * @param method POST
     * @return http response with string containing all items and counts of a shared supply
     */
    public String getItems(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        return response.toString();
    }

    /**
     * Adds an item into the database
     * @param data token, item, count
     * @param method POST
     * @return http response
     */
    public String addItem(Map<String, String> data, String method){
        HTTPResponse response = new HTTPResponse();
        return response.toString();
    }

    /**
     * edits an existing item count
     * @param data token, item, new count
     * @param method POST
     * @return http response
     */
    public String editItem(Map<String, String> data, String method) {
        HTTPResponse response = new HTTPResponse();
        return response.toString();
    }
}
