package server;

import com.sun.net.httpserver.HttpServer;
import database.User;
import util.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.Base64;

public class Server {

    private static int port = 3080;
    static User user = new User();
    public static Thread cmd = new Commands();
    static HttpServer server;
    static KeyPair keyPair;
    static String publicKey;

    /**
     * Main function of this server. It starts the server and connect to MySQL database.
     *
     * @param args arguments when stating the server
     * @throws IOException exception when creating the http server.
     */

    public static void main(String[] args) throws IOException {
        Server.user.init(args[0], args[1]);
        Server.cmd.start();

        Server.server = HttpServer.create(new InetSocketAddress(port), 0);

        Server.server.createContext("/login", new Login());
        Server.server.createContext("/register", new Register());
        Server.server.createContext("/logout", new Logout());
        Server.server.createContext("/delete-account", new DeleteAccount());

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

