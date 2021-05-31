/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ledokolmessenger.server;

import ledokolmessenger.serialized.*;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aleks
 */
public class Database {

    Statement st;

    public Database(Statement st) {
        this.st = st;
    }

    public String SignIn(ClientInfo authMessage) throws SQLException {

        String s = "SELECT * from users where login = '" + authMessage.getClientName() + "'";

        ResultSet resultSet = st.executeQuery(s);
        if (!resultSet.isBeforeFirst()) {
            return "Такого пользователя не существует";
        }

        //throw new SQLException("Phone Number Or Password Is Incorrect");
        //System.out.println(authMessage.getPassword());
        while (resultSet.next()) {
            String title = resultSet.getString("login");
            String password = resultSet.getString("password");
            //System.out.println(authMessage.getPassword());
            //System.out.println(password);
            if (authMessage.getPassword().equals(password)) {
                s = "UPDATE users SET is_online = " + true + " WHERE login = '" + authMessage.getClientName() + "'";
                st.executeUpdate(s);
                return "OK";
            } else {
                //System.out.println(password);
                //System.out.println(authMessage.getPassword());
                return "Неверный пароль";
            }
        }

        return "ничего";
    }

    public void Register(ClientInfo authMessage) throws SQLException {

        String login = authMessage.getClientName();
        String pass = authMessage.getPassword();
        boolean is_online = false;

        String s = "INSERT into users"
                + " values ('" + login + "', '" + pass + "', '" + is_online + " ')";

        st.execute(s);

    }

    public List<ClientInfo> getListFriends(String id) throws SQLException {
        List<ClientInfo> listfr = new ArrayList<ClientInfo>();

        //String s = "SELECT * from users where login != '" + id + "'";
        String s = "SELECT * from users JOIN list_friends ON list_friends.user2 = login where list_friends.user1 = '" + id + "' and status=true";
        ResultSet resultSet = st.executeQuery(s);
        if (!resultSet.isBeforeFirst()) {
            return listfr;
        }

        while (resultSet.next()) {
            String name = resultSet.getString("login");
            boolean is_online = resultSet.getBoolean("is_online");
            ClientInfo user = new ClientInfo("ListFriends", name, is_online);
//            System.out.println(user.getClientName());
            listfr.add(user);
        }

        return listfr;
    }

    public List<ClientInfo> getListFriendRequests(String id) throws SQLException {
        List<ClientInfo> listfr = new ArrayList<ClientInfo>();

        //String s = "SELECT * from users where login != '" + id + "'";
        String s = "SELECT * from users JOIN list_friends ON list_friends.user1 = login where list_friends.user2 = '" + id + "' and status=false";
        ResultSet resultSet = st.executeQuery(s);
        if (!resultSet.isBeforeFirst()) {
            return listfr;
        }

        while (resultSet.next()) {
            String name = resultSet.getString("login");
            boolean is_online = resultSet.getBoolean("is_online");
            ClientInfo user = new ClientInfo("ListFriendRequest", name, is_online);
//            System.out.println(user.getClientName());
            listfr.add(user);
        }

        return listfr;
    }

    public List<ClientInfo> getListGroups(String id) throws SQLException {
        List<ClientInfo> listfr = new ArrayList<ClientInfo>();

        String s = "SELECT * from groups JOIN group_users ON group_id = name where user_id = '" + id + "'";
        ResultSet resultSet = st.executeQuery(s);
        if (!resultSet.isBeforeFirst()) {
            return listfr;
        }

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            ClientInfo user = new ClientInfo("ListGroups", name);
            listfr.add(user);
        }

