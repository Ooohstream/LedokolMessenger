package ledokolmessenger.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedList;
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
    }

    public String getClientName() {
        return clientName;
    }

    @Override
    public void run() {

        Timer sender = new Timer();

        sender.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!activities.isEmpty()) {
                        outputStream.writeUnshared(activities);
                        activities.clear();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 0, 100);

        System.out.println(this.clientName + " подключился");
        try {
            outputStream.writeObject(db.getListFriends(this.clientName));
            outputStream.writeObject(db.getListFriendRequests(this.clientName));
            outputStream.writeObject(db.getListGroups(this.clientName));
            while (true) {
                SendableObject request = (SendableObject) inputStream.readObject();

                if (request.getType().equals("addFriend")) {
                    ClientInfo user = (ClientInfo) request;
                    ClientInfo foundUser = db.CheckUserFriends(user.getClientName(), this.clientName);
                    System.out.println(foundUser.getClientName());
                    switch (foundUser.getType()) {
                        case "##notFound##":
                            activities.add(new Respond("Respond", 404, "Пользователь не найден", java.time.LocalDateTime.now()));
                            break;
                        case "##It##is##you##":
                            activities.add(new Respond("Respond", 404, "Нельзя добавить самого себя", java.time.LocalDateTime.now()));
                            break;
                        case "##friend##available##":
                            activities.add(new Respond("Respond", 404, "Пользователь уже в друзьях", java.time.LocalDateTime.now()));
                            break;
                        case "##request##sent##":
                            activities.add(new Respond("Respond", 404, "Заявка уже была отправлена", java.time.LocalDateTime.now()));
                            break;
                        case "##check##request##":
                            activities.add(new Respond("Respond", 404, "Проверьте заявки на дружбу", java.time.LocalDateTime.now()));
                            break;
                        default:
                            activities.add(new Respond("Respond", 200, "Заявка на дружбу с " + foundUser.getClientName() + " отправлена", java.time.LocalDateTime.now()));
                            sendMessage(new Message("RequestFriend", "Заявка на дружбу", this.clientName, foundUser.getClientName(), java.time.LocalDateTime.now()));
                            //Client client = (Client) StartServer.getClientsOnline().get(message.getRecipient());
                            //outputStream.writeObject(foundUser);
                            break;
                    }
                }
               
                if (request.getType().equals("approveFriend")) {
                    ClientInfo message = (ClientInfo) request;
                    ClientInfo user = db.approveFriend(this.clientName, message.getClientName(), message.getIs_online());
                    if (user != null) {
                        sendMessage(new Message("approveRequestFriend", this.clientName + " принял заявку в друзья", this.clientName, user.getClientName(), java.time.LocalDateTime.now()));
                    }
                }

                if (request.getType().equals("getOldMessages")) {
                    ClientInfo request1 = (ClientInfo) request;
                    MessageList oldMessages = db.getOldMessages(this.clientName, request1.getClientName());
                    if (oldMessages != null) {
                        activities.add(oldMessages);
                    } else {
                        activities.add(new MessageList("OldMessages", null));
                    }
                }

                if (request.getType().equals("Message")) {
                    Message message = (Message) request;
                    if (message.getMessage().equalsIgnoreCase("##session##end##")) {
                        break;
                    }
                    this.sendMessage(message);
                }
                
                if (request.getType().equals("createGroup")){
                    ClientInfo group = (ClientInfo) request;
                    ClientInfo createdGroup = db.CreateGroup(this.clientName,group.getClientName());
                    if(createdGroup.getType().equals("##name##is##taken##"))
                    {
                        activities.add(new Respond("Respond", 404, "Такое название уже занято", java.time.LocalDateTime.now()));
                    }
                    else
                    {
                       activities.add(new Respond("Respond", 200, "Группа создана", java.time.LocalDateTime.now())); 
                    }
                }
                
                if (request.getType().equals("getOldMessagesGroup")) {
                    ClientInfo request1 = (ClientInfo) request;
                    MessageList oldMessages = db.getOldMessagesGroup(this.clientName, request1.getClientName());
                    if (oldMessages != null) {
                        activities.add(oldMessages);
                    } else {
                        activities.add(new MessageList("OldMessages", null));
                    }
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
            if (message.getType().equals("Message")) {
                db.sendMessage(message);
            }
            if (client != null) {
                client.activities.add(message);
            }
        } catch (SQLException ex) {
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
