package database;

import server.Commands;
import server.Server;
import util.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    static Statement st = null;
    private static Connection conn = null;

    /**
     * Connects to local MySQL server and make connection and statement.
     * Have to be called when starting the server.
     *
     * @param id id to login to local MySQL server
     * @param pw password to login to local MySQL server
     */

    public static void init(String id, String pw) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/solvegarage_java?characterEncoding=UTF-8&serverTimezone=UTC", id, pw);

            st = conn.createStatement();

            Util.log("database", "Succeeded on connecting to MySQL server.", Commands.LOG);
        } catch (ClassNotFoundException e) {
            Util.log("database", "Driver not found", Commands.ERR);
        } catch (Exception e) {
            e.printStackTrace();
            ((Commands) Server.cmd).quit();
        }
    }

    /**
     * Close the connection to MySQL server.
     * Used when shutting down the server.
     */

    public static void quit() {
        try {
            st.close();
            conn.close();
        } catch (SQLException e) {
            Util.log("database", "Error closing database connection", Commands.ERR);
        }
    }
}
