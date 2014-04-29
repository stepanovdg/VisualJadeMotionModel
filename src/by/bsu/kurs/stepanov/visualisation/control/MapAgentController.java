package by.bsu.kurs.stepanov.visualisation.control;

import by.bsu.kurs.stepanov.types.Coordinates;
import by.bsu.kurs.stepanov.utils.ExceptionUtils;
import by.bsu.kurs.stepanov.visualisation.application.Runner;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.scene.Group;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 22.02.14
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
 */
public class MapAgentController {
    private AgentController mapAgent;
    private AgentContainer mapContainer;
    private EventsHandler eventsHandler;
    private Runner agentRunner;
    public Controller controller = null;

    public MapAgentController(String name, Group webViewGroup) {
        this(name, webViewGroup, null);
    }

    public MapAgentController(String name, Runner agentRunner, Group webViewGroup) {
        try {
            eventsHandler = new EventsHandler(webViewGroup, this);
            mapContainer = agentRunner.getMainContainer();
            //mapAgent = agentRunner.createMapAgent(name, mapContainer, new Object[]{eventsHandler});
            //mapAgent.start();
            agentRunner.run(eventsHandler);
        } catch (Throwable e) {
            ExceptionUtils.handleException(e);
        }
    }

    public MapAgentController(String minsk, Group webViewGroup, Controller controller) {
        try {
            eventsHandler = new EventsHandler(webViewGroup, this);
        } catch (Throwable e) {
            ExceptionUtils.handleException(e);
        }
        this.controller = controller;
    }

    public void startMoving(Object o) {

    }

    public void addNode(String name, Coordinates coordinates) {
        eventsHandler.addNodeMarker(name, coordinates, "UI");
    }

    public void init() {
        agentRunner = new Runner();
        mapContainer = agentRunner.getMainContainer();
        agentRunner.run(eventsHandler);
    }

    public void stop() {
        agentRunner.stop();
    }

    public void addRoad(Coordinates firstRoadEnd, Coordinates secondRoadEnd) {
        eventsHandler.getNodes();
    }

    public void setState(Controller.State state) {
        eventsHandler.setState(state);
    }

    public void export(File destFile) {
        eventsHandler.getLog().export(destFile);
    }

    public void importFromFile(File imp) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(imp);
            eventsHandler.parse(doc);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            ExceptionUtils.handleException(e);
        }

    }
}
