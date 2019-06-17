package client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import server.Commands;
import util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;

/**
 * Client test for server
 * <p>
 * When this test fail, the server is not perfect. Please do this test before publishing the server.
 * <p>
 * Tests to do.
 * <ol>
 * <li> /delete-account for test account
 * <li> /register for test account
 * <li> /login to test account
 * <li> /login with token with token gotten
 * <li> /profile-image reads the image and sends to server
 * <li> /profile-image gets the image set
 * <li> /logout the account
 * </ol>
 */

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

        File file = new File("./data/profile-img/test.png");
        FileInputStream in = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        //noinspection ResultOfMethodCallIgnored
        in.read(data);
        String img = Base64.getEncoder().encodeToString(data);
        root = SecureHttpConnection.post("http://localhost:3080/profile-image", "{'id':'test','token':'" + token + "','img':'" + img + "'}", serverPublicKey, Objects.requireNonNull(Util.RSA.generateKeyPair()));
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
        assert root.get("img").getAsString().equals(img);

        Util.log("client", "All test are successful", Commands.LOG);
    }
}
