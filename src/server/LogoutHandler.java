package server;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import database.User;
import javafx.util.Pair;
import util.Util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogoutHandler extends SecureHttpHandler {

    /**
     * This method is used for handling logout requests.
     * Using POST method, gets logout data from client
     * and checks the user database logout function.
     * {@link User#logout(String, String)}
     * <p>
     * input is JSON object from client that has `id`,
     * and `key` property.
     * <ul>
     * <li> `id` id to logout
     * <li> `tk` token to logout
     * <li> `key` key to encrypt token when logout is success
     * </ul>
     * When the logout is success, it will send back
     * JSON object that has `success` property
     * <ul>
     * <li> `success` true if logout succeeded
     * <li> `code` failure code when logout failed
     * </ul>
     *
     * @param exchange http exchange object
     * @throws IOException from BufferedReader when input is not properly working.
     */

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Pair<JsonObject, Byte[]> root = super.handleInit(exchange);
        if (root == null) return;

        String id = root.getKey().get("id").getAsString();
        String tk = root.getKey().get("tk").getAsString();

        Pair<Boolean, Integer> t = Server.user.logout(id, tk);
        String res = "{'success':" + t.getKey();
        if (t.getKey())
            res += "}";
        else
            res += ",'code':'" + t.getValue() + "'}";

        if (Server.debugLevel >= 2) {
            Util.log("server", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) +
                    " /logout POST 200 id='" + id + "' response='" + res + "'", Commands.LOG);
        }

        super.send(exchange, res, root.getValue());
    }
}
