package by.bsu.kurs.stepanov.visualisation.control;

import by.bsu.kurs.stepanov.types.Coordinates;
import by.bsu.kurs.stepanov.visualisation.agents.NodeAgentUi;
import by.bsu.kurs.stepanov.visualisation.agents.RoadAgentUi;
import by.bsu.kurs.stepanov.visualisation.agents.TransportAgentUi;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 06.01.14
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
public interface MapFX {
    void addNodeMarker(String name, Coordinates coordinates, String status);

    void addRoadMarker(String name, Coordinates from, Coordinates to, int mode, String status);

    void addTransportMarker(String name, Coordinates situated, Coordinates destination, String status);

    public void moveTransportMarker(String name, Coordinates next, int roadPercent);

    public Map<Coordinates, NodeAgentUi> getNodes();

    public Map<String, TransportAgentUi> getTransports();

    public Map<String, RoadAgentUi> getRoads();

    public Logger getLog();

    public void finish();
}
