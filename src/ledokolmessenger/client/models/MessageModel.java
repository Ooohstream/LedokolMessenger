package ledokolmessenger.client.models;

/**
 *
 * @author OMEN
 */
public class MessageModel {
    private String sender;
    private String recipient;
    private String senderMessage;
    private String recipientMessage;

    public MessageModel(String sender, String senderMessage, String recipientMessage, String recipient) {
        this.sender = sender;
        this.recipient = recipient;
        this.senderMessage = senderMessage;
        this.recipientMessage = recipientMessage;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSenderMessage() {
        return senderMessage;
    }

    public void setSenderMessage(String senderMessage) {
        this.senderMessage = senderMessage;
    }

    public String getRecipientMessage() {
        return recipientMessage;
    }

    public void setRecipientMessage(String recipientMessage) {
        this.recipientMessage = recipientMessage;
    }
    
    
}
