package client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import util.Util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyPair;
import java.util.Base64;

public class SecureHttpConnection {

    /**
     * Secure http post request for server's requirement.
     *
     * @param url             url to send
     * @param data            data to send
     * @param serverPublicKey server's public RSA key to send
     * @param clientKey       client's RSA keyPair to send
     * @return JSON object gotten
     * @throws Exception when something goes wrong
     */

    public static JsonObject post(String url, String data, byte[] serverPublicKey, KeyPair clientKey) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setDoOutput(true);

        data = Base64.getEncoder().encodeToString(clientKey.getPublic().getEncoded()) + ":"
                + Base64.getEncoder().encodeToString(data.getBytes()) + ":"
                + Util.RSA.sign(data, clientKey.getPrivate().getEncoded());

        byte[] aesKey = Util.AES.generateKey();
        data = Util.AES.encrypt(data, aesKey);
        if (data == null) return null;
        data = Util.RSA.encrypt(new String(aesKey), serverPublicKey) + ":" + data;

        DataOutputStream os = new DataOutputStream(con.getOutputStream());
        os.writeBytes(data);
        os.flush();
        os.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String[] input = br.readLine().split("[:]");
        String key = Util.RSA.decrypt(input[0], clientKey.getPrivate().getEncoded());
        if (key == null) return null;
        String body = Util.AES.decrypt(input[1], key.getBytes());
        if (body == null) return null;

        String[] content = body.split("[:]");
        String json = new String(Base64.getDecoder().decode(content[0]));
        String sign = content[1];

        if (!Util.RSA.verify(json, sign, serverPublicKey)) return null;
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonObject();
    }
}
