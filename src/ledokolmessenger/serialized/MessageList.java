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
public class MessageList extends SendableObject {
    private List<Message> MessageList;

    public MessageList(String type,List<Message> MessageList) {
        super(type);
        this.MessageList = MessageList;
    }

    public List<Message> getMessageList() {
        return MessageList;
    }
    
    
    
}
