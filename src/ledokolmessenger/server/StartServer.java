package ledokolmessenger.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BOT
 */
public class StartServer {

    private static final Integer PORT = 3443;
    public static final List<Client> clients = new ArrayList<>();
    public static final Map<String, String> namePass = new HashMap<>();
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = null;
        Socket userSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server has started working!");
            
            while (true) {
                userSocket = serverSocket.accept();
                new Thread(new Authentication(userSocket)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(StartServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            userSocket.close();
            serverSocket.close();
            System.out.println("Server has been stopped");
        }
        
    }
}
