package ledokolmessenger.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import ledokolmessenger.client.ui.MainWindow;
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
    private Queue<SendableObject> activities = new LinkedList<>();

    public Client(Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream, String clientName, Database db) {
        this.socket = socket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.clientName = clientName;
        this.db = db;
        StartServer.addClient(this);

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if(!activities.isEmpty())
                    {
                        outputStream.writeObject(activities);
                        activities.clear();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 0, 100);
    }

    public String getClientName() {
        return clientName;
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
                    System.out.println(foundUser.getClientName());
                    if (foundUser == null) {
                        activities.add(new Respond("Respond", 404, "Пользователь не найден", java.time.LocalDateTime.now()));
                    } else if (foundUser.getType().equals("#friendavailable#")) {
                        activities.add(new Respond("Respond", 404, "Пользователь уже в друзьях", java.time.LocalDateTime.now()));
                    } else {
                        activities.add(new Respond("Respond", 200, "Пользователь " + foundUser.getClientName() + " добавлен в друзья", java.time.LocalDateTime.now()));
                        //outputStream.writeObject(foundUser);
                    }
                }

                if (request.getType().equals("getOldMessages")) {
                    ClientInfo request1 = (ClientInfo) request;
                    MessageList oldMessages = db.getOldMessages(this.clientName, request1.getClientName());
                    if (oldMessages != null) {
                        activities.add(oldMessages);
                    }
                }

                if (request.getType().equals("Message")) {
                    Message message = (Message) request; //inputStream.readObject();
                    if (message.getMessage().equalsIgnoreCase("##session##end##")) {
                        break;
                    }
                    System.out.println(message.getMessage());
                    activities.add(message);
                }

                if (request.getType().equals("Update")) {
                    activities.add(new Respond("Update", 200, "Updated", java.time.LocalDateTime.now()));
                }
            }
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.close();
        }
    }

    public void sendMessage(Message message) {

        Client client = (Client) StartServer.getClientsOnline().get(message.getRecipient());

        try {
            db.sendMessage(message);
            if (client != null) {
                client.outputStream.writeObject(message);
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        StartServer.getClientsOnline().remove(this.clientName);
        try {
            this.inputStream.close();
            this.outputStream.close();
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
