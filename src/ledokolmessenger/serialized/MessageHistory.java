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
public class MessageHistory extends SendableObject {
    private List<Message> messageList;

    public MessageHistory(String type,List<Message> messageList) {
        super(type);
        this.messageList = messageList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }
    
    
    
}
