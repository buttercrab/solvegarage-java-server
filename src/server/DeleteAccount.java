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

public class DeleteAccount implements HttpHandler {

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
     * <li> `key` key to encrypt token when delete-account is success
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
        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));

        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(br.readLine()).getAsJsonObject();

        String id = root.get("id").getAsString();
        String pw = root.get("pw").getAsString();

        String res = Server.user.deleteAccount(id, pw);

        exchange.sendResponseHeaders(200, res.length());
        OutputStream os = exchange.getResponseBody();
        os.write(res.getBytes());
        os.close();
    }
}
