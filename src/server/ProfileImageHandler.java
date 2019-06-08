package server;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import javafx.util.Pair;
import util.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileImageHandler extends SecureHttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            Map<String, Object> params = new HashMap<>();
            Util.parseQuery(exchange.getRequestURI().getRawQuery(), params);
            if (!(params.get("id") instanceof String)) return;

            Pair<Boolean, Object> t = Server.user.getImage((String) params.get("id"));
            String res = "{'success':" + t.getKey();
            if (t.getKey())
                res += ",'img':'" + t.getValue() + "'}";
            else
                res += ",'code':" + t.getValue() + "}";

            if (Server.debugLevel >= 2) {
                Util.log("server", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) +
                        " /profile-image GET 200 id='" + params.get("id") + "' response='" + res + "'", Commands.LOG);
            }

            exchange.sendResponseHeaders(200, res.length());
            OutputStream os = exchange.getResponseBody();
            os.write(res.getBytes());
            os.close();
        } else {
            Pair<JsonObject, Byte[]> root = super.handleInit(exchange);

            String id = root.getKey().get("id").getAsString();
            String tk = root.getKey().get("token").getAsString();
            String img = root.getKey().get("img").getAsString();

            Pair<Boolean, Object> t = Server.user.setImage(id, tk, img);
            String res = "{'success':" + t.getKey();
            if (t.getKey())
                res += ",'token':'" + t.getValue() + "'}";
            else
                res += ",'code':'" + t.getValue() + "'}";

            if (Server.debugLevel >= 2) {
                Util.log("server", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) +
                        " /profile-image POST 200 id='" + id + "' response='" + res + "'", Commands.LOG);
            }

            super.send(exchange, res, root.getValue());
        }
    }
}
