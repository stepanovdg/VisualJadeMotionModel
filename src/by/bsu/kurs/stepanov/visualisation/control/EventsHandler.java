package by.bsu.kurs.stepanov.visualisation.control;

import by.bsu.kurs.stepanov.types.Constants;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 09.02.14
 * Time: 18:59
 * To change this template use File | Settings | File Templates.
 */
public class EventsHandler implements MapFX {

    private Logger log;
    private Group group;
    private Group nodeGr;
    private Group roadGr;
    private Group transportGr;
    private volatile Map<Coordinates, NodeAgentUi> nodes;
    private volatile Map<String, RoadAgentUi> roads;
    private volatile Map<String, TransportAgentUi> transports;
    private volatile Map<ImageView, NodeAgentUi> nodesByImage;
    private volatile Map<Path, RoadAgentUi> roadsByImage;
    private volatile Map<ImageView, TransportAgentUi> transportsByImage;
    private volatile Queue<MapEvent> mapEvents = new ArrayDeque<>();
    private Controller.State state;
    private final EventsHandler handler;
    private Coordinates firstRoadEnd = null;
    private Coordinates transportSituated = null;
    private Integer counter = 0;
    private Line roadDrawingVector = new Line();
    private Line transportDrawingVector = new Line();
    private ImageView lastNodeDragged = null;
    private boolean finished = false;
    private MapAgentController mac = null;


