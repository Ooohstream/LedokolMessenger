package ledokolmessenger.serialized;

/**
 *
 * @author BOT
 */
public class ClientInfo extends SendableObject{
    private String clientName;
    private String password;
    private boolean pendingRequest;
    
    public ClientInfo(String type,String clientName)
    {
        super(type);
        this.clientName = clientName;
    }
   
    public ClientInfo(String type, String clientName, String password) {
        super(type);
        this.clientName = clientName;
        this.password = password;
    }

    public ClientInfo(String type,String clientName, boolean pendingRequest ) {
        super(type);
        this.clientName=clientName;
        this.pendingRequest=pendingRequest;
    }
    
    
    public String getClientName() {
        return clientName;
    }

    public String getPassword() {
        return password;
    }

    public boolean getPendingRequest() {
        return pendingRequest;
    }
    
}
