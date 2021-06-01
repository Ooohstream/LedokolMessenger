/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ledokolmessenger.server;

/**
 *
 * @author aleks
 */
public class ConnectionInfo {
    private final String user;
    private final String pwd;
    private final String dbUrl;
    private final String drvName;

    public ConnectionInfo(String user, String pwd, String dbUrl, String drvName) {
        this.user = user;
        this.pwd = pwd;
        this.dbUrl = dbUrl;
        this.drvName = drvName;
    }

    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDrvName() {
        return drvName;
    }
    
    
    
}
