package by.bsu.kurs.stepanov.visualisation.control;

import by.bsu.kurs.stepanov.types.Coordinates;
import by.bsu.kurs.stepanov.utils.ExceptionUtils;
import by.bsu.kurs.stepanov.visualisation.AtomicTimelineService;
import by.bsu.kurs.stepanov.visualisation.MapEvent;
import by.bsu.kurs.stepanov.visualisation.agents.NodeAgentUi;
import by.bsu.kurs.stepanov.visualisation.agents.RoadAgentUi;
import by.bsu.kurs.stepanov.visualisation.agents.TransportAgentUi;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 09.02.14
 * Time: 18:59
 * To change this template use File | Settings | File Templates.
 */
public class EventsHandler implements MapFX {


    private Group group;
    private Group nodeGr;
    private Group roadGr;
    private Group transportGr;
    private volatile Map<Coordinates, NodeAgentUi> nodes;
    private volatile Map<String, RoadAgentUi> roads;
    private volatile Map<String, TransportAgentUi> transports;
    private volatile Queue<MapEvent> mapEvents = new ArrayDeque<>();

    public EventsHandler(Group webViewGroup) {
        setGroup(webViewGroup);
        nodes = new HashMap<>();
        roads = new HashMap<>();
        transports = new HashMap<>();
        createTimeLine();
    }


    @Override
    public void setNodeMarker(Double lat, Double lng) {
    }

    @Override
    public void addNodeMarker(final String name, final Coordinates coordinates, final String status) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    if (nodes.containsKey(coordinates)) {
                        NodeAgentUi ui = nodes.get(coordinates);
                        ui.changeStatus(status);
                    } else {
                        final NodeAgentUi ui = new NodeAgentUi(name, coordinates);
                        ui.changeStatus(status);
                        nodes.put(coordinates, ui);
                        nodeGr.getChildren().add(ui.getImage());
                    }
                } catch (Throwable t) {
                    ExceptionUtils.handleException(t);
                }
            }
        });

    }

    @Override
    public void addRoadMarker(final String name, final Coordinates from, final Coordinates to, final int mode, final String status) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    if (from == null && to == null) {
                        RoadAgentUi ui = roads.get(name);
                        ui.changeStatus(status);
                        return;
                    }
                    if (nodes.containsKey(from) && nodes.containsKey(to)) {
                        if (roads.containsKey(name)) {
                            RoadAgentUi ui = roads.get(name);
                            ui.changeStatus(status);
                        } else {
                            RoadAgentUi ui = new RoadAgentUi(name, from, to, mode);
                            ui.changeStatus(status);
                            roads.put(name, ui);
                            roadGr.getChildren().add(ui.getRoad());
                        }
                    } else {
                        throw new Throwable("not existing node");
                    }
                } catch (Throwable t) {
                    ExceptionUtils.handleException(t);
                }
            }
        }

        );
    }

    @Override
    public void addTransportMarker(final String name, final Coordinates situated, final Coordinates destination,
                                   final String status) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    if (situated == null && destination == null) {
                        TransportAgentUi ui = transports.get(name);
                        ui.changeStatus(status);
                        return;
                    }
                    if (nodes.containsKey(situated) && nodes.containsKey(destination)) {
                        if (transports.containsKey(name)) {
                            TransportAgentUi ui = transports.get(name);
                            ui.changeStatus(status);

                        } else {
                            TransportAgentUi ui = new TransportAgentUi(name, situated, destination, nodes.get(destination).getName());
                            ui.changeStatus(status);
                            transports.put(name, ui);
                            transportGr.getChildren().add(ui.getTransportImage());
                            transportGr.getChildren().add(ui.getTransportVector());
                        }
                    } else {
                        throw new Throwable("not existing node");
                    }
                } catch (Throwable t) {
                    ExceptionUtils.handleException(t);
                }
            }
        });
    }

    @Override
    public void moveTransportMarker(final String name, final Coordinates next, final int roadPercent) {
//        System.out.println("move transport " + name + " to" + next + "percent" + roadPercent);
        mapEvents.add(new MapEvent(name, next, roadPercent));
    }

    @Override
    public void setRoad(Coordinates from, Coordinates to) {

    }

    public void setGroup(Group group) {
        this.group = group;
        this.nodeGr = (Group) group.getChildren().get(1);
        this.roadGr = (Group) group.getChildren().get(2);
        this.transportGr = (Group) group.getChildren().get(3);
    }

    public Group getGroup() {
        return group;
    }

    private Random rnd = new Random(System.nanoTime());

    public void createTimeLine() {
        AtomicTimelineService atc = new AtomicTimelineService();
        atc.setTransportGroup(getGroup());
        atc.setNodes(nodes);
        atc.setRoads(roads);
        atc.setTransport(transports);
        atc.setEvents(mapEvents);
        atc.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                createTimeLine();
            }
        });
        atc.start();
    }

   /* public void moveTransport(Coordinates next, int roadPercent, final TransportAgentUi ui) {
        ImageView image = ui.getTransportImage();
        Coordinates situated = ui.getSituated();
        final TranslateTransition trans = createTranslateTransition(ui);
        Double xDest = CoordinatesUtils.xFromWorld(next);
        System.err.println("1moving xdest" + xDest + " imxx=" + image.getX());
        xDest = (xDest - (CoordinatesUtils.xFromWorld(situated))) * roadPercent / 100;
        System.err.println("2moving xdest" + xDest + " imxx=" + image.getX());
        xDest = CoordinatesUtils.xFromWorld(situated) + xDest - image.getX();
        //System.err.println("3moving to percent=" + roadPercent + " x=" + xDest);
        trans.setToX(xDest);
        Double yDest = CoordinatesUtils.yFromWorld(next);
        yDest = (yDest - (CoordinatesUtils.yFromWorld(situated))) * roadPercent / 100;
        yDest = CoordinatesUtils.yFromWorld(situated) + yDest - image.getY();
        trans.setToY(yDest);
        if (roadPercent == 100) {
            ui.setSituated(next);
        }
        //transitionQueue.add(trans);
            /*trans.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    TranslateTransition tr = transitionQueue.poll();
                    if (tr != null) {
                        System.err.println("start next moving");
                        tr.playFromStart();
                        moving = true;
                    } else {
                        moving = false;
                    }

                }
            });
            if (!moving) {
                moving = true;
                System.err.println("start first moving");
                trans.playFromStart();
            }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                seq.getChildren().add(trans);
            }
        });

        seq.play();


    }  */

    /*private TranslateTransition createTranslateTransition(final TransportAgentUi ui) {
        final ImageView image = ui.getTransportImage();
        final TranslateTransition transition = new TranslateTransition(Duration.seconds(Constants.TRANSLATE_DURATION), image);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    ui.calculateVector();
                    System.err.println(t);
                    System.err.println(image);
                    System.err.println(image.getTranslateX() + "=translateX image.getx=" + image.getX());
                    System.out.println("never called handle");
                    /*image.setX(image.getTranslateX() + image.getX());
                    image.setY(image.getTranslateY() + image.getY());
                    image.setTranslateX(0);
                    image.setTranslateY(0);
                    image.relocate(image.getTranslateX() + image.getX(), image.getTranslateY() + image.getY());
                /*transportGr.requestLayout();
                group.requestLayout();
                } catch (Throwable te) {
                    te.printStackTrace();
                }
            }
        });

        return transition;
    } */
}
