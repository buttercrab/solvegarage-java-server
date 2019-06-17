package server;

import com.sun.net.httpserver.HttpExchange;
import javafx.util.Pair;

import java.io.IOException;
import java.io.OutputStream;

public class ProblemHandler extends SecureHttpHandler {

    /**
     * This method is used for handling problem requests.
     * Using GET Method, it sends JSON object that contains
     * `success` property
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
                res += ",'title':'" + s[0] + "'";
                res += ",'author':'" + s[1] + "'";
                res += ",'body':'" + s[2] + "'";
                res += ",'date':'" + s[3] + "'}";
            } else
                res += ",'code':" + t.getValue() + "}";

            exchange.sendResponseHeaders(200, res.length());
            OutputStream os = exchange.getResponseBody();
            os.write(res.getBytes());
            os.close();
        } else {

        }
    }
}
