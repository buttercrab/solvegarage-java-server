package server;

import java.util.Scanner;

/**
 * Helper Class to print, input commands
 * Following list is commands api
 * <ul>
 *     <li> `stop` stops the server
 * </ul>
 */

public class Commands extends Thread {

    private boolean finished = false;

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (this.finished) {
            String next = sc.nextLine();
            switch (next) {
                case "stop":
                    this.quit();
                    break;
            }
        }
    }

    /**
     * Function to log to stdout
     *
     * @param s String to log to stdout
     */

    public void log(String s, int type) {
        String color;
        switch(type) {
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
        System.out.println("\r" + color + "[server] " + s + "\033[0m");
        System.out.print("> ");
    }

    public void quit() {
        this.finished = true;
        Server.server.stop(0);
        this.log("Shutting Down Server...", Commands.LOG);
    }

    public static final int LOG = 1;
    public static final int WARN = 2;
    public static final int ERR = 3;
}