/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ledokolmessenger.serialized;

import java.util.List;

/**
 *
 * @author aleks
 */
public class StartupInfo extends SendableObject {
    List<ClientInfo> friends;
    List<ClientInfo> friendRequests;
    List<ClientInfo> groupChats;

    public StartupInfo(String type, List<ClientInfo> friends, List<ClientInfo> friendRequests, List<ClientInfo> groupchats) {
        super(type);
        this.friends = friends;
        this.friendRequests = friendRequests;
        this.groupChats = groupchats;
    }

    public List<ClientInfo> getFriends() {
        return friends;
    }

    public List<ClientInfo> getFriendRequests() {
        return friendRequests;
    }

    public List<ClientInfo> getGroupChats() {
        return groupChats;
    }
    
    
    
}
