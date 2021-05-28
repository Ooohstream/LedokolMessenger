package ledokolmessenger.serialized;

import java.time.LocalDateTime;

/**
 *
 * @author OMEN
 */
public class Respond extends SendableObject{
    private int respondCode;
    private String respond;
    private LocalDateTime respondTime;

    public Respond(String type,int respondCode, String respond, LocalDateTime respondTime) {
        super(type);
        this.respondCode = respondCode;
        this.respond = respond;
        this.respondTime = respondTime;
    }

    public int getRespondCode() {
        return respondCode;
    }

    public String getRespond() {
        return respond;
    }

    public LocalDateTime getRespondTime() {
        return respondTime;
    }
    
    
}
