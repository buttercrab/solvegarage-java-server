package server;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import javafx.util.Pair;
import util.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class ProblemHandler extends SecureHttpHandler {

    /**
     * This method is used for handling problem requests.
     * Using GET Method, it sends JSON object that contains
     * `success` property
     * <p>
     * Using POST Method, it gets the new problem and saves
     * to the database and sends back response JSON object
     * that contains `success` property.
     *
     * @param exchange http exchange object
     * @throws IOException when something goes wrong
     */

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            String subPath = exchange.getRequestURI().getPath().replaceFirst("[/][\\s\\S]+[/]", "");
            int problemID = Integer.valueOf(subPath);
            Pair<Boolean, Object> t = Server.post.getProblem(problemID);
            String res = "{'success':" + t.getKey();
            if (t.getKey()) {
                String[] s = (String[]) t.getValue();
                res += ",'title':'" + Base64.getEncoder().encodeToString(s[0].getBytes()) + "'";
                res += ",'author':'" + s[1] + "'";
                res += ",'body':'" + Base64.getEncoder().encodeToString(s[2].getBytes()) + "'";
                res += ",'date':'" + s[3] + "'}";
            } else
                res += ",'code':" + t.getValue() + "}";

            if (Server.debugLevel >= 2) {
                Util.log("server", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) +
                        " /problem GET 200 id='" + problemID + "' response='" + res + "'", Commands.LOG);
            }

            exchange.sendResponseHeaders(200, res.length());
            OutputStream os = exchange.getResponseBody();
            os.write(res.getBytes());
            os.close();
        } else {
            Pair<JsonObject, Byte[]> root = super.handleInit(exchange);
            if (root == null) return;

            String id = root.getKey().get("id").getAsString();
            String tk = root.getKey().get("token").getAsString();
            String title = new String(Base64.getDecoder().decode(root.getKey().get("title").getAsString()));
            String body = new String(Base64.getDecoder().decode(root.getKey().get("body").getAsString()));

            Pair<Boolean, Object> t = Server.post.setProblem(id, tk, title, body);
            String res = "{'success':" + t.getKey();
            if (t.getKey())
                res += ",'token':'" + t.getValue() + "'}";
            else
                res += ",'code':" + t.getValue() + "}";

            if (Server.debugLevel >= 2) {
                Util.log("server", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) +
                        " /problem POST 200 id='" + id + "' response='" + res + "'", Commands.LOG);
            }

            super.send(exchange, res, root.getValue());
        }
    }
}
