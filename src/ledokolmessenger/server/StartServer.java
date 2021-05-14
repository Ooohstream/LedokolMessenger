package ledokolmessenger.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
        String user="postgres";
        String pwd = "";
        String dbUrl= "jdbc:postgresql://localhost:5432/serverdb";
        String drvName ="org.postgresql.Driver";
        Connection con = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server has started working!");
                        
            Class.forName(drvName);
            con = DriverManager.getConnection(dbUrl, user, pwd);
            Statement st=con.createStatement();
            while (true) {
                userSocket = serverSocket.accept();
                new Thread(new Authentication(userSocket,st)).start();
            }
            
        } catch (IOException ex) {
            Logger.getLogger(StartServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(StartServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            userSocket.close();
            serverSocket.close();
            System.out.println("Server has been stopped");
            if(con != null) try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(StartServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
