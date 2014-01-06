package by.bsu.kurs.stepanov.visualisation;

import by.bsu.kurs.stepanov.types.Coordinates;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 06.01.14
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
public interface MapFX {
    public void setNodeMarker(Double lat,Double lng);
    public void setRoad(Coordinates from,Coordinates to);
}
