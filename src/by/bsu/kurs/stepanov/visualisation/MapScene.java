package by.bsu.kurs.stepanov.visualisation;

import by.bsu.kurs.stepanov.types.Coordinates;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 06.01.14
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public class MapScene extends Application implements MapFX {

    private GoogleMap map;
    private StackPane root;
    private Runner agentRunner;

    @Override
    public void start(Stage stage) throws Exception {
        root = new StackPane();
        map = new GoogleMap();
        root.getChildren().add(map);
        map.setMapCenter(54.92082843149136, 23.829345703125);
        stage.setScene(new javafx.scene.Scene(root));
        stage.show();
        agentRunner = new Runner();
        agentRunner.run(this);
    }

    public void setOnMapLatLngChanged(EventHandler<MapEvent> eventHandler) {
        map.setOnMapLatLngChanged(eventHandler);
    }

    @Override
    public void setNodeMarker(Double lat, Double lng) {
        map.addMarkerPosition(lat, lng);
        root.requestLayout();
    }

    @Override
    public void setRoad(Coordinates from, Coordinates to) {
        map.addRoad(from.getLatitude(),from.getLongitude(),to.getLatitude(),to.getLongitude());
        root.requestLayout();
    }
}
