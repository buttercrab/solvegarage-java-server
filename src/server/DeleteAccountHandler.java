package server;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import database.User;
import javafx.util.Pair;
import util.Util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeleteAccountHandler extends SecureHttpHandler {

    /**
     * This method is used for handling delete-account requests.
     * Using POST method, gets the id and password to logout
     * and calls the delete account function.
     * {@link User#deleteAccount(String, String)}
     * <p>
     * input is JSON object from client that has `id`,
     * and `key` property.
     * <ul>
     * <li> `id` id to delete-account
     * <li> `pw` password to delete-account
     * </ul>
     * When the delete-account is success, it will send back
     * JSON object that has `success` property
     * <ul>
     * <li> `success` true if delete-account succeeded
     * <li> `code` failure code when delete-account failed
     * </ul>
     *
     * @param exchange http exchange object
     * @throws IOException from BufferedReader when input is not properly working.
     */

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Pair<JsonObject, Byte[]> root = super.handleInit(exchange);

        String id = root.getKey().get("id").getAsString();
        String pw = root.getKey().get("pw").getAsString();

        Pair<Boolean, Integer> t = Server.user.deleteAccount(id, pw);
        String res = "{'success':" + t.getKey();
        if (t.getValue() != -1)
            res += ",'code':" + t.getValue() + "}";
        else
            res += "}";

        if (Server.debugLevel >= 2) {
            Util.log("server", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) +
                    " /delete-account POST 200 id='" + id + "' response='" + res + "'", Commands.LOG);
        }

        super.send(exchange, res, root.getValue());
    }
}
