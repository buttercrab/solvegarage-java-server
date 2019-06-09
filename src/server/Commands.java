package server;

import java.util.Scanner;

/**
 * Helper Class to print, input commands
 * Following list is commands api
 * <ul>
 * <li> `exit` exit the program
 * </ul>
 */

public class Commands extends Thread {

    private boolean finished = false;

    /**
     * New thread function to get data from stdin and do some stuffs.
     * It would loop forever unless the server is finished by other function.
     * {@link Commands#quit()}
     */

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\r> ");
        while (!this.finished) {
            String next = sc.nextLine();
            String[] cmd = next.split(" ");
            if (next.length() > 0) {
                switch (cmd[0]) {
                    case "help":
                        this.log("note", "Commands can be used", Commands.WARN);
                        this.log("note", "> stop", Commands.WARN);
                        this.log("note", " : stops the server", Commands.WARN);
                        this.log("note", "> debug {number}", Commands.WARN);
                        this.log("note", " : change the debug level. Number should be between 1 to 2", Commands.WARN);
                        this.log("note", "> help", Commands.WARN);
                        this.log("note", " : show this command help", Commands.WARN);
                        break;
                    case "stop":
                        this.quit();
                        break;
                    case "debug":
                        if (cmd.length < 2) {
                            this.log("error", "debug has one argument", Commands.ERR);
                            this.log("note", "type `help` to see commands", Commands.WARN);
                            break;
                        }
                        if (cmd[1].equals("1")) {
                            this.log("note", "debugLevel changed to 1", Commands.WARN);
                            Server.debugLevel = 1;
                        } else if (cmd[1].equals("2")) {
                            this.log("note", "debugLevel changed to 2", Commands.WARN);
                            Server.debugLevel = 2;
                        } else {
                            this.log("error", "argument has to be between 1 to 2", Commands.ERR);
                            this.log("note", "type `help` to see commands", Commands.WARN);
                        }
                        break;
                    default:
                        this.log("error", "No command named `" + cmd[0] + "`", Commands.ERR);
                        this.log("note", "type `help` to see commands", Commands.WARN);
                }
            } else {
                System.out.print("\r> ");
            }
        }
    }

    /**
     * Function to log to stdout using the format `[{from}] {s}`. `type` would differ
     * the color. It uses ANSI escape code to output the color. Following list is the
     * type to color.
     * <ul>
     * <li> Command.LOG(=1) CYAN
     * <li> Command.WARN(=2) YELLOW
     * <li> Command.ERR(=3) RED
     * </ul>
     *
     * @param from string to output the log source
     * @param s    string to output
     * @param type sets the string color
     */

    public void log(String from, String s, int type) {
        String color;
        switch (type) {
            case Commands.LOG:
                color = "\033[36m";
                break;
            case Commands.WARN:
                color = "\033[33m";
                break;
            case Commands.ERR:
                color = "\033[31m";
                break;
            default:
                color = "\033[0m";
        }
        if (from.length() > 0) from = "[" + from + "]";
        System.out.println("\r" + color + from + " " + s + "\033[0m");
        System.out.print("> ");
    }

    /**
     * Shutting down function that finishes all running things.
     */

    public void quit() {
        this.finished = true;
        Server.server.stop(0);
        Server.user.quit();
        this.log("server", "Shutting down server...", Commands.LOG);
        System.exit(0);
    }

    public static final int LOG = 1;
    public static final int WARN = 2;
    public static final int ERR = 3;
}