    public EventsHandler(Group webViewGroup, MapAgentController mapAgentController) {
        finished = false;
        mac = mapAgentController;
        log = new Logger();
        handler = this;
        setGroup(webViewGroup);
        roadDrawingVector.setVisible(false);
        roadDrawingVector.setScaleX(0.5);
        roadDrawingVector.setScaleY(0.5);
        roadGr.getChildren().add(roadDrawingVector);
        transportDrawingVector.setVisible(false);
        transportGr.getChildren().add(transportDrawingVector);
        nodes = new HashMap<>();
        roads = new HashMap<>();
        transports = new HashMap<>();
        nodesByImage = new HashMap<>();
        roadsByImage = new HashMap<>();
        transportsByImage = new HashMap<>();
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
                        ui.getImage().setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent paramT) {
                                if (paramT.isShiftDown()) {
                                    handler.removeNode(paramT.getSource());
                                } else if (state == Controller.State.ROAD) {
                                    handler.handleRoadEvent(paramT);
                                } else if (state == Controller.State.TRANSPORT) {
                                    handler.handleTransportEvent(paramT);
                                }
                            }
                        });
                        /*ui.getImage().setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent paramT) {
                                if (state == Controller.State.ROAD) {
                                    roadDrawingVector.setVisible(true);
                                    roadDrawingVector.setStartX(paramT.getX());
                                    roadDrawingVector.setStartY(paramT.getY());
                                    handler.handleRoadEvent(paramT);
                                } else if (state == Controller.State.TRANSPORT) {
                                    transportDrawingVector.setVisible(true);
                                    transportDrawingVector.setStartX(paramT.getX());
                                    transportDrawingVector.setStartY(paramT.getY());
                                    handler.handleTransportEvent(paramT);
                                }
                            }
                        });
                        ui.getImage().setOnMouseReleased(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent paramT) {
                                if (state == Controller.State.ROAD) {
                                    roadDrawingVector.setVisible(false);
                                    handler.handleRoadEvent(paramT);
                                } else if (state == Controller.State.TRANSPORT) {
                                    transportDrawingVector.setVisible(false);
                                    handler.handleTransportEvent(paramT);
                                }
                            }
                        });
                        ui.getImage().setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {
                            @Override
                            public void handle(MouseDragEvent paramT) {
                                System.out.println("lastNodeDragged = (ImageView) paramT.getSource()");
                                lastNodeDragged = (ImageView) paramT.getSource();
                            }
                        });
                        ui.getImage().setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
                            @Override
                            public void handle(MouseDragEvent paramT) {
                                System.out.println("lastNodeDragged = null");
                                lastNodeDragged = null;
                            }
                        });
                        ui.getImage().setOnMouseDragged(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent paramT) {
                                Double x = ((ImageView) paramT.getSource()).getX();
                                Double y = ((ImageView) paramT.getSource()).getY();

                                if (state == Controller.State.ROAD) {
                                    roadDrawingVector.setEndX(paramT.getX());
                                    roadDrawingVector.setEndY(paramT.getY());
                                } else if (state == Controller.State.TRANSPORT) {
                                    transportDrawingVector.setEndX(paramT.getX());
                                    transportDrawingVector.setEndY(paramT.getY());
                                }
                            }
                        }); */

                        ui.changeStatus(status);
                        nodes.put(coordinates, ui);
                        boolean flag = nodeGr.getChildren().add(ui.getImage());
                        if (flag) {
                            nodesByImage.put(ui.getImage(), ui);
                        }
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
                            ui.getRoad().setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent paramT) {
                                    if (paramT.isShiftDown()) {
                                        handler.removeRoad(paramT.getSource());
                                    }
                                }
                            });
                            ui.changeStatus(status);
                            roads.put(name, ui);
                            boolean flag = roadGr.getChildren().add(ui.getRoad());
                            if (flag) {
                                roadsByImage.put(ui.getRoad(), ui);
                            }
                            nodes.get(from).addRoad(name);
                            nodes.get(to).addRoad(name);
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
                            ui.getTransportImage().setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent paramT) {
                                    if (paramT.isShiftDown()) {
                                        handler.removeTransport(paramT.getSource());
                                    }
                                }
                            });
                            ui.changeStatus(status);
                            transports.put(name, ui);
                            boolean flag = transportGr.getChildren().add(ui.getTransportImage());
                            if (flag) {
                                transportsByImage.put(ui.getTransportImage(), ui);
                            }
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

    private void removeTransport(Object source) {
        TransportAgentUi ui = transportsByImage.get(source);
        transportGr.getChildren().remove(source);
        transports.remove(ui.getName());
    }

    private void removeRoad(Object source) {
        RoadAgentUi ui = roadsByImage.get(source);
        roadGr.getChildren().remove(source);
        roads.remove(ui.getName());
    }

    private void removeNode(Object source) {
        NodeAgentUi ui = nodesByImage.get(source);
        nodeGr.getChildren().remove(source);
        nodes.remove(ui.getCoordinates());
    }

    private void handleTransportEvent(MouseEvent paramT) {
        ImageView iv = (ImageView) paramT.getSource();
        NodeAgentUi ui = nodesByImage.get(iv);
        if (ui == null) return;
        if (transportSituated != null) {
            Coordinates transportEnd = ui.getCoordinates();
            if (transportSituated == transportEnd) return;
            addTransportMarker("Transport" + counter++, transportSituated, transportEnd, "UI");
            transportSituated = null;
        } else {
            transportSituated = ui.getCoordinates();
        }
    }

    private void handleRoadEvent(MouseEvent paramT) { //todo add dialog
        NodeAgentUi ui = nodesByImage.get(paramT.getSource());
        if (ui == null) return;
        if (firstRoadEnd != null) {
            Coordinates secondRoadEnd = ui.getCoordinates();
            if (firstRoadEnd == secondRoadEnd) return;
            String name = nodes.get(firstRoadEnd).getName() + Constants.COORDINATE_SPLITTER + ui.getName();
            addRoadMarker(name, firstRoadEnd, secondRoadEnd, 0, "UI");
            //ui.addRoad(name);
            //nodes.get(firstRoadEnd).addRoad(name);
            firstRoadEnd = null;
        } else {
            firstRoadEnd = ui.getCoordinates();
        }
    }

    @Override
    public void moveTransportMarker(final String name, final Coordinates next, final int roadPercent) {
//        System.out.println("move transport " + name + " to" + next + "percent" + roadPercent);
        mapEvents.add(new MapEvent(name, next, roadPercent));
    }

    @Override
    public void setRoad(Coordinates from, Coordinates to) {

    }

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public void finish() {
        finished = true;
        //log.close();
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
        if (finished && mapEvents.isEmpty()) {
            mac.controller.stopAgents(null);
            return;
        }
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

    public void reset() {
        nodeGr.getChildren().removeAll();
        nodes.clear();
        nodesByImage.clear();
        roadGr.getChildren().removeAll();
        roads.clear();
        roadsByImage.clear();
        transportGr.getChildren().removeAll();
        transports.clear();
        transportsByImage.clear();
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

    public Map<String, RoadAgentUi> getRoads() {
        return roads;
    }

    public void setRoads(Map<String, RoadAgentUi> roads) {
        this.roads = roads;
    }

    public Map<String, TransportAgentUi> getTransports() {
        return transports;
    }

    public void setTransports(Map<String, TransportAgentUi> transports) {
        this.transports = transports;
    }

    public Map<Coordinates, NodeAgentUi> getNodes() {
        return nodes;
    }

    public void setNodes(Map<Coordinates, NodeAgentUi> nodes) {
        this.nodes = nodes;
    }

    public void setState(Controller.State state) {
        this.state = state;
    }

    public void parse(Document doc) {
        /*Node agent = doc.getDocumentElement();
        NodeList nodesLists = null;
        NodeList roadsLists = null;
        NodeList transportsLists = null;
        Node iterator;
        for (int q = 0; q < agent.getChildNodes().getLength(); q++) {
            iterator = agent.getChildNodes().item(q);
            switch (iterator.getNodeName()) {
                case "nodes": {
                    nodesLists = iterator.getChildNodes();
                    break;
                }
                case "roads": {
                    roadsLists = iterator.getChildNodes();
                    break;
                }
                case "transports": {
                    transportsLists = iterator.getChildNodes();
                    break;
                }
                default: {
                    break;
                }
            }
        }

        NodeList nodes = nodesLists;
        NodeList roads = roadsLists;
        NodeList transports = transportsLists;
//        NodeList nodes = doc.getElementsByTagName("node");
//        NodeList roads = doc.getElementsByTagName("road");
//        NodeList transports = doc.getElementsByTagName("transport");
        Node node;
        String name;
        Map<String, Coordinates> nodeMap = new HashMap<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            node = nodes.item(i);
            if (node.getAttributes() == null) {
                continue;
            }
            name = node.getAttributes().getNamedItem("name").getNodeValue();
            NodeList coordinates = node.getChildNodes();
            String lng = null;
            String lat = null;
            for (int k = 0; k < coordinates.getLength(); k++) {
                Node coord = coordinates.item(k);
                if (coord.getNodeName().equals("longtitude")) {
                    lng = (coord.getNodeValue());
                } else if (coord.getNodeName().equals("latitude")) {
                    lat = (coord.getNodeValue());
                }
            }
            if (lng == null || lat == null) {
                continue;
            }
            Coordinates coordinate = new Coordinates(lat, lng);
            addNodeMarker(name, coordinate, "UI");
            nodeMap.put(name, coordinate);
        }
        for (int i = 0; i < roads.getLength(); i++) {
            node = roads.item(i);
            if (node.getAttributes() == null) {
                continue;
            }
            name = node.getAttributes().getNamedItem("name").getNodeValue();
            NodeList fromToMode = node.getChildNodes();
            String from = null;
            String to = null;
            String mode = null;
            for (int k = 0; k < fromToMode.getLength(); k++) {
                Node childNode = fromToMode.item(k);
                switch (childNode.getNodeName()) {
                    case "from": {
                        from = childNode.getNodeValue();
                        break;
                    }
                    case "to": {
                        to = childNode.getNodeValue();
                        break;
                    }
                    case "roadMode": {
                        mode = childNode.getNodeValue();
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
            if (from == null || to == null || mode == null) {
                continue;
            }
            Coordinates fr = nodeMap.get(from);
            Coordinates t = nodeMap.get(to);
            Integer mo = Integer.valueOf(mode);
            addRoadMarker(name, fr, t, mo, "UI");
        }
        for (int i = 0; i < transports.getLength(); i++) {
            node = transports.item(i);
            if (node.getAttributes() == null) {
                continue;
            }
            name = node.getAttributes().getNamedItem("name").getNodeValue();
            NodeList fromTo = node.getChildNodes();
            String from = null;
            String to = null;
            for (int k = 0; k < fromTo.getLength(); k++) {
                Node childNode = fromTo.item(k);
                switch (childNode.getNodeName()) {
                    case "from": {
                        from = childNode.getNodeValue();
                        break;
                    }
                    case "to": {
                        to = childNode.getNodeValue();
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
            if (from == null || to == null) {
                continue;
            }
            Coordinates fr = nodeMap.get(from);
            Coordinates t = nodeMap.get(to);
            addTransportMarker(name, fr, t, "UI");
        } */
        NodeList agents = getNode("agents", doc.getChildNodes()).getChildNodes();

        Node nodes = getNode("nodes", agents);
        Node roads = getNode("roads", agents);
        Node transports = getNode("transports", agents);

        Map<String, Coordinates> nodeMap = new HashMap<>();
        for (int x = 0; x < nodes.getChildNodes().getLength(); x++) {
            Node node = nodes.getChildNodes().item(x);
            if (node.getNodeName().equalsIgnoreCase("node")) {
                String name = getNodeAttr("name", node);
                Node coordinates = getNode("coordinates", node.getChildNodes());
                String lng = getNodeValue("longtitude", coordinates.getChildNodes());
                String lat = getNodeValue("latitude", coordinates.getChildNodes());
                if (lng == null || lat == null) {
                    continue;
                }
                Coordinates coordinate = new Coordinates(lat, lng);
                addNodeMarker(name, coordinate, "UI");
                nodeMap.put(name, coordinate);
            }
        }
        for (int x = 0; x < roads.getChildNodes().getLength(); x++) {
            Node node = roads.getChildNodes().item(x);
            if (node.getNodeName().equalsIgnoreCase("road")) {
                String name = getNodeAttr("name", node);
                String fromStr = getNodeValue("from", node.getChildNodes());
                String toStr = getNodeValue("to", node.getChildNodes());
                String modeStr = getNodeValue("roadMode", node.getChildNodes());
                if (fromStr == null || toStr == null || modeStr == null) {
                    continue;
                }
                Coordinates from = nodeMap.get(fromStr);
                Coordinates to = nodeMap.get(toStr);
                Integer mode = Integer.valueOf(modeStr);
                addRoadMarker(name, from, to, mode, "UI");
                //this.nodes.get(from).addRoad(name);
                //this.nodes.get(to).addRoad(name);
            }
        }
        for (int x = 0; x < transports.getChildNodes().getLength(); x++) {
            Node node = transports.getChildNodes().item(x);
            if (node.getNodeName().equalsIgnoreCase("transport")) {
                String name = getNodeAttr("name", node);
                String fromStr = getNodeValue("from", node.getChildNodes());
                String toStr = getNodeValue("to", node.getChildNodes());
                if (fromStr == null || toStr == null) {
                    continue;
                }
                Coordinates from = nodeMap.get(fromStr);
                Coordinates to = nodeMap.get(toStr);
                addTransportMarker(name, from, to, "UI");
            }
        }
        System.err.println("import done");
    }

    protected Node getNode(String tagName, NodeList nodes) {
        for (int x = 0; x < nodes.getLength(); x++) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                return node;
            }
        }

        return null;
    }

    protected String getNodeValue(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int x = 0; x < childNodes.getLength(); x++) {
            Node data = childNodes.item(x);
            if (data.getNodeType() == Node.TEXT_NODE)
                return data.getNodeValue();
        }
        return "";
    }

    protected String getNodeValue(String tagName, NodeList nodes) {
        for (int x = 0; x < nodes.getLength(); x++) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++) {
                    Node data = childNodes.item(y);
                    if (data.getNodeType() == Node.TEXT_NODE)
                        return data.getNodeValue();
                }
            }
        }
        return "";
    }

    protected String getNodeAttr(String attrName, Node node) {
        NamedNodeMap attrs = node.getAttributes();
        for (int y = 0; y < attrs.getLength(); y++) {
            Node attr = attrs.item(y);
            if (attr.getNodeName().equalsIgnoreCase(attrName)) {
                return attr.getNodeValue();
            }
        }
        return "";
    }

    protected String getNodeAttr(String tagName, String attrName, NodeList nodes) {
        for (int x = 0; x < nodes.getLength(); x++) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++) {
                    Node data = childNodes.item(y);
                    if (data.getNodeType() == Node.ATTRIBUTE_NODE) {
                        if (data.getNodeName().equalsIgnoreCase(attrName))
                            return data.getNodeValue();
                    }
                }
            }
        }

        return "";
    }
}
