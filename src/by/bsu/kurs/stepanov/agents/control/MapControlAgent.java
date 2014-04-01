package by.bsu.kurs.stepanov.agents.control;

import by.bsu.kurs.stepanov.types.*;
import by.bsu.kurs.stepanov.utils.ExceptionUtils;
import by.bsu.kurs.stepanov.visualisation.agents.NodeAgentUi;
import by.bsu.kurs.stepanov.visualisation.agents.RoadAgentUi;
import by.bsu.kurs.stepanov.visualisation.agents.TransportAgentUi;
import by.bsu.kurs.stepanov.visualisation.application.Runner;
import by.bsu.kurs.stepanov.visualisation.control.MapFX;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 23.05.13
 * Time: 18:37
 * To change this template use File | Settings | File Templates.
 */
public class MapControlAgent extends Agent {

    //private static final String JADE_PREFIX = "@10.6.16.254:1099/JADE";
    //private static final String JADE_PREFIX = "@Dmitriy-Note:1099/JADE";
    private static final String JADE_PREFIX = "";//"192.168.1.3:1099/JADE";
    private static final boolean ISGUUID = AID.ISLOCALNAME;
    private HashMap<AID, Coordinates> nodeAgents;
    private List<String> roadAgents;
    private AgentController agc1;
    private MapFX map;
    private List<String> transportAgents;


    /**
     * This protected method is an empty placeholder for application
     * specific startup code. Agent developers can override it to
     * provide necessary behaviour. When this method is called the agent
     * has been already registered with the Agent Platform <b>AMS</b>
     * and is able to send and receive messages. However, the agent
     * execution model is still sequential and no behaviour scheduling
     * is active yet.
     * <p/>
     * This method can be used for ordinary startup tasks such as
     * <b>DF</b> registration, but is essential to add at least a
     * <code>Behaviour</code> object to the agent, in order for it to be
     * able to do anything.
     *
     * @see jade.core.Agent#addBehaviour(jade.core.behaviours.Behaviour b)
     * @see jade.core.behaviours.Behaviour
     */
    @Override
    protected void setup() {
        addLogBehaviour();
        if (getArguments() != null) {
            try {
                initMapScene(getArguments());
                init(getArguments());
            } catch (StaleProxyException e) {
                ExceptionUtils.handleException(e);
                reset();
                init();
            }
        } else {
            init();
        }

    }

    private void reset() {
        //todo implement reset method
    }

