package Tools;

import java.util.HashMap;

public class HTTPResponse {
    private HashMap<String, String> response = new HashMap<>();
    public int code;

    public HTTPResponse() {
        code = 400;
    }

    public void setMessage(String key, String val) {
        response.put(key, val);
    }

    public String toString() {
        return Utils.assembleHTTPResponse(code, Utils.assembleJson(response));
    }

}
