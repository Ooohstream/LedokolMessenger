package ledokolmessenger.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import ledokolmessenger.serialized.ClientInfo;
import ledokolmessenger.serialized.Respond;
import static ledokolmessenger.server.StartServer.namePass;

/**
 *
 * @author OMEN
 */
public class Authentication implements Runnable{
    Socket userSocket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    
    public Authentication(Socket socket)
    {
        try {
            this.userSocket = socket;
            this.inputStream = new ObjectInputStream(this.userSocket.getInputStream());
            this.outputStream = new ObjectOutputStream(this.userSocket.getOutputStream());
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
                            outputStream.writeObject(new Respond("Respond", 401, "Неправильный пароль", java.time.LocalDateTime.now()));
                    }
                    else
                        outputStream.writeObject(new Respond("Respond", 401, "Такого пользователя не существует", java.time.LocalDateTime.now()));
            }
            else if (authMessage.getType().equals("Register"))
            {
                    namePass.put(authMessage.getClientName(), authMessage.getPassword());
                    outputStream.writeObject(new Respond("Respond", 201, "Аккаунт успешно создан", java.time.LocalDateTime.now()));
                    this.outputStream.close();
                    this.inputStream.close();
                    this.userSocket.close();
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
