package by.bsu.kurs.stepanov.visualisation;

import by.bsu.kurs.stepanov.types.Coordinates;
import javafx.event.Event;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 21.02.13
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */
public class MapEvent extends Event {

    private String name;
    private Coordinates next;
    private int roadPercent;

    public MapEvent(GoogleMap map, double lat, double lng) {
        super(map, Event.NULL_SOURCE_TARGET, Event.ANY);
        this.lat = lat;
        this.lng = lng;
    }

    public MapEvent(String name, Coordinates next, int roadPercent) {
        super(null, Event.NULL_SOURCE_TARGET, Event.ANY);
        this.name = name;
        this.next = next;
        this.roadPercent = roadPercent;
    }


    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getNext() {
        return next;
    }

    public void setNext(Coordinates next) {
        this.next = next;
    }

    public int getRoadPercent() {
        return roadPercent;
    }

    public void setRoadPercent(int roadPercent) {
        this.roadPercent = roadPercent;
    }

    private double lat;
    private double lng;
}