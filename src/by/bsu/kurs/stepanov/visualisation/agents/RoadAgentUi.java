package by.bsu.kurs.stepanov.visualisation.agents;

import by.bsu.kurs.stepanov.types.Coordinates;
import by.bsu.kurs.stepanov.utils.CoordinatesUtils;
import jade.core.AID;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.transform.Rotate;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 06.02.14
 * Time: 21:31
 * To change this template use File | Settings | File Templates.
 */
public class RoadAgentUi {
    private final Tooltip tooltip;
    private Status status;

    public enum Status {UI, READY, INUSE, DESTINATION, DISTANCE}

    private Coordinates from;
    private Coordinates to;
    private int roadMotionMode = 0;
    private String name;
    private AID aid;
    private Path road;
    private static Paint distancePaint = Color.GREY;
    private static Paint destiantionPaint = Color.YELLOW;
    private static Paint inUsePaint = Color.GREEN;
    private static Paint readyPaint = Color.BLUE;
    private static Paint uiPaint = Color.RED;

    public int getRoadMotionMode() {
        return roadMotionMode;
    }

    public void setRoadMotionMode(int roadMotionMode) {
        this.roadMotionMode = roadMotionMode;
    }

    public Coordinates getFrom() {
        return from;
    }

    public void setFrom(Coordinates from) {
        this.from = from;
    }

    public Coordinates getTo() {
        return to;
    }

    public void setTo(Coordinates to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public AID getAid() {
        return aid;
    }

    public void setAid(AID aid) {
        this.aid = aid;
    }

    public Path getRoad() {
        return road;
    }


    public RoadAgentUi(String name, Coordinates from, Coordinates to, Integer roadMotionMode) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.roadMotionMode = roadMotionMode;
        road = new Path();

        createRoad(CoordinatesUtils.getInstance().xFromWorld(from.getLongitude()), CoordinatesUtils.getInstance().yFromWorld(from.getLatitude()),
                CoordinatesUtils.getInstance().xFromWorld(to.getLongitude()), CoordinatesUtils.getInstance().yFromWorld(to.getLatitude()));
        tooltip = new Tooltip(name);
        Tooltip.install(road, tooltip);
        changeStatus(Status.UI);
    }

    public RoadAgentUi(String name, Double x, Double y, Double toX, Double toY) {
        createRoad(x, y, toX, toY);
        tooltip = new Tooltip(name);
        Tooltip.install(road, tooltip);
        changeStatus(Status.UI);
    }

    private void createRoad(Double x, Double y, final Double toX, final Double toY) {
        double yy = (toY - y);
        double theta = Math.atan((toX - x) / (toY - y)) * 180 / Math.PI;
        if (yy > 0) {
            theta = theta + 180;
        }
        double length = Math.sqrt((toY - y) * (toY - y) + (toX - x) * (toX - x));
        final Path path = new Path();
        path.getTransforms().add(new Rotate(-theta, toX, toY));
        path.getElements().add(new MoveTo(toX, toY));
        if (roadMotionMode >= 0) {
            path.getElements().add(new LineTo(toX - 2, toY + 10));
            path.getElements().add(new LineTo(toX + 2, toY + 10));
            path.getElements().add(new LineTo(toX, toY));
        }
        path.getElements().add(new LineTo(toX, toY + length));
        if (roadMotionMode <= 0) {
            path.getElements().add(new LineTo(toX + 2, toY + length - 10));
            path.getElements().add(new LineTo(toX - 2, toY + length - 10));
            path.getElements().add(new LineTo(toX, toY + length));
        }
        path.getElements().add(new MoveTo(toX, toY));
        road = path;
        road.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                road.setStrokeWidth(3);
            }
        });
        road.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                road.setStrokeWidth(1);
            }
        });
    }


    private void setFill(Paint p) {
        road.setFill(p);
        road.setStroke(p);
    }

    private String getStatusMessage() {
        StringBuilder msg = new StringBuilder(" - ");
        switch (status) {
            case UI: {
                msg.append("Exists only at UI. Corresponding jade agent still is not available");
                break;
            }
            case READY: {
                msg.append("Corresponding jade agent successfully initialised and ready for work");
                break;
            }
            case DESTINATION: {
                msg.append("This road transport msg about searching destination");
                break;
            }
            case DISTANCE: {
                msg.append("This is road is calculating its price for movement");
                break;
            }
            case INUSE: {
                msg.append("This road is used by transport");
                break;
            }
        }
        return msg.toString();
    }

    private void setTooltip() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tooltip.setText(getName() + getStatusMessage());
            }
        });
    }

    public void changeStatus(Status status) {
        this.status = status;
        setTooltip();
        switch (status) {
            case UI: {
                setFill(uiPaint);
                break;
            }
            case READY: {
                setFill(readyPaint);
                break;
            }
            case INUSE: {
                setFill(inUsePaint);
                break;
            }
            case DESTINATION: {
                setFill(destiantionPaint);
                break;
            }
            case DISTANCE: {
                setFill(distancePaint);
                break;
            }
        }
    }

    public void changeStatus(String status) {
        changeStatus(Status.valueOf(status));
    }
}
