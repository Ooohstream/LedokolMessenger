package ledokolmessenger.serialized;

import java.time.LocalDateTime;

/**
 *
 * @author BOT
 */
public class Message extends SendableObject{
    private String message;
    private LocalDateTime time;

    public Message(String type, String message, LocalDateTime time) {
        super(type);
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTime() {
        return time;
    }
    
}
