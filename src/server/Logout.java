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
