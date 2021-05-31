package ledokolmessenger.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BOT
 */
public class StartServer {

    private static final Integer PORT = 3443;
    private static final Map<String, Client> clientsOnline = new HashMap<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = null;
        Socket userSocket = null;
        ConnectionInfo info = new ConnectionInfo("postgres", System.getenv("PG_PASSWORD"), "jdbc:postgresql://localhost:5432/messenger", "org.postgresql.Driver");
        Connection con = null;
        try {
            serverSocket = new ServerSocket(PORT);
            Class.forName(info.getDrvName());
            con = DriverManager.getConnection(info.getDbUrl(), info.getUser(), info.getPwd());
            Statement st = con.createStatement();
            System.out.println("Сервер запущен");
            while (true) {
                userSocket = serverSocket.accept();
                new Thread(new Authentication(userSocket, st)).start();
            }
        } catch (IOException | SQLException ex) {
            Logger.getLogger(StartServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            serverSocket.close();
            System.out.println("Сервер был остановлен");
            if (con != null) try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(StartServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Сервер был остановлен");
        }

    }

    public static Map<String, Client> getClientsOnline() {
        return clientsOnline;
    }

    public static void addClient(Client client) {
        clientsOnline.put(client.getClientName(), client);
    }
}
