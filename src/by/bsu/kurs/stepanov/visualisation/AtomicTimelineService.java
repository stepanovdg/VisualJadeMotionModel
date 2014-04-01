package by.bsu.kurs.stepanov.visualisation;

import by.bsu.kurs.stepanov.types.Coordinates;
import by.bsu.kurs.stepanov.utils.CoordinatesUtils;
import by.bsu.kurs.stepanov.utils.ExceptionUtils;
import by.bsu.kurs.stepanov.visualisation.agents.NodeAgentUi;
import by.bsu.kurs.stepanov.visualisation.agents.RoadAgentUi;
import by.bsu.kurs.stepanov.visualisation.agents.TransportAgentUi;
import javafx.animation.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 22.02.14
 * Time: 12:53
 * To change this template use File | Settings | File Templates.
 */
public class AtomicTimelineService extends Service {
    private Map<Coordinates, NodeAgentUi> nodes;
    private Map<String, RoadAgentUi> roads;
    private Map<String, TransportAgentUi> transport;
    private Queue<MapEvent> events;

    public Group getTransportGroup() {
        return transportGroup;
    }

    public void setTransportGroup(Group transportGroup) {
        this.transportGroup = transportGroup;
    }

    private Random rnd = new Random(System.nanoTime());
    private Group transportGroup;

    @Override
    protected Task createTask() {
        return new Task<String>() {
            protected String call() throws IOException {
                String result = null;
                createTimeLine();
                return result;
            }

            public Timeline createTimeLine() {
                try {
                    Timeline timeline;
                    TimelineBuilder timelineBuilder = TimelineBuilder.create();
                    timeline = timelineBuilder.build();
                    timeline.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            succeeded();
                        }
                    });
                    for (int i = 1; i <= 1; i++) {
                        KeyFrame kf = getKeyFrame(i);
                        if (kf != null) {
                            timeline.getKeyFrames().add(kf);
                        }
                    }
                    timeline.playFromStart();
                    return timeline;
                } catch (Throwable t) {
                    ExceptionUtils.handleException(t);
                }
                return null;
            }
        };
    }

    private KeyFrame getKeyFrame(int i) {
        try {
            MapEvent me = events.poll();
            if (me != null) {
                if (nodes.containsKey(me.getNext())) {
                    if (transport.containsKey(me.getName())) {
                        TransportAgentUi ui = transport.get(me.getName());
                        return moveTransport(me.getNext(), me.getRoadPercent(), ui, Duration.millis(i * 1000));
                    } else {
                        throw new Throwable("trying to move not existing transport");
                    }
                } else {
                    throw new Throwable("not existing node");
                }
            } else {
                Thread.sleep(1000);
            }
        } catch (Throwable te) {
            ExceptionUtils.handleException(te);
        }
        return getKeyFrame(i);
       /* return new KeyFrame(Duration.millis(i * 1000),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        try {
                            //transportGroup.requestLayout();
                            MapEvent me = events.poll();
                            if (me != null) {
                                if (nodes.containsKey(me.getNext())) {
                                    if (transport.containsKey(me.getName())) {
                                        TransportAgentUi ui = transport.get(me.getName());
                                        moveTransport(me.getNext(), me.getRoadPercent(), ui);
                                    } else {
                                        throw new Throwable("trying to move not existing transport");
                                    }

                                } else {
                                    throw new Throwable("not existing node");
                                }
                            }
                        } catch (Throwable te) {
                            ExceptionUtils.handleException(te);
                        }
                    }
                },
                new KeyValue[0]); */
    }

    KeyFrame moveTransport(Coordinates next, int roadPercent, TransportAgentUi ui, Duration millis) {
        ImageView image = ui.getTransportImage();
        Line vector = ui.getTransportVector();
        Coordinates situated = ui.getSituated();
        Double xDest = CoordinatesUtils.getInstance().xFromWorld(next);
        xDest = (xDest - (CoordinatesUtils.getInstance().xFromWorld(situated))) * roadPercent / 100;
        xDest = CoordinatesUtils.getInstance().xFromWorld(situated) + xDest;
        Double yDest = CoordinatesUtils.getInstance().yFromWorld(next);
        yDest = (yDest - (CoordinatesUtils.getInstance().yFromWorld(situated))) * roadPercent / 100;
        yDest = CoordinatesUtils.getInstance().yFromWorld(situated) + yDest;
        if (roadPercent == 100) {
            ui.changeStatus(TransportAgentUi.Status.WAITING);
            ui.setSituated(next);
        } else {
            ui.changeStatus(TransportAgentUi.Status.MOVING);
        }
        KeyValue kv1 = new KeyValue(image.xProperty(), xDest - image.getImage().getWidth() / 2, Interpolator.EASE_BOTH);
        KeyValue kv2 = new KeyValue(image.yProperty(), yDest - image.getImage().getHeight() / 2, Interpolator.EASE_BOTH);
        KeyValue kv3 = new KeyValue(vector.startXProperty(), xDest, Interpolator.EASE_BOTH);
        KeyValue kv4 = new KeyValue(vector.startYProperty(), yDest, Interpolator.EASE_BOTH);

        return new KeyFrame(millis, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent paramT) {
                //transportGroup.requestLayout();
            }
        }, kv1, kv2, kv3, kv4);
    }

    private KeyFrame getKeyFrameRandomRect(final Canvas animation, final GraphicsContext context2, int i) {
        return new KeyFrame(Duration.millis(i * 500),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        context2.clearRect(0, 0, animation.getWidth(), animation.getHeight());
                        int rndX = rnd.nextInt(100);
                        int rndY = rnd.nextInt(100);
                        Integer paint = rnd.nextInt(200);
                        Integer paint1 = rnd.nextInt(200);
                        Integer paint2 = rnd.nextInt(200);
                        String pain = paint.toString() + paint1.toString() + paint2.toString();
                        System.out.println(pain);
                        //context2.setFill(Paint.valueOf(pain));
                        context2.fillOval(rndX, rndY, rndX, rndY); // TODO location
                    }
                },
                new KeyValue[0]);
    }

    public void setNodes(Map<Coordinates, NodeAgentUi> nodes) {
        this.nodes = nodes;
    }

    public Map<Coordinates, NodeAgentUi> getNodes() {
        return nodes;
    }

    public void setRoads(Map<String, RoadAgentUi> roads) {
        this.roads = roads;
    }

    public Map<String, RoadAgentUi> getRoads() {
        return roads;
    }

    public void setTransport(Map<String, TransportAgentUi> transport) {
        this.transport = transport;
    }

    public Map<String, TransportAgentUi> getTransport() {
        return transport;
    }

    public void setEvents(Queue<MapEvent> events) {
        this.events = events;
    }

    public Queue<MapEvent> getEvents() {
        return events;
    }
}
