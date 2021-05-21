package ledokolmessenger.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ledokolmessenger.serialized.ClientInfo;
import ledokolmessenger.serialized.Message;

/**
 *
 * @author BOT
 */
public class Client implements Runnable{
    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    private final String clientName;
    private Database db;
  public Client(Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream, String clientName) {
      this.socket = socket;
      this.outputStream = outputStream;
      this.inputStream = inputStream;
      this.clientName = clientName;
  }

  @Override
  public void run() {   
      System.out.println(this.clientName +" подключился");
      
    try {  
      List<ClientInfo> friends;
      String id=this.clientName;
      friends=db.getListFriends(id);
      System.out.println(this.clientName +" подключился");
      outputStream.writeObject(friends);
      while (true) {
          
          Message message = (Message)inputStream.readObject();
          if (message.getMessage().equalsIgnoreCase("##session##end##")) {
            break;
            }
          this.sendMessage(message);
          Thread.sleep(100);
    }
  }
  catch (IOException | ClassNotFoundException | InterruptedException ex){
          Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
      } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
  finally {
    this.close();
  }
}
  
  public void sendMessage(Message message) {
      StartServer.clients.forEach(client -> {
          try {
              client.outputStream.writeObject(message);
          } catch (IOException ex) {
              Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
          }
        });
  }
  
  public void close() {
      StartServer.clients.remove(this);
  }
}
