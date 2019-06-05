package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Logout implements HttpHandler {

    /**
     * This method is used for handling logout requests.
     * Using POST method, gets login data from client
     * and checks the user database logout function.
     * {@link database.Database#logout(String)}
     * <p>
     * input is JSON object from client that has `id`,
     * and `key` property.
     * <ul>
     * <li> `id` id to logout
     * <li> `key` key to encrypt token when logout is success
     * </ul>
     * When the logout is success, it will send back
     * JSON object that has `success`, `token` property
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
        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));

        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(br.readLine()).getAsJsonObject();

        String id = root.get("id").getAsString();

        String res = Server.db.logout(id);

        exchange.sendResponseHeaders(200, res.length());
        OutputStream os = exchange.getResponseBody();
        os.write(res.getBytes());
        os.close();
    }
}
