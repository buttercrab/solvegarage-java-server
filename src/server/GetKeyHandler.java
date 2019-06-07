package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetKeyHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            if (Server.debugLevel >= 2) {
                Util.log("server", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) +
                        " /get-key GET 200", Commands.LOG);
            }

            exchange.sendResponseHeaders(200, Server.publicKey.length());
            OutputStream os = exchange.getResponseBody();
            os.write(Server.publicKey.getBytes());
            os.close();
        }
    }
}
