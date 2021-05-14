/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ledokolmessenger.server;

import ledokolmessenger.serialized.ClientInfo;
import ledokolmessenger.serialized.Respond;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aleks
 */
public class Database {
    Statement st;
    public Database(Statement st) {
        this.st = st;       
    }
    
    
    
    public String SignIn(ClientInfo authMessage) throws SQLException
    {
        
        String s = "SELECT * from users where login = '" + authMessage.getClientName() + ", password = '"+authMessage.getPassword() +"'";
        
        ResultSet resultSet = st.executeQuery(s);
        if (! resultSet.isBeforeFirst())
            return "No such client exists";
        
        //throw new SQLException("Phone Number Or Password Is Incorrect");
        
        //while(resultSet.next())
        //{
            //String title = resultSet.getString("login");
            String password = resultSet.getString("password");
            System.out.println(authMessage.getPassword());
            if(authMessage.getPassword() == password){
                return "OK";}
            else {
                System.out.println(password);
                System.out.println(authMessage.getPassword());
                return "Invalid password";
        }
        //}
    }
    
    public void Register(ClientInfo authMessage) throws SQLException{
        
        String login = authMessage.getClientName();
        String pass = authMessage.getPassword();
        boolean is_online = false;
        
        String s = "INSERT into users" +
                   " values ('" + login + "', '" + pass + "', '" + is_online + " ')";
        
        st.execute(s);
        
    }
    
    
}
