package database;

import server.Commands;
import server.Server;
import util.Util;

import java.sql.*;

public class Database {

    private Connection conn = null;
    private Statement st = null;

    /**
     * Connects to local MySQL server and make connection and statement.
     * Have to be called when starting the server.
     *
     * @param id id to login to local MySQL server
     * @param pw password to login to local MySQL server
     */

    public void init(String id, String pw) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/solvegarage_java?characterEncoding=UTF-8&serverTimezone=UTC", id, pw);

            this.st = conn.createStatement();

            Util.log("database", "Succeeded on connecting to MySQL server.", Commands.LOG);
        } catch (ClassNotFoundException e) {
            Util.log("database", "Driver not found", Commands.ERR);
        } catch (Exception e) {
            e.printStackTrace();
            ((Commands) Server.cmd).quit();
        }
    }

    public synchronized void register(String id, String pw) {

    }

    /**
     * Method to login to server. Gets the id and password and verify by database.
     * When login succeeded, it generates token and save into database. It would
     * return JSON with response data that has `success` field.
     * <p>
     * If `success` field is true, response data contains `token` field that has
     * token value with it. If it is false, response data contains `code` field
     * that has failure code with it.
     * <p>
     * Failure codes
     * <ul>
     * <li> 0: Server error. An error occurred when processing login.
     * <li> 1: Username is not found.
     * <li> 2: Password is incorrect.
     * </ul>
     *
     * @param id id to login
     * @param pw password to login
     * @return String with JSON format with response data.
     */

    public synchronized String login(String id, String pw) {
        try {
            ResultSet rs = this.st.executeQuery("SELECT id, pw FROM user_data WHERE id='" + id + "'");
            if (rs.next()) {
                String _pw = rs.getString("pw");
                if (_pw.equals(pw)) {
                    String token = Util.generateToken(id);
                    this.st.executeUpdate("UPDATE user_data SET tk='" + token + "' WHERE id='" + id + "'");
                    return "{'success':true,'token':'" + token + "'}";
                } else {
                    return "{'success':false,'code':2}";
                }
            } else {
                return "{'success':false,'code':1}";
            }
        } catch (SQLException e) {
            Util.log("database", "Error on login with id: " + id + ", pw: " + pw, Commands.ERR);
        }
        return "{'success':false,'code':0}";
    }

    /**
     * Does similar thing with login function. {@link Database#login(String, String)}
     * It login with token instead of password. It has same response data format.
     * It would regenerate the token and send back the new token.
     *
     * @param id    id to login
     * @param token token to login
     * @return String with JSON format with response data.
     */

    public synchronized String loginWithToken(String id, String token) {
        try {
            ResultSet rs = this.st.executeQuery("SELECT tk FROM user_data WHERE id='" + id + "'");
            if (rs.next()) {
                String saved = rs.getString("tk");
                if (!rs.wasNull()) {
                    if (Util.validateToken(token, saved)) {
                        token = Util.generateToken(id);
                        this.st.executeUpdate("UPDATE user_data SET tk='" + token + "' WHERE id='" + id + "'");
                        return "{'success':true,'token':'" + token + "'}";
                    }
                }
                return "{'success':false,'code':2}";
            } else {
                return "{'success':false,'code':1}";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "{'success':false,'code':0}";
    }

    public synchronized void logout(String id) {

    }

    /**
     * Close the connection to MySQL server.
     * Used when shutting down the server.
     */

    public void quit() {
        try {
            this.st.close();
            this.conn.close();
        } catch (SQLException e) {
            Util.log("database", "Error closing database connection", Commands.ERR);
        }
    }
}
