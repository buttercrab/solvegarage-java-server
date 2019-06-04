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

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.print("> ");
        while (!this.finished) {
            String next = sc.nextLine();
            String[] cmd = next.split(" ");
            if (next.length() > 0) {
                switch (cmd[0]) {
                    case "help":
                        this.log("note", "    exit", Commands.WARN);
                        break;
                    case "exit":
                        this.quit();
                        break;
                    default:
                        this.log("error", "No command named `" + cmd[0] + "`", Commands.ERR);
                        this.log("note", "type `help` to see commands", Commands.WARN);
                }
            } else {
                System.out.print("> ");
            }
        }
    }

    /**
     * Functoin to log to stdout using the format `[{from}] {s}`. `type` would differ
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
        Server.db.quit();
        this.log("server", "Shutting down server...", Commands.LOG);
        System.exit(0);
    }

    public static final int LOG = 1;
    public static final int WARN = 2;
    public static final int ERR = 3;
}