package by.bsu.kurs.stepanov.visualisation.agents;

import by.bsu.kurs.stepanov.types.Coordinates;
import by.bsu.kurs.stepanov.utils.CoordinatesUtils;
import jade.core.AID;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;


/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 06.02.14
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */
public class TransportAgentUi {
    public enum Status {UI, READY, MOVING, WAITING, FINISH}

    private Coordinates situated;
    private Coordinates destination;
    private Status status = Status.UI;
    private String name;
    private String destinationName;
    private AID aid;
    private ImageView image;
    private Line vector;
    private Tooltip imageTooltip;
    private static Image uiPointer = new Image("/icon15.png");
    private static Image readyPointer = new Image("/icon31r.png");
    private static Image movingPointer = new Image("/icon31.png");
    private static Image waitingPointer = new Image("/icon31w.png");
    private static Image finishPointer = new Image("/icon62.png");


    public TransportAgentUi(String name, Coordinates situated, Coordinates destination, String destinationName) {
        this.name = name;
        this.destinationName = destinationName;
        this.situated = situated;
        this.destination = destination;
        image = new ImageView();
        vector = new Line();
        image.setX(CoordinatesUtils.xFromWorld(situated) - (0.5) * (uiPointer.getWidth()));
        image.setY(CoordinatesUtils.yFromWorld(situated) - (0.5) * (uiPointer.getHeight()));
        image.setScaleX(0.5);
        image.setScaleY(0.5);
        setImage(uiPointer);
        vector.setStrokeDashOffset(10);
        vector.setVisible(false);
        calculateVector();

        imageTooltip = new Tooltip(this.name);
        Tooltip.install(image, imageTooltip);
        changeStatus(Status.UI);
        image.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                image.setScaleX(1);
                image.setScaleY(1);

                vector.setVisible(true);

            }
        });
        image.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                image.setScaleX(0.5);
                image.setScaleY(0.5);

                vector.setVisible(false);
            }
        });

    }

    public void calculateVector() {
        vector.setStartX(image.getX() + image.getImage().getWidth() / 2);
        vector.setStartY(image.getY() + image.getImage().getHeight() / 2);
        vector.setEndX(CoordinatesUtils.xFromWorld(destination));
        vector.setEndY(CoordinatesUtils.yFromWorld(destination));
    }

    private void setImage(Image img) {
        image.setImage(img);
    }

    public ImageView getTransportImage() {
        return image;
    }

    public Line getTransportVector() {
        return vector;
    }

    public Coordinates getSituated() {
        return situated;
    }

    public void setSituated(Coordinates situated) {
        this.situated = situated;
        if (situated.equals(destination)) {
            changeStatus(Status.FINISH);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void changeStatus(Status status) {
        this.status = status;

        setTooltip();
        switch (status) {
            case UI: {
                setImage(uiPointer);
                break;
            }
            case READY: {
                setImage(readyPointer);
                break;
            }
            case WAITING: {
                setImage(waitingPointer);
                break;
            }
            case MOVING: {
                setImage(movingPointer);
                break;
            }
            case FINISH: {
                setImage(finishPointer);
                break;
            }
        }
    }

    private void setTooltip() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                imageTooltip.setText(getName() + getStatusMessage());
            }
        });

    }

    private String getStatusMessage() {
        StringBuilder msg = new StringBuilder(" - ");
        switch (status) {
            case UI: {
                msg.append("Exists only at UI. Corresponding jade agent still is not available");
                break;
            }
            case READY: {
                msg.append("Corresponding jade agent successfully initialised");
                break;
            }
            case WAITING: {
                msg.append("This transport is waiting for answer from node or road (Traffic light)");
                break;
            }
            case MOVING: {
                msg.append("This transport is moving now");
                break;
            }
            case FINISH: {
                msg.append("This transport ends his journey");
                break;
            }
        }
        return msg.toString();
    }

    public void changeStatus(String status) {
        changeStatus(Status.valueOf(status));
    }
}
