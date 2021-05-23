/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ledokolmessenger.server;

import ledokolmessenger.serialized.ClientInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        String s = "SELECT * from users JOIN list_friends ON list_friends.user2 = login where list_friends.user1 = '"+id+"'";
        ResultSet resultSet = st.executeQuery(s);
        if (!resultSet.isBeforeFirst()) {
            return listfr;
        }

        while (resultSet.next()) {
            String name = resultSet.getString("login");
            boolean is_online = resultSet.getBoolean("is_online");
            ClientInfo user = new ClientInfo("ListFriends", name, is_online);
            System.out.println(user.getClientName());
            listfr.add(user);
        }

        return listfr;
    }

     public String addUser(String id,String Myself) throws SQLException {

        
        String s = "SELECT * from users where login = '" + id + "'";
        ClientInfo user = null;
        ResultSet resultSet = st.executeQuery(s);
        if (!resultSet.isBeforeFirst()) {
            return null;
        }

        while (resultSet.next()) {
            String name = resultSet.getString("login");
            boolean is_online = resultSet.getBoolean("is_online");
            user = new ClientInfo("getUser", name, is_online);
            //System.out.println(user.getClientName());
        }
        s ="SELECT * from list_friends where user1='"+ Myself +"' AND user2='"+ id +"'";
        resultSet = st.executeQuery(s);
        if (resultSet.isBeforeFirst()) {
            return "#friendavailable#";
        }
        s ="SELECT * from list_friends where user1='"+ id +"' AND user2='"+ Myself +"'";
        resultSet = st.executeQuery(s);
        if (resultSet.isBeforeFirst()) {
            return "#friendavailable#";
        }
        
        s ="INSERT into list_friends values ('" + Myself +"', '" + id +"')";
        st.execute(s);
        s ="INSERT into list_friends values ('" + id +"', '" + Myself +"')";
        st.execute(s);
        return user.getClientName();
    }
    

}
