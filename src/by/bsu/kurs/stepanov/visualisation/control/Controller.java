package by.bsu.kurs.stepanov.visualisation.control;

import by.bsu.kurs.stepanov.types.Coordinates;
import by.bsu.kurs.stepanov.utils.CoordinatesUtils;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import netscape.javascript.JSObject;

import java.io.File;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 09.02.14
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */
public class Controller {

    @FXML // fx:id="transports"
    private Group transports; // Value injected by FXMLLoader
    @FXML // fx:id="stopAgentsButton"
    private Button stopAgentsButton; // Value injected by FXMLLoader
    @FXML // fx:id="nodes"
    private Group nodes; // Value injected by FXMLLoader
    @FXML // fx:id="roads"
    private Group roads; // Value injected by FXMLLoader
    @FXML // fx:id="addRoadButton"
    private Button addRoadButton; // Value injected by FXMLLoader
    @FXML // fx:id="addNodeButton"
    private Button addNodeButton; // Value injected by FXMLLoader
    @FXML // fx:id="nameColumn"
    private TableColumn<String, String> nameColumn; // Value injected by FXMLLoader
    @FXML // fx:id="valueColumn"
    private TableColumn<String, String> valueColumn; // Value injected by FXMLLoader
    @FXML // fx:id="addTransportButton"
    private Button addTransportButton; // Value injected by FXMLLoader
    @FXML // fx:id="table"
    private TableView<String> table; // Value injected by FXMLLoader
    @FXML // fx:id="lockMapButton"
    private Button lockMapButton; // Value injected by FXMLLoader
    @FXML // fx:id="initAgentsButton"
    private Button initAgentsButton; // Value injected by FXMLLoader
    @FXML
    private Button loadMapButton;
    @FXML
    private Button startButton;
    @FXML // fx:id="exportButton"
    private Button exportButton; // Value injected by FXMLLoader
    @FXML // fx:id="importButton"
    private Button importButton; // Value injected by FXMLLoader
    @FXML
    private WebView webView;
    @FXML
    private Group webViewGroup;
    @FXML // fx:id="canvas"
    private Canvas canvas; // Value injected by FXMLLoader

    public enum State {MOVING, SHOWING, ROAD, NODE, TRANSPORT}


    private State state = State.MOVING;
    private Coordinates firstRoadEnd = null;
    private JSObject doc;
    private WebEngine webEngine;
    private Worker<Thread> jsWorker;
    private boolean ready;
    private boolean initialized;
    private boolean lockMap = false;
    private MapAgentController mac;
    private Integer counter = 0;
    private DropShadow dropShadow = new DropShadow();
    final WebView smallView = new WebView();

    private final static class HeightTransition extends Transition {

        final Rectangle node;
        final double height;

        public HeightTransition(Duration duration, Rectangle node) {
            this(duration, node, node.getHeight());
        }

        public HeightTransition(Duration duration, Rectangle node, double height) {
            this.node = node;
            this.height = height;
            this.setCycleDuration(duration);
        }

        public Duration getDuration() {
            return getCycleDuration();
        }

        @Override
        protected void interpolate(double frac) {
            this.node.setHeight((1.0 - frac) * height);
        }
    }

    private final static class WidthTransition extends Transition {

        final Rectangle node;
        final double width;

        public WidthTransition(Duration duration, Rectangle node) {
            this(duration, node, node.getWidth());
        }

        public WidthTransition(Duration duration, Rectangle node, double width) {
            this.node = node;
            this.width = width;
            this.setCycleDuration(duration);
        }

        public Duration getDuration() {
            return getCycleDuration();
        }

        @Override
        protected void interpolate(double frac) {
            this.node.setWidth((1.0 - frac) * width);
        }
    }

