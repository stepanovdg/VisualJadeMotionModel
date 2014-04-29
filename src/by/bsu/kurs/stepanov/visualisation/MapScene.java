package by.bsu.kurs.stepanov.visualisation;

import by.bsu.kurs.stepanov.types.Coordinates;
import by.bsu.kurs.stepanov.visualisation.agents.NodeAgentUi;
import by.bsu.kurs.stepanov.visualisation.agents.RoadAgentUi;
import by.bsu.kurs.stepanov.visualisation.agents.TransportAgentUi;
import by.bsu.kurs.stepanov.visualisation.application.Runner;
import by.bsu.kurs.stepanov.visualisation.control.Logger;
import by.bsu.kurs.stepanov.visualisation.control.MapFX;
import javafx.animation.Timeline;
import javafx.animation.TimelineBuilder;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

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
    private HashMap<String, NodeAgentUi> markers = new HashMap<String, NodeAgentUi>();
    private HashMap<String, RoadAgentUi> roads = new HashMap<String, RoadAgentUi>();
    private HashMap<String, TransportAgentUi> transports = new HashMap<String, TransportAgentUi>();

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

    public Timeline createTimeLine() {
        Timeline timeline;
        TimelineBuilder timelineBuilder = TimelineBuilder.create();
        timeline = timelineBuilder.build();
        return timeline;
    }

    public void setOnMapLatLngChanged(EventHandler<MapEvent> eventHandler) {
        map.setOnMapLatLngChanged(eventHandler);
    }

    @Override
    public void setNodeMarker(Double lat, Double lng) {
        map.addMarkerPosition(lat, lng);
        root.requestLayout();
        //javafx.lang.FX.deferAction();
    }

    @Override
    public void addNodeMarker(String name, Coordinates coordinates, String status) {

    }

    @Override
    public void addRoadMarker(String name, Coordinates from, Coordinates to, int mode, String status) {

    }

    @Override
    public void addTransportMarker(String name, Coordinates situated, Coordinates destination, String status) {

    }

    public void addNodeMarker(String name, Double lat, Double lng, Boolean active) {
//        NodeAgentUi marker = new NodeAgentUi(name, lat, lng);
//        markers.put(name, marker);
    }


    public void addRoadMarker(String name, Coordinates from, Coordinates to, int mode, Boolean active) {

    }

    public void addRoadMarker(String name, Coordinates from, Coordinates to, Boolean active) {
        //RoadAgentUi marker = new RoadAgentUi(from, to);
        //roads.put(name, marker);
    }


    public void addTransportMarker(String name, Coordinates situated, Coordinates destination, Boolean active) {

    }

    @Override
    public void moveTransportMarker(String name, Coordinates next, int roadPercent) {

    }

    @Override
    public Map<Coordinates, NodeAgentUi> getNodes() {
        return null;
    }

    @Override
    public Map<String, TransportAgentUi> getTransports() {
        return null;
    }

    @Override
    public Map<String, RoadAgentUi> getRoads() {
        return null;
    }

    @Override
    public void setRoad(Coordinates from, Coordinates to) {
        map.addRoad(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude());
        root.requestLayout();
        //javafx.lang.FX.deferAction();
    }

    @Override
    public Logger getLog() {
        return null;
    }

    @Override
    public void finish() {

    }
}
