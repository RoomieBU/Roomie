
import java.util.List;

public class CreateGroupChatRequest {
    private String token;
    private List<Integer> groupChatIds;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Integer> getGroupChatIds() {
        return groupChatIds;
    }

    public void setGroupChatIds(List<Integer> groupChatIds) {
        this.groupChatIds = groupChatIds;
    }
}