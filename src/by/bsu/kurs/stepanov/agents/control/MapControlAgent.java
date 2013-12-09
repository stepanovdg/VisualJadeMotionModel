package by.bsu.kurs.stepanov.agents.control;

import by.bsu.kurs.stepanov.types.Constants;
import by.bsu.kurs.stepanov.types.PurposeHandler;
import by.bsu.kurs.stepanov.visualisation.Runner;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.IOException;
import java.util.ArrayList;
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
    private List<String> nodeAgents;
    private List<String> roadAgents;
    private AgentController agc1;


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
        init(getArguments());

    }

    private void init(Object[] arguments) {
        nodeAgents = new ArrayList<>();
        roadAgents = new ArrayList<>();
        AgentContainer mainContainer = getContainerController();
        try {
            generateNodes(mainContainer);
            generateRoads(mainContainer);
            generateTransport(mainContainer);
            agc1.start();
            ACLMessage msg = new ACLMessage(7);
            PurposeHandler ph = new PurposeHandler(Constants.START);
            msg.setContentObject(ph);
            msg.addReceiver(new AID("CAR1" + JADE_PREFIX,ISGUUID));
            //msg.addReceiver(new AID("CAR2" + JADE_PREFIX,ISGUUID));
            doWait(50000);
            send(msg);
            System.out.println("Cars start to move");
        } catch (StaleProxyException | IOException e) {
            e.printStackTrace();
        }

    }

    private void generateTransport(AgentContainer mainContainer) throws StaleProxyException {
        createCar("CAR1",mainContainer,new AID("N0" + JADE_PREFIX,ISGUUID),new AID("N4" + JADE_PREFIX,ISGUUID));
        createCar("CAR2",mainContainer,new AID("N0" + JADE_PREFIX,ISGUUID),new AID("N5" + JADE_PREFIX,ISGUUID));
    }

    private void createCar(String car1, AgentContainer mainContainer, AID n0, AID n4) throws StaleProxyException {
        AgentController agc = Runner.createTransportAgent(car1, mainContainer, new Object[]{n0, n4});
        agc.start();
    }

    private void generateRoads(AgentContainer mainContainer) throws StaleProxyException {
        createRoad("N0N1",mainContainer,3,new AID("N0" + JADE_PREFIX,ISGUUID),new AID("N1" + JADE_PREFIX,ISGUUID),0);
        createRoad("N1N2",mainContainer,4,new AID("N1" + JADE_PREFIX,ISGUUID),new AID("N2" + JADE_PREFIX,ISGUUID),0);
        createRoad("N1N3",mainContainer,4,new AID("N1" + JADE_PREFIX,ISGUUID),new AID("N3" + JADE_PREFIX,ISGUUID),0);
        createRoad("N3N5",mainContainer,4,new AID("N3" + JADE_PREFIX,ISGUUID),new AID("N5" + JADE_PREFIX,ISGUUID),0);
        createRoad("N2N4",mainContainer,5,new AID("N2" + JADE_PREFIX,ISGUUID),new AID("N4" + JADE_PREFIX,ISGUUID),0);
        createRoad("N4N5",mainContainer,3,new AID("N4" + JADE_PREFIX,ISGUUID),new AID("N5" + JADE_PREFIX,ISGUUID),0);
        createRoad("N4N6",mainContainer,4,new AID("N4" + JADE_PREFIX,ISGUUID),new AID("N6" + JADE_PREFIX,ISGUUID),0);
        createRoad("N5N7",mainContainer,4,new AID("N5" + JADE_PREFIX,ISGUUID),new AID("N7" + JADE_PREFIX,ISGUUID),0);

    }

    private void createRoad(String non1, AgentContainer mainContainer, int i, AID n0, AID n1, int i1) throws StaleProxyException {
        AgentController agc = Runner.createRoadAgent(non1,mainContainer,new Object[]{i,n0,n1,i1});
        agc.start();
    }

    private void generateNodes(AgentContainer mainContainer) throws StaleProxyException {
        AID road1 = new AID("N0N1" + JADE_PREFIX,ISGUUID);
        AID road2 = new AID("N1N2" + JADE_PREFIX,ISGUUID);
        AID road3 = new AID("N1N3" + JADE_PREFIX,ISGUUID);
        AID road4 = new AID("N3N5" + JADE_PREFIX,ISGUUID);
        AID road5 = new AID("N2N4" + JADE_PREFIX,ISGUUID);
        AID road6 = new AID("N4N5" + JADE_PREFIX,ISGUUID);
        AID road7 = new AID("N4N6" + JADE_PREFIX,ISGUUID);
        AID road8 = new AID("N5N7" + JADE_PREFIX,ISGUUID);
        AgentController agc;
        agc = Runner.createNodeAgent("N0", mainContainer, new Object[]{road1});
        nodeAgents.add("N0");
        agc.start();
        agc = Runner.createNodeAgent("N1", mainContainer, new Object[]{road1, road2, road3});
        nodeAgents.add("N1");
        agc.start();
        agc = Runner.createNodeAgent("N2", mainContainer, new Object[]{road2, road5});
        nodeAgents.add("N2");
        agc.start();
        agc = Runner.createNodeAgent("N3", mainContainer, new Object[]{road3, road4});
        nodeAgents.add("N3");
        agc.start();
        agc = Runner.createNodeAgent("N4", mainContainer, new Object[]{road5, road7, road6});
        nodeAgents.add("N4");
        agc.start();
        agc = Runner.createNodeAgent("N5", mainContainer, new Object[]{road4, road6, road8});
        nodeAgents.add("N5");
        agc.start();
        agc = Runner.createNodeAgent("N6", mainContainer, new Object[]{road7});
        nodeAgents.add("N6");
        agc.start();
        agc = Runner.createNodeAgent("N7", mainContainer, new Object[]{road8});
        nodeAgents.add("N7");
        agc.start();
        agc1 = Runner.createAtomicMotionAgent("A1", mainContainer, new Object[]{road1, road2, road3, road4, road5, road6, road7, road8});

    }
}
