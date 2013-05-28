package by.bsu.kurs.stepanov.visualisation;

import javafx.event.Event;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 21.02.13
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */
public class MapEvent extends Event {

    public MapEvent(GoogleMap map, double lat, double lng) {
        super(map, Event.NULL_SOURCE_TARGET, Event.ANY);
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }

    private double lat;
    private double lng;
}