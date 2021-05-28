package ledokolmessenger.serialized;

import java.time.LocalDateTime;

/**
 *
 * @author BOT
 */
public class Message extends SendableObject{
    private String message;
    private String sender;
    private String recipient;
    private LocalDateTime time;

    public Message(String type, String message,String sender, String recipient, LocalDateTime time) {
        super(type);
        this.message = message;
        this.sender=sender;
        this.recipient=recipient;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }
 
    
}
