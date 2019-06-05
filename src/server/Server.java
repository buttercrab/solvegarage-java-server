package server;

import com.sun.net.httpserver.HttpServer;
import database.Database;
import util.Util;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    private static int port = 3080;
    static Database db = new Database();
    public static Thread cmd = new Commands();
    static HttpServer server;

    /**
     * Main function of this server. It starts the server and connect to MySQL database.
     *
     * @param args arguments when stating the server
     * @throws IOException exception when creating the http server.
     */

    public static void main(String[] args) throws IOException {
        Server.db.init(args[0], args[1]);
        Server.cmd.start();

        Server.server = HttpServer.create(new InetSocketAddress(port), 0);

        Server.server.createContext("/login", new Login());
        Server.server.createContext("/register", new Register());
        Server.server.createContext("/logout", new Logout());

        Server.server.start();

        Util.log("server", "Server started on port " + Server.port, Commands.LOG);
    }
}

