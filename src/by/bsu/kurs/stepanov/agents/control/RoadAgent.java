package by.bsu.kurs.stepanov.agents.control;

import by.bsu.kurs.stepanov.types.Constants;
import by.bsu.kurs.stepanov.types.Price;
import by.bsu.kurs.stepanov.types.PriceRuleObj;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 21.05.13
 * Time: 13:21
 * To change this template use File | Settings | File Templates.
 */
public class RoadAgent extends Agent {

    private int length;

    private AID firstRoadEnd;
    private AID secondRoadEnd;

    private AID[] toFirstRoadEndStack;
    private AID[] toSecondRoadEndStack;

    public LinkedHashMap<AID, Integer> getWaitForTimer() {
        return waitForTimer;
    }

    private LinkedHashMap<AID, Integer> waitForTimer;

    /**
     * if 0 it means that this road is bothsided
     * positive int means that this road leads from firstRoadEnd
     * to secondRoadEnd
     * negative int means that this road leads from secondRoadEnd
     * to firstRoadEnd
     */
    private int roadMotionMode;

    public RoadAgent() {
    }

    public RoadAgent(int length, AID firstRoadEnd, AID secondRoadEnd) {
        new RoadAgent(length, firstRoadEnd, secondRoadEnd, 0);

    }

    public RoadAgent(int length, AID firstRoadEnd, AID secondRoadEnd, int roadMotionMode) {
        this.length = length;
        this.firstRoadEnd = firstRoadEnd;
        this.secondRoadEnd = secondRoadEnd;
        this.roadMotionMode = roadMotionMode;
        this.toFirstRoadEndStack = new AID[length + 1];
        this.toSecondRoadEndStack = new AID[length + 1];
        this.waitForTimer = new LinkedHashMap<>();

    }

    private void init(Object[] roadSet) {
        this.length = (int) roadSet[0];
        this.firstRoadEnd = (AID) roadSet[1];
        this.secondRoadEnd = (AID) roadSet[2];
        this.roadMotionMode = (int) roadSet[3];
        this.toFirstRoadEndStack = new AID[length + 1];
        this.toSecondRoadEndStack = new AID[length + 1];
        this.waitForTimer = new LinkedHashMap<>();
    }

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
        System.out.println("Road " + getAID().getName() + " ready to work.");
        addBehaviour(new CyclicBehaviour(this) {

            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    ACLMessage reply = null;
                    try {
                        reply = chooseAction(msg);
                    } catch (UnreadableException | IOException e) {
                        e.printStackTrace();  //TODO.
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

    private ACLMessage chooseAction(ACLMessage msg) throws UnreadableException, IOException {

        ACLMessage reply = null;
        switch (msg.getPerformative()) {
            case Constants.ACTION_CALCULATE_DISTANCE: {
                System.out.println(msg);
                //  AID dest = TrajectoryFactory.getDestinationAddress(msg);
                AID from = msg.getSender();
                if (roadMotionMode > 0) {
                    if (!from.equals(secondRoadEnd)) {
                        break;
                    }
                }
                if (roadMotionMode < 0) {
                    if (!from.equals(firstRoadEnd)) {
                        break;
                    }
                }
                PriceRuleObj<AID, Price> dist = (PriceRuleObj<AID, Price>) msg.getContentObject();
                dist.setDistance(calculate(dist.getDistance()));
                ACLMessage ask = new ACLMessage(Constants.ACTION_CALCULATED_DISTANCE);
                AID to = chooseOtherRoadEnd(from);
                if (to != null) {
                    ask.setContentObject(dist);
                    ask.addReceiver(to);
                    send(ask);
                }
                break;
            }
            case Constants.ACTION_FIND_DESTINATION: {
               /* System.out.println(msg);
                AID from = msg.getSender();
                AID to = chooseOtherRoadEnd(from);
                PriceRuleObj<AID, Price> dist = (PriceRuleObj<AID, Price>) msg.getContentObject();
                AID dest = dist.getAddress();
                if (to != null) {
                    ACLMessage ask = new ACLMessage(Constants.ACTION_FIND_DESTINATION);
                    ask.setContentObject(dest);
                    ask.addReceiver(to);
                    send(ask);
                }    */
                System.out.println(msg);
                AID from = msg.getSender();
                AID to = chooseOtherRoadEnd(from);
                AID dest = (AID) msg.getContentObject();
                if (to != null) {
                    ACLMessage ask = new ACLMessage(Constants.ACTION_FIND_DESTINATION);
                    ask.setContentObject(dest);
                    ask.addReceiver(to);
                    send(ask);
                }
                break;
            }
            case Constants.ACTION_START_MOTION: {
                System.out.println(msg);
                AID transport = msg.getSender();
                AID from = (AID) msg.getContentObject();
                AID to = chooseOtherRoadEnd(from);
                if (to != null) {
                    addToTransportStack(to, transport);
                }
                break;
            }
            case Constants.ACTION_TIMER: {
                moveInStacks();
                addToStacks();
            }
        }
        return reply;
    }

    private void addToStacks() {
        if (!getWaitForTimer().isEmpty()) {
            AID transportAID = getWaitForTimer().keySet().iterator().next();
            Integer stack = getWaitForTimer().get(transportAID);
            getWaitForTimer().remove(transportAID);
            if (stack == 1) {
                toFirstRoadEndStack[length] = transportAID;
            }
            if (stack == 2) {
                toSecondRoadEndStack[0] = transportAID;
            }
        }

    }

    private void moveInStacks() throws IOException {
        if (toFirstRoadEndStack[0] != null) {
            sendStopMotion(toFirstRoadEndStack[0], firstRoadEnd);
        }
        if (toSecondRoadEndStack[length] != null) {
            sendStopMotion(toSecondRoadEndStack[length], secondRoadEnd);
        }
        for (int i = 0; i < length; i++) {
            if (toFirstRoadEndStack[i + 1] != null) {
                toFirstRoadEndStack[i] = toFirstRoadEndStack[i + 1];
                toFirstRoadEndStack[i + 1] = null;
            }
            if (toSecondRoadEndStack[length - i - 1] != null) {
                toSecondRoadEndStack[length - i] = toSecondRoadEndStack[length - i - 1];
                toSecondRoadEndStack[length - i - 1] = null;
            }
        }
    }

    private void sendStopMotion(AID transport, AID roadEnd) throws IOException {
        ACLMessage msg = new ACLMessage(Constants.ACTION_STOP_MOTION);
        msg.setContentObject(roadEnd);
        msg.addReceiver(transport);
        send(msg);
    }

    private void addToTransportStack(AID destinationRoadEnd, AID transport) {
        Integer to = chooseRoadStack(destinationRoadEnd);
        if (to == null) {
            System.out.println("RoadEndNotFound");
            return;
        }
        getWaitForTimer().put(transport, to);
    }

    private AID chooseOtherRoadEnd(AID from) {
        if (from.equals(firstRoadEnd)) return secondRoadEnd;
        if (from.equals(secondRoadEnd)) return firstRoadEnd;
        return null;
    }

    private Integer chooseRoadStack(AID to) {
        if (to.equals(firstRoadEnd)) return 1;
        if (to.equals(secondRoadEnd)) return 2;
        return null;
    }

    private Price calculate(Price distance) {
        distance.setDistance(distance.getDistance() + length);
        return distance;
    }
}
