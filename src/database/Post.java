package database;

import javafx.util.Pair;
import server.Commands;
import util.Util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Post {

    /**
     * returns the problem data.
     * <p>
     * Failure codes
     * <ul>
     * <li> 0: Server Error
     * <li> 1: Problem not found.
     * </ul>
     *
     * @param problemID problem id to get
     * @return response data
     */

    public synchronized Pair<Boolean, Object> getProblem(int problemID) {
        try {
            ResultSet rs = Database.st.executeQuery("SELECT * FROM post WHERE id='" + problemID + "'");
            if (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                String body = rs.getString("body");
                String date = rs.getDate("date").toString();
                return new Pair<>(true, new String[]{title, author, body, date});
            }
            return new Pair<>(false, 1);
        } catch (SQLException e) {
            Util.log("database", "Error on getting problem with id: " + problemID, Commands.ERR);
        }
        return new Pair<>(false, 0);
    }

    /**
     * Make new Problem given.
     * <p>
     * Failure codes
     * <ul>
     * <li> 0: Server Error
     * <li> 1: Id is not valid
     * <li> 2: token is not valid
     * </ul>
     *
     * @param id    id that made problem
     * @param tk    token to verify
     * @param title title of problem
     * @param body  body of problem
     * @return response data
     */

    public synchronized Pair<Boolean, Object> setProblem(String id, String tk, String title, String body) {
        try {
            ResultSet rs = Database.st.executeQuery("SELECT token FROM user WHERE id='" + id + "'");
            if (rs.next()) {
                String t = rs.getString("token");
                if (!rs.wasNull() && t.equals(tk)) {
                    t = Util.token.generate(id);
                    Database.st.executeUpdate("UPDATE user SET token='" + t + "' WHERE id='" + id + "'");
                    Database.st.executeUpdate("INSERT INTO post (type, title, author, date, body, tags, category) VALUES (1, '" + title + "', '" + id + "', curdate(), '" + body + "', '', '')");
                    return new Pair<>(true, t);
                }
                return new Pair<>(false, 2);
            }
            return new Pair<>(false, 1);
        } catch (SQLException e) {
            Util.log("database", "Error on setting problem with id: " + id, Commands.ERR);
        }
        return new Pair<>(false, 0);
    }
}
