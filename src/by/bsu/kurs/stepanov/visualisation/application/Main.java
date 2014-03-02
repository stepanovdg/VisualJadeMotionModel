package by.bsu.kurs.stepanov.visualisation.application;/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 09.02.14
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL mainUrl = getClass().getClassLoader().getResource("main.fxml");
        Parent root = FXMLLoader.load(mainUrl);
        primaryStage.setTitle("Visual Java Agents");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}
