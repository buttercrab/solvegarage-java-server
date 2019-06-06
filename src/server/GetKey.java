package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class GetKey implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(200, Server.publicKey.length());
            OutputStream os = exchange.getResponseBody();
            os.write(Server.publicKey.getBytes());
            os.close();
        }
    }
}
