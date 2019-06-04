package database;

import server.Commands;
import server.Server;
import util.Util;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    private Connection conn = null;

    public void init(String id, String pw) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/solvegarage_java?characterEncoding=UTF-8&serverTimezone=UTC", id, pw);

            Util.log("Succeeded on Connecting to MySQL Server.", Commands.LOG);
        } catch(ClassNotFoundException e) {
            Util.log("Driver not Found", Commands.ERR);
        } catch(Exception e) {
            e.printStackTrace();
            ((Commands) Server.cmd).quit();
        }
    }

    public void register(String id, String pw) {

    }

    public String login(String id, String pw) {
        return "";
    }

    public void logout(String id) {

    }

    public void quit() {

    }
}
