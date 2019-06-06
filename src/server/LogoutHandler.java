package server;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import database.User;
import javafx.util.Pair;

import java.io.IOException;

public class LogoutHandler extends SecureHttpHandler {

    /**
     * This method is used for handling logout requests.
     * Using POST method, gets logout data from client
     * and checks the user database logout function.
     * {@link User#logout(String)}
     * <p>
     * input is JSON object from client that has `id`,
     * and `key` property.
     * <ul>
     * <li> `id` id to logout
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

        Pair<Boolean, Integer> t = Server.user.logout(id);
        String res = "{'success':" + t.getKey() + "";
        if (t.getKey())
            res += "}";
        else
            res += ",'code':'" + t.getValue() + "'}";

        super.send(exchange, res, root.getValue());
    }
}
