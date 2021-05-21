package ledokolmessenger.serialized;

/**
 *
 * @author BOT
 */
public class ClientInfo extends SendableObject{
    private String clientName;
    private String password;    
   
    public ClientInfo(String type, String clientName, String password) {
        super(type);
        this.clientName = clientName;
        this.password = password;
    }
    
    public String getClientName() {
        return clientName;
    }

    public String getPassword() {
        return password;
    }
    
    
}
