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

public class Register implements HttpHandler {

    /**
     * This method is used for handling login requests.
     * Using POST method, gets login data from client
     * and makes new account. {@link User#register(String, String)}
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
     * <li> `success` true if register succeeded
     * <li> `token` token to use for every logged-in action
     * <li> `code` failure code when login failed
     * </ul>
     *
     * @param exchange http exchange object
     * @throws IOException from BufferedReader when input is not properly working.
     */

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));

        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(br.readLine()).getAsJsonObject();

        String id = root.get("id").getAsString();
        String pw = root.get("pw").getAsString();

        String res = Server.db.register(id, pw);

        exchange.sendResponseHeaders(200, res.length());
        OutputStream os = exchange.getResponseBody();
        os.write(res.getBytes());
        os.close();
    }
}
