package by.bsu.kurs.stepanov.visualisation.control;

import by.bsu.kurs.stepanov.types.Coordinates;
import by.bsu.kurs.stepanov.utils.ExceptionUtils;
import by.bsu.kurs.stepanov.visualisation.application.Runner;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.scene.Group;

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

    public MapAgentController(String name, Group webViewGroup) {
        try {
            eventsHandler = new EventsHandler(webViewGroup);
        } catch (Throwable e) {
            ExceptionUtils.handleException(e);
        }
    }

    public MapAgentController(String name, Runner agentRunner, Group webViewGroup) {
        try {
            eventsHandler = new EventsHandler(webViewGroup);
            mapContainer = agentRunner.getMainContainer();
            //mapAgent = agentRunner.createMapAgent(name, mapContainer, new Object[]{eventsHandler});
            //mapAgent.start();
            agentRunner.run(eventsHandler);
        } catch (Throwable e) {
            ExceptionUtils.handleException(e);
        }
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
}
