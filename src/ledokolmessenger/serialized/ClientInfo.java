package ledokolmessenger.serialized;

/**
 *
 * @author BOT
 */
public class ClientInfo extends SendableObject{
    private String clientName;
    private String ClientName2;
    private String password;
    private boolean is_online;
   
    public ClientInfo(String type, String clientName, String password) {
        super(type);
        this.clientName = clientName;
        this.password = password;
    }

    public ClientInfo(String type,String clientName, boolean is_online ) {
        super(type);
        this.clientName=clientName;
        this.is_online=is_online;
    }
    
    public ClientInfo(String type, String clientName, String clientName2){
        super(type);
        this.clientName = clientName;
        this.ClientName2 = clientName2;
    }
    
    public String getClientName() {
        return clientName;
    }

    public String getPassword() {
        return password;
    }

    public boolean getIs_online() {
        return is_online;
    }
    
}
