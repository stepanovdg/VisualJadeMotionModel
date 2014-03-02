package by.bsu.kurs.stepanov.visualisation.control;

import by.bsu.kurs.stepanov.utils.CoordinatesUtils;
import by.bsu.kurs.stepanov.visualisation.AtomicTimelineService;
import by.bsu.kurs.stepanov.visualisation.agents.NodeAgentUi;
import by.bsu.kurs.stepanov.visualisation.agents.RoadAgentUi;
import by.bsu.kurs.stepanov.visualisation.agents.TransportAgentUi;
import by.bsu.kurs.stepanov.visualisation.application.Runner;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javafx.util.Duration;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.HashMap;

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
    @FXML // fx:id="stopAgents"
    private Button stopAgents; // Value injected by FXMLLoader
    @FXML // fx:id="nodes"
    private Group nodes; // Value injected by FXMLLoader
    @FXML // fx:id="roads"
    private Group roads; // Value injected by FXMLLoader
    @FXML // fx:id="initAgentsButton"
    private Button initAgentsButton; // Value injected by FXMLLoader
    @FXML
    private Button loadMapButton;
    @FXML
    private Button start;
    @FXML
    private WebView webView;
    @FXML
    private Group webViewGroup;
    @FXML // fx:id="canvas"
    private Canvas canvas; // Value injected by FXMLLoader

    private Runner agentRunner;
    private JSObject doc;
    private WebEngine webEngine;
    private Worker<Thread> jsWorker;
    private boolean ready;
    private boolean initialized;
    private MapAgentController mac;
    final WebView smallView = new WebView();

    private HashMap<String, NodeAgentUi> markersMap = new java.util.HashMap<String, NodeAgentUi>();
    private HashMap<String, RoadAgentUi> roadsMap = new HashMap<String, RoadAgentUi>();
    private HashMap<String, TransportAgentUi> transportsMap = new HashMap<String, TransportAgentUi>();

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

    private FadeTransition fadeOut(final Duration duration, final Node node) {
        final FadeTransition fadeOut = new FadeTransition(duration, node);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                node.setVisible(false);
            }
        });
        return fadeOut;
    }

    @FXML
    void initAgents(ActionEvent event) {
        initAgentsButton.setDisable(true);
        getMapCenter();
        getMapBorders();
        agentRunner = new Runner();
        mac = new MapAgentController("Minsk", agentRunner, webViewGroup);
        webViewGroup.requestLayout();
        webViewGroup.requestFocus();
        final FadeTransition fadeLockButton = fadeOut(Duration.valueOf("1s"), initAgentsButton);
        //start.setDisable(false);
        stopAgents.setDisable(false);
    }

    @FXML
    void stopAgents(ActionEvent event) {
        stopAgents.setDisable(true);
        agentRunner.stop();
        webViewGroup.requestLayout();
        webViewGroup.requestFocus();
        final FadeTransition fadeLockButton = fadeOut(Duration.valueOf("1s"), stopAgents);
        initAgentsButton.setDisable(false);
        start.setDisable(true);
    }

    @FXML
    void initialize() {
        assert webView != null : "fx:id=\"webView\" was not injected: check your FXML file 'main.fxml'.";
        assert loadMapButton != null : "fx:id=\"loadMapButton\" was not injected: check your FXML file 'main.fxml'.";
        assert start != null : "fx:id=\"start\" was not injected: check your FXML file 'main.fxml'.";
        assert webViewGroup != null : "fx:id=\"webViewGroup\" was not injected: check your FXML file 'main.fxml'.";
        CoordinatesUtils.setResolution(webView.getPrefWidth(), webView.getPrefHeight());
    }

    // Handler for Button[fx:id="loadMapButton"] onAction
    @FXML
    void loadMapAction(ActionEvent event) {
        loadMapButton.setDisable(true);
        initAgentsButton.setDisable(true);
        initMap();
        initCommunication();

        setMapCenter(54.92082843149136, 23.829345703125);// temporary
        //getMapCenter();
        //getMapBorders();
        webViewGroup.requestLayout();
        webViewGroup.requestFocus();
        final FadeTransition fadeLockButton = fadeOut(Duration.valueOf("1s"), loadMapButton);

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
        start.setDisable(true);
        //agentRunner.run(eventsHandler);
        setMapCenter(54.92082843149136, 23.829345703125);// temporary
        mac.startMoving(null);
        //webViewGroup.requestFocus();
        final FadeTransition fadeLockButton = fadeOut(Duration.valueOf("1s"), start);
    }

    @FXML
    void tempAddNode(ActionEvent event) {
        AtomicTimelineService atc = new AtomicTimelineService();
        atc.setTransportGroup(webViewGroup);
        atc.start();
    }


    private void initMap() {
        webEngine = webView.getEngine();
        URL mapUrl = getClass().getClassLoader().getResource("map.html");
        webEngine.load(mapUrl != null ? mapUrl.toExternalForm() : null);
        ready = false;
        /*webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(final ObservableValue<? extends Worker.State> observableValue,
                                final Worker.State oldState,
                                final Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    ready = true;
                }
            }
        }); */
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
                    initAgentsButton.setDisable(false);
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
                        System.err.println(s);
                        return true;
                    }
                }
        );

        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> stringWebEvent) {
                System.err.println(stringWebEvent.toString());
            }
        });
    }

    private void invokeJS(final String str) {
       /* Platform.runLater(new Runnable() {
            @Override
            public void run() { */
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
                        System.out.println("invokeJS " + str);
                        doc.eval(str);
                    }
                }
            });
        }
           /* }
        });     */

    }


    public void setMapCenter(double lat, double lng) {
        String sLat = Double.toString(lat);
        String sLng = Double.toString(lng);
        invokeJS("setMapCenter(" + sLat + ", " + sLng + ")");
    }

    public void callback(boolean bool) {
        System.out.println("Callback called" + "," + bool);
        initialized = bool;
        //CoordinatesUtils.setMapCenterZoom(54.92082843149136, 23.0, zoom);
    }

    public void callback(Number latC, Number lonC) {
        System.out.println("Callback called" + "," + latC + "," + lonC);
        CoordinatesUtils.setMapCenterZoom(lonC.doubleValue(), latC.doubleValue(), 7);
    }

    public void callback(double latC, double lonC, int zoom) {
        System.out.println("1Callback called" + "," + latC + "," + lonC + "," + zoom);
        CoordinatesUtils.setMapCenterZoom(lonC, latC, zoom);
    }

    public void callback(String latMax, String latMin, String lonMax, String lonMin) {
        System.out.println("2Map Borders" + "," + latMax + "," + latMin + "," + lonMax + "," + lonMin);
        CoordinatesUtils.setMapBorders(Double.valueOf(latMax), Double.valueOf(latMin), Double.valueOf(lonMax), Double.valueOf(lonMin));
    }

}
