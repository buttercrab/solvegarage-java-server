package server;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import database.User;
import javafx.util.Pair;

import java.io.IOException;

public class Login extends SecureHttpHandler {

    /**
     * This method is used for handling login requests.
     * Using POST method, gets login data from client
     * and checks the user database login function.
     * {@link User#login(String, String)}
     * <p>
     * input is JSON object from client that has `id`,
     * `pw`, and `key` property.
     * <ul>
     * <li> `id` id to login
     * <li> `pw` encrypted password to login
     * <li> `key` key to encrypt token when login is success
     * </ul>
     * When the login is success, it will send back
     * JSON object that has `success`, `token` property
     * <ul>
     * <li> `success` true if login succeeded
     * <li> `token` token to use for every logged-in action
     * <li> `code` failure code when login failed
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

        Pair<Boolean, Object> t;
        if (root.getKey().has("pw")) {
            String pw = root.getKey().get("pw").getAsString();
            t = Server.user.login(id, pw);
        } else {
            String tk = root.getKey().get("tk").getAsString();
            t = Server.user.loginWithToken(id, tk);
        }
        String res = "{'success':" + t.getKey() + "";
        if (t.getKey())
            res += ",'token':'" + t.getValue() + "'}";
        else
            res += ",'code':'" + t.getValue() + "'}";

        super.send(exchange, res, root.getValue());
    }
}
