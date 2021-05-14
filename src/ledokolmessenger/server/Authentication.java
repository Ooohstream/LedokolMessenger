package ledokolmessenger.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ledokolmessenger.serialized.ClientInfo;
import ledokolmessenger.serialized.Respond;
import static ledokolmessenger.server.StartServer.namePass;
import java.sql.Statement;

/**
 *
 * @author OMEN
 */
public class Authentication implements Runnable{
    Socket userSocket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;    
    Database db;
    public Authentication(Socket socket, Statement st)
    {
        try {
            this.userSocket = socket;
            this.inputStream = new ObjectInputStream(this.userSocket.getInputStream());
            this.outputStream = new ObjectOutputStream(this.userSocket.getOutputStream());
            this.db = new Database(st);
        } catch (IOException ex) {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        
        try {
            ClientInfo authMessage = (ClientInfo)inputStream.readObject();
            
            if(authMessage.getType().equals("SignIn"))
            {
                    String mess=db.SignIn(authMessage);
                    
                    if(mess=="OK"){
                            outputStream.writeObject(new Respond("Respond", 200, "OK", java.time.LocalDateTime.now()));
                            Client newClient = new Client(this.userSocket, this.outputStream, this.inputStream, authMessage.getClientName());
                            StartServer.clients.add(newClient);
                            new Thread(newClient).start();
                    }
                    else
                         outputStream.writeObject(new Respond("Respond", 401, mess, java.time.LocalDateTime.now()));
                    
                    
                    /*
                    if(namePass.containsKey(authMessage.getClientName()))
                    {
                        if(namePass.get(authMessage.getClientName()).equals(authMessage.getPassword()))
                        {
                            outputStream.writeObject(new Respond("Respond", 200, "OK", java.time.LocalDateTime.now()));
                            Client newClient = new Client(this.userSocket, this.outputStream, this.inputStream, authMessage.getClientName());
                            StartServer.clients.add(newClient);
                            new Thread(newClient).start();
                        }
                        else
                            outputStream.writeObject(new Respond("Respond", 401, "Invalid password", java.time.LocalDateTime.now()));
                    }
                    else
                        outputStream.writeObject(new Respond("Respond", 401, "No such client exists", java.time.LocalDateTime.now()));
                    */
            }
            else if (authMessage.getType().equals("Register"))
            {
                    db.Register(authMessage);
                    //namePass.put(authMessage.getClientName(), authMessage.getPassword());
                    outputStream.writeObject(new Respond("Respond", 201, "Account created", java.time.LocalDateTime.now()));
                    this.outputStream.close();
                    this.inputStream.close();
                    this.userSocket.close();
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
