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

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 06.02.14
 * Time: 21:31
 * To change this template use File | Settings | File Templates.
 */
public class NodeAgentUi {
    public enum Status {UI, READY, DESTINATION, FOUND_DESTINATION, DISTANCE}

    private Double lat;
    private Double lng;
    private Status status = Status.UI;
    private String name;
    private AID aid;
    private ImageView image;
    private Tooltip tooltip;
    private static Image uiPointer = new Image("/red.png");
    private static Image destinationPointer = new Image("/red-dot.png");
    private static Image foundDestinationPointer = new Image("/green.png");
    private static Image distancePointer = new Image("/yellow.png");
    private static Image readyPointer = new Image("/blue.png");

    public NodeAgentUi(String name, Coordinates coordinates) {
        this(name, coordinates.getLatitude(), coordinates.getLongitude());
    }

    public NodeAgentUi(String name, final Double lat, final Double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        image = new ImageView();
        image.setX(CoordinatesUtils.xFromWorld(lng) - (0.5) * (uiPointer.getWidth()));
        image.setY(CoordinatesUtils.yFromWorld(lat) - (1) * (uiPointer.getHeight()));
        image.setScaleX(0.5);
        image.setScaleY(0.5);
        image.setY(image.getY() + (0.25) * (uiPointer.getHeight()));
        tooltip = new Tooltip(this.name + "  -  " + status);
        Tooltip.install(image, tooltip);
        changeStatus(Status.UI);
        image.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                image.setScaleX(1);
                image.setScaleY(1);
                image.setY(image.getY() - (0.25) * (image.getImage().getHeight()));
            }
        });
        image.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                image.setScaleX(0.5);
                image.setScaleY(0.5);
                image.setY(image.getY() + (0.25) * (image.getImage().getHeight()));
            }
        });

    }

    private void setImage(Image img) {
        image.setImage(img);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AID getAid() {
        return aid;
    }

    public void setAid(AID aid) {
        this.aid = aid;
    }

    public ImageView getImage() {
        return image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeAgentUi that = (NodeAgentUi) o;

        if (!lat.equals(that.lat)) return false;
        if (!lng.equals(that.lng)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lat.hashCode();
        result = 31 * result + lng.hashCode();
        return result;
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
                msg.append("This node is searching the most suitable direction");
                break;
            }
            case DISTANCE: {
                msg.append("This is node is recalculating distance price for its roads");
                break;
            }
            case FOUND_DESTINATION: {
                msg.append("This successfully ends searching the destination/path ");
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
                setImage(uiPointer);
                break;
            }
            case READY: {
                setImage(readyPointer);
                break;
            }
            case DESTINATION: {
                setImage(destinationPointer);
                break;
            }
            case DISTANCE: {
                setImage(distancePointer);
                break;
            }
            case FOUND_DESTINATION: {
                setImage(foundDestinationPointer);
                break;
            }
        }
    }

    public void changeStatus(String status) {
        changeStatus(Status.valueOf(status));
    }
}
