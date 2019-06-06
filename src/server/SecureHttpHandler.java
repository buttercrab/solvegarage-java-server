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

/**
 * This abstract class is to make secure connection.
 */

public abstract class SecureHttpHandler implements HttpHandler {
    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;

    /**
     * Gets the input from http connection and encrypts the data.
     * <p>
     * <h1>Instructions to make txt to send</h1>
     * <ol>
     * <li> Stringify the JSON file to send
     * <li> Encode the json string by base64
     * <li> Make RSA key pair
     * <li> Make string with format {public key}:{encoded text}:{signature}
     * <li> Encrypt the string above with public key gotten from server
     * <li> Encode the string bt base64
     * </ol>
     *
     * @param exchange exchange object for connection
     * @return Pair of json and public key gotten
     * @throws IOException when something goes wrong
     */

    Pair<JsonObject, Byte[]> handleInit(HttpExchange exchange) throws IOException {
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

    /**
     * Sends the data with encryption from public key
     * <p>
     * It signs the data with server private key and encrypts the whole data.
     * Then encodes the string with base64.
     * <p>
     * <h1>Instructions to get txt from server</h1>
     * <ol>
     *     <li> Decode the text by base64
     *     <li> Decrypt the decoded text with client's private key
     *     <li> Then the text would be the format {text}:{signature}
     *     <li> Then verify the signature with server's public key just in case
     * </ol>
     *
     * @param exchange http exchange object
     * @param data     data to send
     * @param key      key to encrypt
     * @throws IOException when something goes wrong
     */

    void send(HttpExchange exchange, String data, Byte[] key) throws IOException {
        byte[] keyPrimitive = new byte[key.length];
        int i = 0;
        for (Byte b : key)
            keyPrimitive[i++] = b;

        data += ":" + Util.RSA.sign(data, Server.keyPair.getPrivate().getEncoded());
        data = Util.RSA.encrypt(data, keyPrimitive);
        if (data == null) return;
        data = Base64.getEncoder().encodeToString(data.getBytes());

        exchange.sendResponseHeaders(200, data.length());
        OutputStream os = exchange.getResponseBody();
        os.write(data.getBytes());
        os.close();
    }
}
