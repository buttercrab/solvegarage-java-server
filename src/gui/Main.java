package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for JavaFX application
 */

public class Main extends Application {

    static String[] main_args;

    public static void main(String[] args) {
        main_args = args;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));
        primaryStage.setTitle("Solvegarage Server");
        primaryStage.setScene(new Scene(root, 700, 700));
        primaryStage.show();
    }
}
