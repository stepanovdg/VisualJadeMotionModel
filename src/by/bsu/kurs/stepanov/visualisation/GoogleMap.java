package by.bsu.kurs.stepanov.visualisation;


import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.net.URL;


/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 21.02.13
 * Time: 18:29
 * To change this template use File | Settings | File Templates.
 */
public class GoogleMap extends Parent {

    private JSObject doc;
    private EventHandler<MapEvent> onMapLatLngChanged;
    private WebView webView;
    private WebEngine webEngine;
    private boolean ready;

    public GoogleMap() {
        initMap();
        initCommunication();
        getChildren().add(webView);
        setMarkerPosition(0, 0);
        setMapCenter(0, 0);
        switchTerrain();
    }

    private void initMap() {
        webView = new WebView();
        webEngine = webView.getEngine();
        URL mapUrl = getClass().getClassLoader().getResource("map.html");
        webEngine.load(mapUrl != null ? mapUrl.toExternalForm() : null);
        ready = false;
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(final ObservableValue<? extends Worker.State> observableValue,
                                final Worker.State oldState,
                                final Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    ready = true;
                }
            }
        });
    }

    private void initCommunication() {
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(final ObservableValue<? extends Worker.State> observableValue,
                                final Worker.State oldState,
                                final Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    doc = (JSObject) webEngine.executeScript("window");
                    doc.setMember("app", GoogleMap.this);
                }
            }
        });
    }

    private void invokeJS(final String str) {
        if (ready) {
            doc.eval(str);
        } else {
            webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                @Override
                public void changed(final ObservableValue<? extends Worker.State> observableValue,
                                    final Worker.State oldState,
                                    final Worker.State newState) {
                    if (newState == Worker.State.SUCCEEDED) {
                        doc.eval(str);
                    }
                }
            });
        }
    }

    public void setOnMapLatLngChanged(EventHandler<MapEvent> eventHandler) {
        onMapLatLngChanged = eventHandler;
    }

    public void handle(double lat, double lng) {
        if (onMapLatLngChanged != null) {
            MapEvent event = new MapEvent(this, lat, lng);
            onMapLatLngChanged.handle(event);
        }
    }

    public void setMarkerPosition(double lat, double lng) {
        String sLat = Double.toString(lat);
        String sLng = Double.toString(lng);
        invokeJS("setMarkerPosition(" + sLat + ", " + sLng + ")");
    }

    public void addMarkerPosition(double lat, double lng) {
        String sLat = Double.toString(lat);
        String sLng = Double.toString(lng);
        invokeJS("addMarkerPosition(" + sLat + ", " + sLng + ")");
    }

    public void setMapCenter(double lat, double lng) {
        String sLat = Double.toString(lat);
        String sLng = Double.toString(lng);
        invokeJS("setMapCenter(" + sLat + ", " + sLng + ")");
    }

    public void switchSatellite() {
        invokeJS("switchSatellite()");
    }

    public void switchRoadmap() {
        invokeJS("switchRoadmap()");
    }

    public void switchHybrid() {
        invokeJS("switchHybrid()");
    }

    public void switchTerrain() {
        invokeJS("switchTerrain()");
    }

    public void startJumping() {
        invokeJS("startJumping()");
    }

    public void stopJumping() {
        invokeJS("stopJumping()");
    }

    public void setHeight(double h) {
        webView.setPrefHeight(h);
    }

    public void setWidth(double w) {
        webView.setPrefWidth(w);
    }

    public ReadOnlyDoubleProperty widthProperty() {
        return webView.widthProperty();
    }

    public void addRoad(Double latitude, Double longitude, Double latitude1, Double longitude1) {
        String fromLat = Double.toString(latitude);
        String fromLng = Double.toString(longitude);
        String toLat = Double.toString(latitude1);
        String toLng = Double.toString(longitude1);
        invokeJS("addRoad(" + fromLat + ", " + fromLng + ", " + toLat + ", " + toLng + ")");
    }
}