package ledokolmessenger.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ledokolmessenger.serialized.*;

/**
 *
 * @author BOT
 */
public class Client implements Runnable {

    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    private final String clientName;
    private Database db;

    public Client(Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream, String clientName, Database db) {
        this.socket = socket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.clientName = clientName;
        this.db = db;
    }

    @Override
    public void run() {
        System.out.println(this.clientName + " подключился");
        try {
            outputStream.writeObject(db.getListFriends(this.clientName));
            while (true) {
                SendableObject request = (SendableObject) inputStream.readObject();

                if (request.getType().equals("addFriend")) {
                    ClientInfo request1 = (ClientInfo) request;
                    ClientInfo foundUser = db.addUser(request1.getClientName(), this.clientName);

                    if (foundUser == null) {
                        outputStream.writeObject(new Respond("Respond", 404, "Пользователь не найден", java.time.LocalDateTime.now()));
                    } else {
                        outputStream.writeObject(new Respond("Respond", 200, "Пользователь " + foundUser.getClientName() + " добавлен в друзья", java.time.LocalDateTime.now()));
                    }
                } else {
                    Message message = (Message) inputStream.readObject();
                    if (message.getMessage().equalsIgnoreCase("##session##end##")) {
                        break;
                    }
                    this.sendMessage(message);
                }
            }
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
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
