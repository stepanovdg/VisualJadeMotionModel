package by.bsu.kurs.stepanov.agents.control;

import by.bsu.kurs.stepanov.types.Constants;
import by.bsu.kurs.stepanov.types.Coordinates;
import by.bsu.kurs.stepanov.types.PurposeHandler;
import by.bsu.kurs.stepanov.visualisation.MapFX;
import by.bsu.kurs.stepanov.visualisation.Runner;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            initMapScene(getArguments());
            init(getArguments());
        } else {
            init(null);
        }

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
                        reply = chooseAction(msg);
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

    private ACLMessage chooseAction(ACLMessage msg) {
        AID sender = msg.getSender();
        String name = sender.getLocalName();
        String event = msg.getContent();
        if (nodeAgents.containsKey(sender)) {
            Coordinates coordinates = nodeAgents.get(sender);
            switch (event) {
                case Constants.READY: {
                    map.setNodeMarker(coordinates.getLatitude(), coordinates.getLongitude());
                    break;
                }
                default: {
                    break;
                }
            }
        } else if (roadAgents.contains(name)) {
            String[] coordinates = name.split(Constants.AID_NAME_GUID_SPLITTER);
            Coordinates from = nodeAgents.get(new AID(coordinates[0],ISGUUID));
            Coordinates to = nodeAgents.get(new AID(coordinates[1],ISGUUID));
            switch (event) {
                case Constants.READY: {
                    map.setRoad(from, to);
                    break;
                }
                default: {
                    break;
                }
            }
        } else {
            //transport
        }
        return null;
    }

    private void initMapScene(Object[] arguments) {
        if (arguments.length <= 0 || !(arguments[0] instanceof MapFX)) {
            return;
        }
        map = (MapFX) arguments[0];

    }

    private void init(Object[] arguments) {
        nodeAgents = new HashMap<>();
        roadAgents = new ArrayList<>();
        AgentContainer mainContainer = getContainerController();
        try {
            doWait(20000);
            generateNodes(mainContainer);
            generateRoads(mainContainer);
            generateTransport(mainContainer);
            agc1.start();
            ACLMessage msg = new ACLMessage(7);
            PurposeHandler ph = new PurposeHandler(Constants.START);
            msg.setContentObject(ph);
            msg.addReceiver(new AID("CAR1" + JADE_PREFIX, ISGUUID));
            msg.addReceiver(new AID("CAR2" + JADE_PREFIX, ISGUUID));
            doWait(50000);
            send(msg);
            System.out.println("Cars start to move");
        } catch (StaleProxyException | IOException e) {
            e.printStackTrace();
        }

    }

    private void generateTransport(AgentContainer mainContainer) throws StaleProxyException {
        createCar("CAR1", mainContainer, new AID("N0" + JADE_PREFIX, ISGUUID), new AID("N4" + JADE_PREFIX, ISGUUID));
        createCar("CAR2", mainContainer, new AID("N0" + JADE_PREFIX, ISGUUID), new AID("N5" + JADE_PREFIX, ISGUUID));
    }

    private void createCar(String car1, AgentContainer mainContainer, AID n0, AID n4) throws StaleProxyException {
        AgentController agc = Runner.createTransportAgent(car1, mainContainer, new Object[]{n0, n4});
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

    private void createRoad(String non1, AgentContainer mainContainer, int i, AID n0, AID n1, int i1) throws StaleProxyException {
        AgentController agc = Runner.createRoadAgent(non1, mainContainer, new Object[]{i, n0, n1, i1});
        agc.start();
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
        AgentController agc = Runner.createNodeAgent(name, mainContainer, roads);
        nodeAgents.put(new AID(name, ISGUUID), new Coordinates(lat, lng));
        agc.start();
    }
}
