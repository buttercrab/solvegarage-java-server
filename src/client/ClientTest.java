package client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import server.Commands;
import util.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;

public class ClientTest {

    public static void main(String[] args) throws Exception {
        URL url = new URL("http://localhost:3080/get-key");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        byte[] serverPublicKey = Base64.getDecoder().decode(br.readLine());

        JsonObject root;

        root = SecureHttpConnection.post("http://localhost:3080/delete-account", "{'id':'test','pw':'test'}", serverPublicKey, Objects.requireNonNull(Util.RSA.generateKeyPair()));
        assert root != null;
        Util.log("delete-account", root.toString(), Commands.LOG);
        assert root.get("success").getAsBoolean();

        root = SecureHttpConnection.post("http://localhost:3080/register", "{'id':'test','pw':'test'}", serverPublicKey, Objects.requireNonNull(Util.RSA.generateKeyPair()));
        assert root != null;
        Util.log("register", root.toString(), Commands.LOG);
        assert root.get("success").getAsBoolean();

        root = SecureHttpConnection.post("http://localhost:3080/login", "{'id':'test','pw':'test'}", serverPublicKey, Objects.requireNonNull(Util.RSA.generateKeyPair()));
        assert root != null;
        Util.log("login", root.toString(), Commands.LOG);
        String token = root.get("token").getAsString();
        assert root.get("success").getAsBoolean();

        root = SecureHttpConnection.post("http://localhost:3080/login", "{'id':'test','token':'" + token + "'}", serverPublicKey, Objects.requireNonNull(Util.RSA.generateKeyPair()));
        assert root != null;
        Util.log("login-as-token", root.toString(), Commands.LOG);
        token = root.get("token").getAsString();
        assert root.get("success").getAsBoolean();

        root = SecureHttpConnection.post("http://localhost:3080/profile-image", "{'id':'test','token':'" + token + "','img':'" + "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" + "'}", serverPublicKey, Objects.requireNonNull(Util.RSA.generateKeyPair()));
        assert root != null;
        Util.log("profile-image", root.toString(), Commands.LOG);
        token = root.get("token").getAsString();
        assert root.get("success").getAsBoolean();

        root = SecureHttpConnection.post("http://localhost:3080/logout", "{'id':'test','token':'" + token + "'}", serverPublicKey, Objects.requireNonNull(Util.RSA.generateKeyPair()));
        assert root != null;
        Util.log("logout", root.toString(), Commands.LOG);
        assert root.get("success").getAsBoolean();

        url = new URL("http://localhost:3080/profile-image?id=test");
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        root = new JsonParser().parse(new BufferedReader(new InputStreamReader(con.getInputStream())).readLine()).getAsJsonObject();
        Util.log("profile-image", root.toString(), Commands.LOG);
        assert root.get("success").getAsBoolean();
//        assert new String(Base64.getDecoder().decode(root.get("img").getAsString())).equals("Hello, World!");

        Util.log("client", "All test are successful", Commands.LOG);
    }
}
