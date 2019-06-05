package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Login implements HttpHandler {

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
        if (exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(200, Server.publicKey.length());
            OutputStream os = exchange.getResponseBody();
            os.write(Server.publicKey.getBytes());
            os.close();
            return;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));

        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(br.readLine()).getAsJsonObject();

        String res;
        String id = root.get("id").getAsString();

        if (root.has("pw")) {
            String pw = root.get("pw").getAsString();
            res = Server.user.login(id, pw);
        } else {
            String tk = root.get("tk").getAsString();
            res = Server.user.loginWithToken(id, tk);
        }

        exchange.sendResponseHeaders(200, res.length());
        OutputStream os = exchange.getResponseBody();
        os.write(res.getBytes());
        os.close();
    }
}
