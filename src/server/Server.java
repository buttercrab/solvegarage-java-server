package server;

import com.sun.net.httpserver.HttpServer;
import database.Database;
import util.Util;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    private static int port = 3080;
    public static Database db = new Database();
    public static Thread cmd = new Commands();
    static HttpServer server;

    public static void main(String[] args) throws IOException {
        Server.db.init("buttercrab", args[0]);
        Server.cmd.start();

        Server.server = HttpServer.create(new InetSocketAddress(port), 0);

        Server.server.createContext("/login", new Login());

        Server.server.start();

        Util.log("Server started on port " + Server.port, Commands.LOG);
    }
}