    private void addLogBehaviour() {
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    ACLMessage reply = null;
                    if (msg.getSender().getLocalName().equals("ams")) {
                        System.out.println(msg);
                    } else {
                        try {
                            reply = chooseAction(msg);
                        } catch (UnreadableException e) {
                            ExceptionUtils.handleException(e);
                        }
                    }
                    if (reply != null) {
                        send(reply); //отправляем сообщения
                    }

                } else {
                    block();
                }
            }
        });
    }

    private ACLMessage chooseAction(ACLMessage msg) throws UnreadableException {
        AID sender = msg.getSender();
        String name = sender.getLocalName();
        PurposeHandler ph = (PurposeHandler) msg.getContentObject();
        String event = ph.getPurpose();
        if (nodeAgents.containsKey(sender)) {
            Coordinates coordinates = nodeAgents.get(sender);
            switch (event) {
                /*case Constants.READY: {
                    map.addNodeMarker(name, coordinates, "READY");
                    break;
                } */
                case Constants.STATUS: {
                    StringEnvelope env = (StringEnvelope) ph.getObj()[0];
                    map.addNodeMarker(name, coordinates, env.getString());
                    break;
                }
                default: {
                    break;
                }
            }
        } else if (roadAgents.contains(name)) {
            String[] coordinates = name.split(Constants.AID_NAME_GUID_SPLITTER);
            Coordinates from = nodeAgents.get(new AID(coordinates[0], ISGUUID));
            Coordinates to = nodeAgents.get(new AID(coordinates[1], ISGUUID));
            switch (event) {
                case Constants.READY: {
                    IntegerEnvelope mode = (IntegerEnvelope) ph.getObj()[0];
                    map.addRoadMarker(name, from, to, mode.percent, "READY");
                    break;
                }
                case Constants.TRANSPORT_MOVE: {
                    AID transport = (AID) ph.getObj()[0];
                    AID destination = (AID) ph.getObj()[1];
                    IntegerEnvelope integerEnvelope = (IntegerEnvelope) ph.getObj()[2];
                    Coordinates dest = nodeAgents.get(destination);
                    map.moveTransportMarker(transport.getLocalName(), dest, integerEnvelope.percent);
                    break;
                }
                case Constants.STATUS: {
                    StringEnvelope env = (StringEnvelope) ph.getObj()[0];
                    map.addRoadMarker(name, null, null, 0, env.getString());
                    break;
                }
                default: {
                    System.err.println(event);
                    break;
                }
            }
        } else {
            switch (event) {
                case Constants.READY: {  //todo add check for outofbounds and etc
                    AID situated = (AID) ph.getObj()[0];
                    AID destination = (AID) ph.getObj()[1];
                    Coordinates sit = nodeAgents.get(situated);
                    Coordinates dest = nodeAgents.get(destination);
                    map.addTransportMarker(name, sit, dest, "READY");
                    break;
                }
                case Constants.START: {
                    AID situated = (AID) ph.getObj()[0];
                    Coordinates sit = nodeAgents.get(situated);
                    map.moveTransportMarker(name, sit, 0);
                    break;
                }
                case Constants.FINISH: {
                    AID situated = (AID) ph.getObj()[0];
                    Coordinates dest = nodeAgents.get(situated);
                    map.moveTransportMarker(name, dest, 100);
                    break;
                }
                case Constants.STATUS: {
                    StringEnvelope env = (StringEnvelope) ph.getObj()[0];
                    map.addTransportMarker(name, null, null, env.getString());
                    break;
                }
                default: {
                    break;
                }
            }
        }
        return null;
    }

    private void initMapScene(Object[] arguments) {
        if (arguments.length <= 0 || !(arguments[0] instanceof MapFX)) {
            return;
        }
        map = (MapFX) arguments[0];
    }

    private void initRoads(Object[] arguments) {
        if (arguments.length <= 0 || !(arguments[1] instanceof List)) {
            return;
        }
        roadAgents = (List<String>) arguments[1];
    }

    private void initNodes(Object[] arguments) {
        if (arguments.length <= 0 || !(arguments[2] instanceof Map)) {
            return;
        }
        nodeAgents = (HashMap<AID, Coordinates>) arguments[0];
    }

    private void initTransport(Object[] arguments) {
        if (arguments.length <= 0 || !(arguments[3] instanceof List)) {
            return;
        }
        transportAgents = (List<String>) arguments[0];
    }

    private void init(Object[] arguments) throws StaleProxyException {
        if (map.getNodes().isEmpty()) {
            init();
            return;
        }
        nodeAgents = new HashMap<>();
        roadAgents = new ArrayList<>();
        transportAgents = new ArrayList<>();
        AgentContainer mainContainer = getContainerController();
        Map<Coordinates, NodeAgentUi> nodes = map.getNodes();
        Map<String, RoadAgentUi> roads = map.getRoads();
        Map<String, TransportAgentUi> transports = map.getTransports();
        NodeAgentUi naui;
        for (Coordinates coord : nodes.keySet()) {
            naui = nodes.get(coord);
            try {
                createNode(naui.getName(), mainContainer, coord, createAID(naui.getRoads()));
            } catch (StaleProxyException e) {
                ExceptionUtils.handleException(e);
            }
        }
        RoadAgentUi raui;
        for (String names : roads.keySet()) {
            raui = roads.get(names);
            try {
                Coordinates from = raui.getFrom();
                Coordinates to = raui.getTo();
                Double length = (Coordinates.countLength(from, to) * Constants.LENGTH_PER_COORDINATES);
                System.out.println("road " + raui.getName() + " length=" + length);
                createRoad(raui.getName(), mainContainer, length.intValue(),
                        createAID(nodes.get(from).getName()), createAID(nodes.get(to).getName()), raui.getRoadMotionMode());
            } catch (StaleProxyException e) {
                ExceptionUtils.handleException(e);
            }
        }
        TransportAgentUi taui;
        for (String names : transports.keySet()) {
            taui = transports.get(names);
            try {
                Coordinates from = taui.getSituated();
                Coordinates to = taui.getDestination();
                createCar(taui.getName(), mainContainer, createAID(nodes.get(from).getName()), createAID(nodes.get(to).getName()));
            } catch (StaleProxyException e) {
                ExceptionUtils.handleException(e);
            }
        }
        agc1 = Runner.createAtomicMotionAgent("A1", mainContainer, createAID(roadAgents));
        startTransport();
    }

    private void startTransport() {
        try {
            agc1.start();
            ACLMessage msg = new ACLMessage(Constants.MESSAGE);
            PurposeHandler ph = new PurposeHandler(Constants.START);
            msg.setContentObject(ph);
            for (String name : transportAgents) {
                msg.addReceiver(createAID(name));
            }
            doWait(10000);
            send(msg);
        } catch (StaleProxyException | IOException e) {
            ExceptionUtils.handleException(e);
        }

    }

    private void init() {
        nodeAgents = new HashMap<>();
        roadAgents = new ArrayList<>();
        transportAgents = new ArrayList<>();
        AgentContainer mainContainer = getContainerController();
        try {
            doWait(10000);
            generateNodes(mainContainer);
            generateRoads(mainContainer);
            generateTransport(mainContainer);
            agc1.start();
            ACLMessage msg = new ACLMessage(Constants.MESSAGE);
            PurposeHandler ph = new PurposeHandler(Constants.START);
            msg.setContentObject(ph);
            msg.addReceiver(new AID("CAR1" + JADE_PREFIX, ISGUUID));
            msg.addReceiver(new AID("CAR2" + JADE_PREFIX, ISGUUID));
//            doWait(50000);
            doWait(10000);
            send(msg);
//            System.out.println("Cars start to move");
        } catch (StaleProxyException | IOException e) {
            ExceptionUtils.handleException(e);
        }

    }

    private void generateTransport(AgentContainer mainContainer) throws StaleProxyException {
        createCar("CAR1", mainContainer, new AID("N0" + JADE_PREFIX, ISGUUID), new AID("N4" + JADE_PREFIX, ISGUUID));
        createCar("CAR2", mainContainer, new AID("N0" + JADE_PREFIX, ISGUUID), new AID("N5" + JADE_PREFIX, ISGUUID));
    }

    private void createCar(String name, AgentContainer mainContainer, AID situated, AID destination) throws StaleProxyException {
        AgentController agc = Runner.createTransportAgent(name, mainContainer, new Object[]{situated, destination});
        Coordinates from = nodeAgents.get(situated);
        Coordinates to = nodeAgents.get(destination);
        if (map != null) {
            map.addTransportMarker(name, from, to, "UI");
        }
        transportAgents.add(name);
        agc.start();
    }

    private void generateRoads(AgentContainer mainContainer) throws StaleProxyException {
        createRoad("N0:N1", mainContainer, 3, new AID("N0" + JADE_PREFIX, ISGUUID), new AID("N1" + JADE_PREFIX, ISGUUID), 0);
        createRoad("N1:N2", mainContainer, 4, new AID("N1" + JADE_PREFIX, ISGUUID), new AID("N2" + JADE_PREFIX, ISGUUID), 0);
        createRoad("N1:N3", mainContainer, 4, new AID("N1" + JADE_PREFIX, ISGUUID), new AID("N3" + JADE_PREFIX, ISGUUID), 0);
        createRoad("N3:N5", mainContainer, 4, new AID("N3" + JADE_PREFIX, ISGUUID), new AID("N5" + JADE_PREFIX, ISGUUID), 0);
        createRoad("N2:N4", mainContainer, 5, new AID("N2" + JADE_PREFIX, ISGUUID), new AID("N4" + JADE_PREFIX, ISGUUID), 0);
        createRoad("N4:N5", mainContainer, 3, new AID("N4" + JADE_PREFIX, ISGUUID), new AID("N5" + JADE_PREFIX, ISGUUID), 0);
        createRoad("N4:N6", mainContainer, 4, new AID("N4" + JADE_PREFIX, ISGUUID), new AID("N6" + JADE_PREFIX, ISGUUID), 0);
        createRoad("N5:N7", mainContainer, 4, new AID("N5" + JADE_PREFIX, ISGUUID), new AID("N7" + JADE_PREFIX, ISGUUID), 0);

    }

    private void createRoad(String name, AgentContainer mainContainer, int i, AID n0, AID n1, int roadMode) throws StaleProxyException {
        AgentController agc = Runner.createRoadAgent(name, mainContainer, new Object[]{i, n0, n1, roadMode});
        roadAgents.add(name);
        Coordinates from = nodeAgents.get(n0);
        Coordinates to = nodeAgents.get(n1);
        agc.start();
        if (map != null) {
            map.addRoadMarker(name, from, to, roadMode, "UI");
        }

    }


    private void generateNodes(AgentContainer mainContainer) throws StaleProxyException {
        AID road1 = new AID("N0:N1" + JADE_PREFIX, ISGUUID);
        AID road2 = new AID("N1:N2" + JADE_PREFIX, ISGUUID);
        AID road3 = new AID("N1:N3" + JADE_PREFIX, ISGUUID);
        AID road4 = new AID("N3:N5" + JADE_PREFIX, ISGUUID);
        AID road5 = new AID("N2:N4" + JADE_PREFIX, ISGUUID);
        AID road6 = new AID("N4:N5" + JADE_PREFIX, ISGUUID);
        AID road7 = new AID("N4:N6" + JADE_PREFIX, ISGUUID);
        AID road8 = new AID("N5:N7" + JADE_PREFIX, ISGUUID);
        AgentController agc;
        createNode("N0", mainContainer, 54.584796743678744, 23.302001953125, new Object[]{road1});
        createNode("N1", mainContainer, 54.92082843149136, 23.829345703125, new Object[]{road1, road2, road3});
        createNode("N2", mainContainer, 55.44771083630114, 22.730712890625, new Object[]{road2, road5});
        createNode("N3", mainContainer, 55.64659898563683, 24.36767578125, new Object[]{road3, road4});
        createNode("N4", mainContainer, 55.7642131648377, 21.18164062, new Object[]{road5, road7, road6});
        createNode("N5", mainContainer, 55.95535088453654, 23.302001953125, new Object[]{road4, road6, road8});
        createNode("N6", mainContainer, 55.979945357882315, 21.104736328125, new Object[]{road7});
        createNode("N7", mainContainer, 56.26165975623276, 23.609619140625, new Object[]{road8});
        agc1 = Runner.createAtomicMotionAgent("A1", mainContainer, new Object[]{road1, road2, road3, road4, road5, road6, road7, road8});

    }

    private void createNode(String name, AgentContainer mainContainer, Double lat, Double lng, Object[] roads) throws StaleProxyException {
        Coordinates coordinates = new Coordinates(lat, lng);
        createNode(name, mainContainer, coordinates, roads);
    }

    private void createNode(String name, AgentContainer mainContainer, Coordinates coordinates, Object[] roads) throws StaleProxyException {
        AgentController agc = Runner.createNodeAgent(name, mainContainer, roads);
        AID nameA = new AID(name, ISGUUID);
        nodeAgents.put(nameA, coordinates);
        agc.start();
        if (map != null) {
            map.addNodeMarker(nameA.getLocalName(), coordinates, "UI");
        }
    }

    private AID createAID(String name) {
        return new AID(name + JADE_PREFIX, ISGUUID);
    }

    private Object[] createAID(Collection<String> collection) {
        Collection<Object> aids = new ArrayList<>();
        for (String element : collection) {
            aids.add(createAID(element));
        }
        return aids.toArray();
    }
}
