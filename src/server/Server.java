package server;

import com.sun.net.httpserver.HttpServer;
import database.Database;
import database.Post;
import database.User;
import util.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.Base64;

public class Server {

    private static int port = 3080;
    static User user = new User();
    static Post post = new Post();
    public static Thread cmd = new Commands();
    static HttpServer server;
    static KeyPair keyPair;
    static String publicKey;

    /**
     * 1: release level (no log printed)
     * 2: debug level (some log printed)
     */

    static int debugLevel = 2;

    /**
     * Main function of this server. It starts the server and connect to MySQL database.
     *
     * @param args arguments when stating the server
     * @throws IOException exception when creating the http server.
     */

    public static void main(String[] args) throws IOException {
        Database.init(args[0], args[1]);
        Server.cmd.start();

        Server.server = HttpServer.create(new InetSocketAddress(port), 0);

        Server.server.createContext("/get-key", new GetKeyHandler());
        Server.server.createContext("/login", new LoginHandler());
        Server.server.createContext("/register", new RegisterHandler());
        Server.server.createContext("/logout", new LogoutHandler());
        Server.server.createContext("/delete-account", new DeleteAccountHandler());
        Server.server.createContext("/profile-image", new ProfileImageHandler());
        Server.server.createContext("/problem", new ProblemHandler());

        Server.server.start();

        keyPair = Util.RSA.generateKeyPair();
        if (keyPair != null) {
            publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        } else {
            Util.log("server", "Error creating RSA-2048 public key", Commands.ERR);
            Util.log("server", "Please restart the server", Commands.ERR);
        }

        Util.log("server", "Server started on port " + Server.port, Commands.LOG);
    }
}