    private FadeTransition fadeOut(final Node node) {
        final Duration duration = Duration.millis(1000);
        final FadeTransition fadeOut = new FadeTransition(duration, node);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                node.setVisible(false);
                node.setDisable(true);
            }
        });
        fadeOut.play();
        return fadeOut;
    }

    private FadeTransition fadeIn(final Node node) {
        final Duration duration = Duration.millis(1000);
        final FadeTransition fadeOut = new FadeTransition(duration, node);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                node.setDisable(false);
                node.setVisible(true);
            }
        });
        fadeOut.play();
        return fadeOut;
    }

    @FXML
    void importAction(ActionEvent event) {
        final Stage dialog = new Stage(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.WINDOW_MODAL);
        FileChooser fileChooser = new FileChooser();
        File dest = fileChooser.showOpenDialog(dialog);
        if (dest == null) {
            return;
        }
        mac.importFromFile(dest);
    }

    @FXML
    void exportAction(ActionEvent event) {
        final Stage dialog = new Stage(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.WINDOW_MODAL);
        FileChooser fileChooser = new FileChooser();
        File dest = fileChooser.showSaveDialog(dialog);
        if (dest == null) {
            return;
        }
        mac.export(dest);
    }


    @FXML
    void lockMap(ActionEvent event) {
        if (!lockMap) {
            fadeIn(initAgentsButton);
            lockMapButton.setText("Unlock map");
            fadeIn(addNodeButton);
            fadeIn(addRoadButton);
            fadeIn(addTransportButton);
            fadeIn(table);
            fadeIn(importButton);
        } else {
            fadeOut(initAgentsButton);
            lockMapButton.setText("Lock map");
            state = State.MOVING;
            resetEffects();
            fadeOut(addNodeButton);
            fadeOut(addRoadButton);
            fadeOut(addTransportButton);
            fadeOut(table);
            fadeOut(importButton);
        }
        setMapDraggable(lockMap);
        //webView.setDisable(!lockMap);
        lockMap = !lockMap;

    }

    @FXML
    void addNode(ActionEvent event) {
        mac.setState(State.NODE);
        state = State.NODE;
        resetEffects();
        addNodeButton.setEffect(dropShadow);
    }

    private void resetEffects() {
        addNodeButton.setEffect(null);
        addRoadButton.setEffect(null);
        addTransportButton.setEffect(null);
    }

    @FXML
    void addRoad(ActionEvent event) {
        state = State.ROAD;
        mac.setState(State.ROAD);
        resetEffects();
        addRoadButton.setEffect(dropShadow);
    }

    @FXML
    void addTransport(ActionEvent event) {
        state = State.TRANSPORT;
        mac.setState(State.TRANSPORT);
        resetEffects();
        addTransportButton.setEffect(dropShadow);
    }


    @FXML
    void initAgents(ActionEvent event) {
        fadeOut(addNodeButton);
        fadeOut(addRoadButton);
        fadeOut(addTransportButton);
        fadeOut(lockMapButton);
        fadeOut(initAgentsButton);
        fadeOut(importButton);
//        getMapCenter();
//        getMapBorders();
        state = State.SHOWING;
        mac.setState(State.SHOWING);
        mac.init();
        webViewGroup.requestLayout();
        webViewGroup.requestFocus();
        fadeIn(stopAgentsButton);
    }

    @FXML
    void stopAgents(ActionEvent event) {
        fadeIn(lockMapButton);
        fadeIn(initAgentsButton);
        fadeIn(addNodeButton);
        fadeIn(addRoadButton);
        fadeIn(addTransportButton);
        fadeIn(table);
        fadeOut(stopAgentsButton);
        //mac.stop(); todo change to real mac stop now it is still close all
        webViewGroup.requestLayout();
        webViewGroup.requestFocus();
        fadeIn(importButton);
        fadeIn(exportButton);
        fadeIn(initAgentsButton);
        fadeOut(startButton);

    }

    @FXML
    void initialize() {
        assert loadMapButton != null : "fx:id=\"loadMapButton\" was not injected: check your FXML file 'main.fxml'.";
        assert startButton != null : "fx:id=\"startButton\" was not injected: check your FXML file 'main.fxml'.";
        assert roads != null : "fx:id=\"roads\" was not injected: check your FXML file 'main.fxml'.";
        assert addRoadButton != null : "fx:id=\"addRoadButton\" was not injected: check your FXML file 'main.fxml'.";
        assert addNodeButton != null : "fx:id=\"addNodeButton\" was not injected: check your FXML file 'main.fxml'.";
        assert webViewGroup != null : "fx:id=\"webViewGroup\" was not injected: check your FXML file 'main.fxml'.";
        assert transports != null : "fx:id=\"transports\" was not injected: check your FXML file 'main.fxml'.";
        assert stopAgentsButton != null : "fx:id=\"stopAgentsButton\" was not injected: check your FXML file 'main.fxml'.";
        assert nodes != null : "fx:id=\"nodes\" was not injected: check your FXML file 'main.fxml'.";
        assert nameColumn != null : "fx:id=\"nameColumn\" was not injected: check your FXML file 'main.fxml'.";
        assert valueColumn != null : "fx:id=\"valueColumn\" was not injected: check your FXML file 'main.fxml'.";
        assert addTransportButton != null : "fx:id=\"addTransportButton\" was not injected: check your FXML file 'main.fxml'.";
        assert lockMapButton != null : "fx:id=\"lockMapButton\" was not injected: check your FXML file 'main.fxml'.";
        assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'main.fxml'.";
        assert initAgentsButton != null : "fx:id=\"initAgentsButton\" was not injected: check your FXML file 'main.fxml'.";
        assert importButton != null : "fx:id=\"importButton\" was not injected: check your FXML file 'main.fxml'.";
        assert exportButton != null : "fx:id=\"exportButton\" was not injected: check your FXML file 'main.fxml'.";
        CoordinatesUtils.getInstance().setResolution(webView.getPrefWidth(), webView.getPrefHeight());
        fadeOut(addNodeButton);
        fadeOut(addRoadButton);
        fadeOut(addTransportButton);
        fadeOut(initAgentsButton);
        fadeOut(startButton);
        fadeOut(stopAgentsButton);
        fadeOut(lockMapButton);
        fadeOut(table);
        fadeOut(importButton);
        fadeOut(exportButton);
        mac = new MapAgentController("Minsk", webViewGroup, this);
        dropShadow.setColor(Color.GREEN);
    }

    // Handler for Button[fx:id="loadMapButton"] onAction
    @FXML
    void loadMapAction(ActionEvent event) {
        fadeOut(loadMapButton);
        fadeOut(initAgentsButton);
        initMap();
        initCommunication();

        setMapCenter(54.92082843149136, 23.829345703125);// temporary
        //getMapCenter();
        //getMapBorders();
        webViewGroup.requestLayout();
        webViewGroup.requestFocus();
    }

    private void getMapBorders() {
        invokeJS("getMapBorders()");
    }

    private void getMapCenter() {
        invokeJS("getMapCenter()");
    }

    // Handler for Button[fx:id="startButton"] onAction
    @FXML
    void start(ActionEvent event) {
        startButton.setDisable(true);
        //agentRunner.run(eventsHandler);
        setMapCenter(54.92082843149136, 23.829345703125);// temporary
        mac.startMoving(null);
    }

    private void initMap() {
        webEngine = webView.getEngine();
        URL mapUrl = getClass().getClassLoader().getResource("map.html");
        webEngine.load(mapUrl != null ? mapUrl.toExternalForm() : null);
        ready = false;
    }

    private void initCommunication() {
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(final ObservableValue<? extends Worker.State> observableValue,
                                final Worker.State oldState,
                                final Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    doc = (JSObject) webEngine.executeScript("window");
                    doc.setMember("app", Controller.this);
                    ready = true;
                    fadeIn(lockMapButton);
                    //doc.setMember("app", GoogleMap.class);
                }
            }
        });
        //handle popup windows
        webEngine.setCreatePopupHandler(
                new Callback<PopupFeatures, WebEngine>() {
                    @Override
                    public WebEngine call(PopupFeatures config) {
                        smallView.setFontScale(0.8);
                        webViewGroup.getChildren().add(smallView);
                        return smallView.getEngine();
                    }
                }
        );
        //handle popup windows
        webEngine.setConfirmHandler(
                new Callback<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
//                        System.err.println(s);
                        return true;
                    }
                }
        );

        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> stringWebEvent) {
//                System.err.println(stringWebEvent.toString());
            }
        });
    }

    private void invokeJS(final String str) {
        if (ready) {
            System.out.println("invokeJS " + str);
            doc.eval(str);
        } else {
            webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                @Override
                public void changed(final ObservableValue<? extends Worker.State> observableValue,
                                    final Worker.State oldState,
                                    final Worker.State newState) {
                    if (newState == Worker.State.SUCCEEDED) {   //change to ready from worker
                        doc.eval(str);
                    }
                }
            });
        }
    }


    public void setMapCenter(double lat, double lng) {
        String sLat = Double.toString(lat);
        String sLng = Double.toString(lng);
        invokeJS("setMapCenter(" + sLat + ", " + sLng + ")");
    }

    public void setMapDraggable(Boolean bool) {
        invokeJS("setMapDraggable(" + bool + ")");
    }


    public void callback(boolean bool) {
        initialized = bool;
    }

    public void callback(Number latC, Number lonC) {
        CoordinatesUtils.getInstance().setMapCenterZoom(lonC.doubleValue(), latC.doubleValue(), 7);
    }

    public void callback(double latC, double lonC, int zoom) {
        CoordinatesUtils.getInstance().setMapCenterZoom(lonC, latC, zoom);
    }

    //public void callback(String latC, String lonC, String zoom,String latMax, String latMin, String lonMax, String lonMin, String latClick, String lonClick) {
    public void callback(double latC, double lonC, int zoom, String latMax, String latMin, String lonMax, String lonMin, String latClick, String lonClick) {
        switch (state) {
            case NODE: {
                String name = "N" + counter++;
                Coordinates coord = new Coordinates(latClick, lonClick);
                mac.addNode(name, coord);
                break;
            }
            case ROAD: {
                break;
            }
            case TRANSPORT: {
                break;
            }
            case SHOWING: {
                break;
            }
            case MOVING:
            default: {
                CoordinatesUtils.getInstance().setMapCenterZoom(lonC, latC, zoom);
                CoordinatesUtils.getInstance().setMapBorders(Double.valueOf(latMax), Double.valueOf(latMin), Double.valueOf(lonMax), Double.valueOf(lonMin));
                break;
            }
        }

    }

}
