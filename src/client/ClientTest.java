package client;

import com.google.gson.JsonObject;
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

        JsonObject root = SecureHttpConnection.post("http://buttercrab.iptime.org:3080/login", "{'id':'admin','pw':'abcd1234'}", serverPublicKey, Util.RSA.generateKeyPair());
        System.out.println(root.toString());
    }
}
