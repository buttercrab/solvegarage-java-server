package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Search implements HttpHandler {

    /**
     * http request
     *
     * @param exchange http request object
     * @throws IOException thrown when something goes wrong
     */

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) return;

        Map<String, Object> parameters = new HashMap<>();
        Util.parseQuery(exchange.getRequestURI().getRawQuery(), parameters);


    }
}
