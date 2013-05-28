package by.bsu.kurs.stepanov.visualisation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 22.02.13
 * Time: 17:44
 * To change this template use File | Settings | File Templates.
 */
public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        GoogleMap gm = new GoogleMap();
        stage.setScene(new Scene(gm));
        stage.show();
    }
}
