package ledokolmessenger.serialized;

import java.util.List;

/**
 *
 * @author aleks
 */
public class UpdateData extends SendableObject  {
    private List<ClientInfo> FriendList;

    public UpdateData(String type,List<ClientInfo> FriendList) {
        super(type);
        this.FriendList = FriendList;
    }

    public List<ClientInfo> getFriendList() {
        return FriendList;
    }
    
}
