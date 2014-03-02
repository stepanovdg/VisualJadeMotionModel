package by.bsu.kurs.stepanov.visualisation.control;

import by.bsu.kurs.stepanov.types.Coordinates;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 06.01.14
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
public interface MapFX {
    public void setNodeMarker(Double lat, Double lng);

    /*public void setNodeStatus(String name, Coordinates coordinates, String status);

    public void setRoadStatus(String name, int mode, String status);

    public void setTransportStatus(String name, String status); */

    void addNodeMarker(String name, Coordinates coordinates, String status);

    void addRoadMarker(String name, Coordinates from, Coordinates to, int mode, String status);

    void addTransportMarker(String name, Coordinates situated, Coordinates destination, String status);

    public void moveTransportMarker(String name, Coordinates next, int roadPercent);

    public void setRoad(Coordinates from, Coordinates to);
}
