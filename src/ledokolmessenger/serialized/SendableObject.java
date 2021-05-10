package ledokolmessenger.serialized;

import java.io.Serializable;

/**
 *
 * @author BOT
 */
public class SendableObject implements Serializable{
    private String type;
    
    public SendableObject(String type)
    {
        this.type = type;
    }
    
    public String getType()
    {
        return type;
    }
}
