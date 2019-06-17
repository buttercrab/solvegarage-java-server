package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import server.Commands;
import util.Util;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextArea logArea;
    @FXML
    Button startButton, stopButton;
    private String log = "";
    private boolean isRunning = false;
    private Process process;
    private Thread th;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        th = new ServerHandler();

        startButton.setOnMouseClicked(e -> {
            if (isRunning) return;
            try {
                Runtime runtime = Runtime.getRuntime();
                process = runtime.exec(new String[]{"java", "-cp", "/Library/Java/JavaVirtualMachines/jdk1.8.0_201.jdk/Contents/Home/lib/tools.jar:/Users/jaeyong/Github/solvegarage-java/out/production/solvegarage-java:/Users/jaeyong/Github/solvegarage-java/lib/gson-2.8.5.jar:/Users/jaeyong/Documents/Java/Library/mysql-connector-java-8.0.16.jar", "server.Server", Main.main_args[0], Main.main_args[1]}, null, new File("out/production/solvegarage-java"));
                isRunning = true;
                th.start();
            } catch (IOException ex) {
                Util.log("gui", "Error on starting server", Commands.ERR);
                ex.printStackTrace();
                isRunning = false;
            }
        });

        stopButton.setOnMouseClicked(e -> {
            if (!isRunning) return;
            try {
                OutputStream os = process.getOutputStream();
                os.write("stop".getBytes());
                os.flush();
                os.close();
            } catch (IOException ex) {
                Util.log("gui", "Error on stopping server, force shutting down.", Commands.ERR);
                process.destroy();
            }
        });
    }

    private void addLog(String s) {
        s = s.replaceAll("[\\[][0-9]+[m]", "");
        s = s.replaceFirst("[\\s\\S]*[\r]", "");
        s = s.replaceAll("[>]", "");
        s = s.trim();
        if (s.equals("")) return;
        log += s + '\n';
        logArea.setText(log);
    }


    class ServerHandler extends Thread {

        @Override
        public void run() {
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (isRunning) {
                try {
                    String line = br.readLine();
                    addLog(line);
                    if (!process.isAlive()) {
                        Platform.exit();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
