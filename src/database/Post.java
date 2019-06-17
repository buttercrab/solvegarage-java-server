package database;

import javafx.util.Pair;
import server.Commands;
import util.Util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Post {

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
}
