package database;

import javafx.util.Pair;
import server.Commands;
import util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class User {

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
            ResultSet rs = Database.st.executeQuery("SELECT id FROM user WHERE id='" + id + "'");
            if (!rs.next()) {
                String token = Util.token.generate(id);
                Database.st.executeUpdate("INSERT INTO user (id, pw, token) VALUES ('" + id + "', '" + pw + "', '" + token + "')");
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
            ResultSet rs = Database.st.executeQuery("SELECT pw FROM user WHERE id='" + id + "'");
            if (rs.next()) {
                String _pw = rs.getString("pw");
                if (_pw.equals(pw)) {
                    String token = Util.token.generate(id);
                    Database.st.executeUpdate("UPDATE user SET token='" + token + "' WHERE id='" + id + "'");
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
            ResultSet rs = Database.st.executeQuery("SELECT token FROM user WHERE id='" + id + "'");
            if (rs.next()) {
                String saved = rs.getString("token");
                if (!rs.wasNull() && Util.token.verify(token, saved)) {
                    token = Util.token.generate(id);
                    Database.st.executeUpdate("UPDATE user SET token='" + token + "' WHERE id='" + id + "'");
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

    public synchronized Pair<Boolean, Integer> logout(String id, String tk) {
        try {
            ResultSet rs = Database.st.executeQuery("SELECT token FROM user WHERE id='" + id + "'");
            if (rs.next()) {
                if (tk.equals(rs.getString("token")) && !rs.wasNull()) {
                    Database.st.executeUpdate("UPDATE user SET token=NULL WHERE id='" + id + "'");
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
            ResultSet rs = Database.st.executeQuery("SELECT pw FROM user WHERE id='" + id + "'");
            if (rs.next()) {
                String p = rs.getString("pw");
                if (p.equals(pw)) {
                    Database.st.executeUpdate("DELETE FROM user WHERE id='" + id + "'");
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
     * Is sets the profile image to database
     * It would send the response data
     * <p>
     * Failure codes
     * <ul>
     * <li> 0: Server error. An error occurred when processing login.
     * <li> 1: id is not found.
     * <li> 2: token is not correct.
     * </ul>
     *
     * @param id  id to change the image
     * @param tk  token to verify
     * @param img image data on base64 to save
     * @return response data
     */

    public synchronized Pair<Boolean, Object> setImage(String id, String tk, String img) {
        try {
            ResultSet rs = Database.st.executeQuery("SELECT token FROM user WHERE id='" + id + "'");
            if (rs.next()) {
                String t = rs.getString("token");
                if (!rs.wasNull() && t.equals(tk)) {
                    t = Util.token.generate(id);
                    Database.st.executeUpdate("UPDATE user SET token='" + t + "' WHERE id='" + id + "'");
                    File file = new File("/Users/jaeyong/Github/solvegarage-java/data/profile-img/" + id + ".png");
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(Base64.getDecoder().decode(img));
                    out.flush();
                    out.close();

                    return new Pair<>(true, t);
                }
                return new Pair<>(false, 2);
            }
            return new Pair<>(false, 1);
        } catch (Exception e) {
            Util.log("database", "Error on setting image with id: " + id, Commands.ERR);
        }
        return new Pair<>(false, 0);
    }

    /**
     * returns the image of id
     * <p>
     * Failure codes
     * <ul>
     * <li> 0: Server error. An error occurred when processing login.
     * <li> 1: id is not found.
     * <li> 2: id exists but image was not found.
     * </ul>
     *
     * @param id id to get image
     * @return response data
     */

    public synchronized Pair<Boolean, Object> getImage(String id) {
        try {
            ResultSet rs = Database.st.executeQuery("SELECT id FROM user WHERE id='" + id + "'");
            if (rs.next()) {
                try {
                    File file = new File("/Users/jaeyong/Github/solvegarage-java/data/profile-img/" + id + ".png");
                    byte[] data = new byte[(int) file.length()];
                    FileInputStream in = new FileInputStream(file);
                    //noinspection ResultOfMethodCallIgnored
                    in.read(data);
                    in.close();

                    return new Pair<>(true, Base64.getEncoder().encodeToString(data));
                } catch (Exception e) {
                    return new Pair<>(false, 2);
                }
            }
            return new Pair<>(false, 1);
        } catch (SQLException e) {
            Util.log("database", "Error on getting image with id: " + id, Commands.ERR);
        }
        return new Pair<>(false, 0);
    }
}
