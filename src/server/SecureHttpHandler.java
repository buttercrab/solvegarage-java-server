package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javafx.util.Pair;
import util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public abstract class SecureHttpHandler implements HttpHandler {
    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;

    protected Pair<JsonObject, Byte[]> handleInit(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(200, Server.publicKey.length());
            OutputStream os = exchange.getResponseBody();
            os.write(Server.publicKey.getBytes());
            os.close();
            return null;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));

        String input = Util.RSA.decrypt(Arrays.toString(Base64.getDecoder().decode(br.readLine())), Server.keyPair.getPrivate().getEncoded());
        if (input == null) return null;

        String[] body = input.split("[:]");
        byte[] key = Base64.getDecoder().decode(body[0]);
        String text = Arrays.toString(Base64.getDecoder().decode(body[1]));
        String sign = Arrays.toString(Base64.getDecoder().decode(body[2]));

        if (!Util.RSA.verify(text, sign, key)) return null;

        Byte[] keyObject = new Byte[key.length];
        int i = 0;
        for (byte b : key)
            keyObject[i++] = b;

        JsonParser parser = new JsonParser();
        return new Pair<>(parser.parse(text).getAsJsonObject(), keyObject);
    }

    protected void send(HttpExchange exchange, String data, Byte[] key) throws IOException {
        byte[] keyPrimitive = new byte[key.length];
        int i = 0;
        for (Byte b : key)
            keyPrimitive[i++] = b;

        data += ":" + Util.RSA.sign(data, Server.keyPair.getPrivate().getEncoded());
        data = Util.RSA.encrypt(data, keyPrimitive);

        if (data == null) return;

        exchange.sendResponseHeaders(200, data.length());
        OutputStream os = exchange.getResponseBody();
        os.write(data.getBytes());
        os.close();
    }
}