        return listfr;
    }

    public ClientInfo getUser(String id) throws SQLException {
        ClientInfo user = null;

        String s = "SELECT * from users where login = '" + id + "'";
        ResultSet resultSet = st.executeQuery(s);
        if (!resultSet.isBeforeFirst()) {
            return null;
        }

        while (resultSet.next()) {
            String name = resultSet.getString("login");
            boolean is_online = resultSet.getBoolean("is_online");
            user = new ClientInfo("Friend", name, is_online);
            System.out.println(user.getClientName());
        }
        return user;
    }

    public ClientInfo CheckUserFriends(String id, String Myself) throws SQLException {
        String s = "SELECT * from users where login = '" + id + "'";
        ClientInfo user = null;
        ResultSet resultSet = st.executeQuery(s);
        if (!resultSet.isBeforeFirst()) {
            return new ClientInfo("##notFound##", id);
        }

        while (resultSet.next()) {
            String name = resultSet.getString("login");
            boolean is_online = resultSet.getBoolean("is_online");
            user = new ClientInfo("getUser", name, is_online);
        }

        if (Myself.equals(id)) {
            return new ClientInfo("##It##is##you##", user.getClientName());
        }

        String s1 = "user1='" + Myself + "' AND user2='" + id + "'";
        String s2 = "user1='" + id + "' AND user2='" + Myself + "'";
        s = "SELECT * from list_friends where " + s1 + " AND status=true";
        resultSet = st.executeQuery(s);
        if (resultSet.isBeforeFirst()) {
            return new ClientInfo("##friend##available##", user.getClientName());
        }
        s = "SELECT * from list_friends where " + s1 + " AND status=false";
        resultSet = st.executeQuery(s);
        if (resultSet.isBeforeFirst()) {
            return new ClientInfo("##request##sent##", user.getClientName());
        }

        s = "SELECT * from list_friends where " + s2 + " AND status=false";
        resultSet = st.executeQuery(s);
        if (resultSet.isBeforeFirst()) {
            return new ClientInfo("##check##request##", user.getClientName());
        }

        s = "INSERT into list_friends values ('" + Myself + "', '" + id + "',false)";
        st.execute(s);
        return user;
    }

    public ClientInfo approveFriend(String Myself, String id, boolean is_status) throws SQLException {
        if (is_status == true) {
            String s = "INSERT into list_friends values ('" + Myself + "', '" + id + "',true)";
            st.execute(s);
            s = "UPDATE list_friends SET status=true WHERE user1 = '" + id + "' AND user2 = '" + Myself + "'";
            st.executeUpdate(s);
            return getUser(id);
        } else {
            String s = "DELETE from list_friends where user1 = '" + id + "' AND user2 = '" + Myself + "' and status = false";
            st.execute(s);
            return null;
        }
    }

    public MessageList getOldMessages(String myLogin, String userLogin) throws SQLException {
        List<Message> oldMessages = new ArrayList<>();

        String S = "SELECT * FROM messages where ("
                + "sender ='" + myLogin + "' AND recipient ='" + userLogin + "') OR ("
                + "sender ='" + userLogin + "' AND recipient ='" + myLogin + "')";

        ResultSet resultSet = st.executeQuery(S);

        if (!resultSet.isBeforeFirst()) {
            return null;
        }

        while (resultSet.next()) {
            String sender = resultSet.getString("sender");
            String recipient = resultSet.getString("recipient");
            String content = resultSet.getString("content");
            Timestamp ts = resultSet.getTimestamp("date_create");
            LocalDateTime date_create = null;
            if (ts != null) {
                date_create = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
            }

            Message message = new Message("Message", content, sender, recipient, date_create);
            oldMessages.add(message);
        }
        MessageList messages = new MessageList("OldMessages", oldMessages);

        return messages;
    }

    public Message sendMessage(Message mess) throws SQLException {
        //LocalDateTime localDateTime = LocalDateTime.now();

        String s
                = "INSERT INTO messages (sender, recipient , content, date_create) "
                + "VALUES ( '" + mess.getSender() + "', '" + mess.getRecipient() + "', '" + mess.getMessage() + "', '" + mess.getTime() + "' ) ";

        st.execute(s);

        return mess;
    }

    public ClientInfo CreateGroup(String myLogin, String nameGroup) throws SQLException {
        String s = "SELECT * from groups where name = '" + nameGroup + "'";
        ClientInfo group = null;
        ResultSet resultSet = st.executeQuery(s);
        if (resultSet.isBeforeFirst()) {
            return new ClientInfo("##name##is##taken##", nameGroup);
        }

        s = "INSERT into groups values ('" + nameGroup + "', '" + myLogin + "')";
        st.execute(s);

        s = "INSERT into group_users values ('" + nameGroup + "', '" + myLogin + "')";
        st.execute(s);
        return new ClientInfo(nameGroup, myLogin);
    }

    public MessageList getOldMessagesGroup(String nameGroup) throws SQLException {
        List<Message> oldMessages = new ArrayList<>();

        String S = "SELECT * FROM messages_group where recipient ='" + nameGroup + "'";

        ResultSet resultSet = st.executeQuery(S);

        if (!resultSet.isBeforeFirst()) {
            return null;
        }

        while (resultSet.next()) {
            String sender = resultSet.getString("sender");
            String recipient = resultSet.getString("recipient");
            String content = resultSet.getString("content");
            Timestamp ts = resultSet.getTimestamp("date_create");
            LocalDateTime date_create = null;
            if (ts != null) {
                date_create = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);
            }

            Message message = new Message("Message", content, sender, recipient, date_create);
            oldMessages.add(message);
        }
        MessageList messages = new MessageList("OldMessagesGroup", oldMessages);

        return messages;
    }

    public List<ClientInfo> sendMessageGroup(Message mess) throws SQLException {
        String s
                = "INSERT INTO messages_group (sender, recipient , content, date_create) "
                + "VALUES ( '" + mess.getSender() + "', '" + mess.getRecipient() + "', '" + mess.getMessage() + "', '" + mess.getTime() + "' ) ";
        st.execute(s);

        List<ClientInfo> listfr = new ArrayList<ClientInfo>();

        s = "SELECT * from users JOIN group_users ON user_id = login where group_id = '" + mess.getRecipient() + "'";
        ResultSet resultSet = st.executeQuery(s);
        if (!resultSet.isBeforeFirst()) {
            return listfr;
        }
        while (resultSet.next()) {
            String name = resultSet.getString("login");
            ClientInfo user = new ClientInfo("Members", name);
            listfr.add(user);
        }

        return listfr;
    }

    public ClientInfo FindGroup(String myLogin, String nameGroup) throws SQLException {
        String s = "SELECT * from groups where name = '" + nameGroup + "'";
        ClientInfo group = null;
        ResultSet resultSet = st.executeQuery(s);
        if (!resultSet.isBeforeFirst()) {
            return new ClientInfo("##notFound##", nameGroup);
        }

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            group = new ClientInfo("Group", name);
        }

        String s1 = "group_id='" + nameGroup + "' AND user_id='" + myLogin + "'";
        s = "SELECT * from group_users where " + s1;
        resultSet = st.executeQuery(s);
        if (resultSet.isBeforeFirst()) {
            return new ClientInfo("##group##available##", group.getClientName());
        }

        s = "INSERT into group_users values ('" + nameGroup + "', '" + myLogin + "')";
        st.execute(s);
        return group;
    }

}
