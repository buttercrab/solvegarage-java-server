package client;

import com.google.gson.JsonObject;
import server.Commands;
import util.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class ClientTest {

    public static void main(String[] args) throws Exception {
        URL url = new URL("http://buttercrab.iptime.org:3080/get-key");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        byte[] serverPublicKey = Base64.getDecoder().decode(br.readLine());

        JsonObject root;

        root = SecureHttpConnection.post("http://buttercrab.iptime.org:3080/delete-account", "{'id':'test','pw':'test'}", serverPublicKey, Util.RSA.generateKeyPair());
        Util.log("delete-account", root.toString(), Commands.LOG);

        root = SecureHttpConnection.post("http://buttercrab.iptime.org:3080/register", "{'id':'test','pw':'test'}", serverPublicKey, Util.RSA.generateKeyPair());
        Util.log("register", root.toString(), Commands.LOG);

        root = SecureHttpConnection.post("http://buttercrab.iptime.org:3080/login", "{'id':'test','pw':'test'}", serverPublicKey, Util.RSA.generateKeyPair());
        Util.log("login", root.toString(), Commands.LOG);
        String token = root.get("token").getAsString();

        root = SecureHttpConnection.post("http://buttercrab.iptime.org:3080/login", "{'id':'test','tk':'" + token + "'}", serverPublicKey, Util.RSA.generateKeyPair());
        Util.log("login-as-token", root.toString(), Commands.LOG);

        root = SecureHttpConnection.post("http://buttercrab.iptime.org:3080/logout", "{'id':'test','pw':'test'}", serverPublicKey, Util.RSA.generateKeyPair());
        Util.log("logout", root.toString(), Commands.LOG);
    }
}
