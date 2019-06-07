package database;

import javafx.util.Pair;
import server.Commands;
import server.Server;
import util.Util;

import java.sql.*;

public class User {

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

    /**
     * Method to make new account. Gets the id and password and make new account
     * if id had not been used. It generates token and save into database. It would
     * return JSON with response data.
     * <p>
     * If register succeeded, key is true and value is String which contains the
     * token. If register is not successful, key is false, value is Integer which
     * contains the failure code.
     * <p>
     * Failure codes
     * <ul>
     * <li> 0: Server error. An error occurred when processing login.
     * <li> 1: Username is already used.
     * </ul>
     *
     * @param id id to register
     * @param pw password to register
     * @return Pair with if register succeeded and code or token
     */

    public synchronized Pair<Boolean, Object> register(String id, String pw) {
        try {
            ResultSet rs = this.st.executeQuery("SELECT id FROM user_data WHERE id='" + id + "'");
            if (!rs.next()) {
                String token = Util.token.generate(id);
                this.st.executeUpdate("INSERT INTO user_data (id, pw, tk) VALUES ('" + id + "', '" + pw + "', '" + token + "')");
                return new Pair<>(true, token);
            }
            return new Pair<>(false, 1);
        } catch (SQLException e) {
            Util.log("database", "Error on register with id: " + id, Commands.ERR);
        }
        return new Pair<>(false, 0);
    }

    /**
     * Method to login to server. Gets the id and password and verify by database.
     * When login succeeded, it generates token and save into database. It would
     * return JSON with response data.
     * <p>
     * If login succeeded, key is true and value is String which contains the
     * token. If login is not successful, key is false, value is Integer which
     * contains the failure code.
     * <p>
     * Failure codes
     * <ul>
     * <li> 0: Server error. An error occurred when processing login.
     * <li> 1: id is not found.
     * <li> 2: Password is incorrect.
     * </ul>
     *
     * @param id id to login
     * @param pw password to login
     * @return Pair with if register succeeded and code or token
     */

    public synchronized Pair<Boolean, Object> login(String id, String pw) {
        try {
            ResultSet rs = this.st.executeQuery("SELECT pw FROM user_data WHERE id='" + id + "'");
            if (rs.next()) {
                String _pw = rs.getString("pw");
                if (_pw.equals(pw)) {
                    String token = Util.token.generate(id);
                    this.st.executeUpdate("UPDATE user_data SET tk='" + token + "' WHERE id='" + id + "'");
                    return new Pair<>(true, token);
                }
                return new Pair<>(false, 2);
            }
            return new Pair<>(false, 1);
        } catch (SQLException e) {
            Util.log("database", "Error on login with id: " + id, Commands.ERR);
        }
        return new Pair<>(false, 0);
    }

    /**
     * Does similar thing with login function. {@link User#login(String, String)}
     * It login with token instead of password. It has same response data format.
     * It would regenerate the token and send back the new token.
     * <p>
     * Failure codes
     * <ul>
     * * <li> 0: Server error. An error occurred when processing login.
     * <li> 1: id is not found.
     * <li> 2: Token is not correct.
     * </ul>
     *
     * @param id    id to login
     * @param token token to login
     * @return Pair with if register succeeded and code or token
     */

    public synchronized Pair<Boolean, Object> loginWithToken(String id, String token) {
        try {
            ResultSet rs = this.st.executeQuery("SELECT tk FROM user_data WHERE id='" + id + "'");
            if (rs.next()) {
                String saved = rs.getString("tk");
                if (!rs.wasNull() && Util.token.verify(token, saved)) {
                    token = Util.token.generate(id);
                    this.st.executeUpdate("UPDATE user_data SET tk='" + token + "' WHERE id='" + id + "'");
                    return new Pair<>(true, token);
                }
                return new Pair<>(false, 2);
            }
            return new Pair<>(false, 1);
        } catch (SQLException e) {
            Util.log("database", "Error on login with id: " + id, Commands.ERR);
        }
        return new Pair<>(false, 0);
    }

    /**
     * It removes the token to verify from database.
     * it would send response data.
     * <p>
     * Failure codes
     * <ul>
     * <li> 0: Server error. An error occurred when processing login.
     * <li> 1: id is not found.
     * <li> 2: Already logged out.
     * </ul>
     *
     * @param id id to logout
     * @return Pair with if register succeeded and code or token
     */

    public synchronized Pair<Boolean, Integer> logout(String id) {
        try {
            ResultSet rs = this.st.executeQuery("SELECT tk FROM user_data WHERE id='" + id + "'");
            if (rs.next()) {
                rs.getString("tk");
                if (!rs.wasNull()) {
                    this.st.executeUpdate("UPDATE user_data SET tk=NULL WHERE id='" + id + "'");
                    return new Pair<>(true, -1);
                }
                return new Pair<>(false, 2);
            }
            return new Pair<>(false, 1);
        } catch (SQLException e) {
            Util.log("database", "Error on logout with id: " + id, Commands.ERR);
        }
        return new Pair<>(false, 0);
    }

    /**
     * It removes the account from the database.
     * It would send response data.
     * <p>
     * Failure codes
     * <ul>
     * <li> 0: Server error. An error occurred when processing login.
     * <li> 1: id is not found.
     * <li> 2: password is not correct.
     * </ul>
     *
     * @param id id to delete account
     * @param pw password to the account
     * @return Pair with if register succeeded and code or token
     */

    public synchronized Pair<Boolean, Integer> deleteAccount(String id, String pw) {
        try {
            ResultSet rs = this.st.executeQuery("SELECT pw FROM user_data WHERE id='" + id + "'");
            if (rs.next()) {
                String p = rs.getString("pw");
                if (p.equals(pw)) {
                    this.st.executeUpdate("DELETE FROM user_data WHERE id='" + id + "'");
                    return new Pair<>(true, -1);
                }
                return new Pair<>(false, 2);
            }
            return new Pair<>(false, 1);
        } catch (SQLException e) {
            Util.log("database", "Error on deleting an account: " + id, Commands.ERR);
        }
        return new Pair<>(false, 0);
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
