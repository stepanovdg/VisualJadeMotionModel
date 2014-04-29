package by.bsu.kurs.stepanov.agents.control;

import by.bsu.kurs.stepanov.types.*;
import by.bsu.kurs.stepanov.utils.ExceptionUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Serializable;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 21.05.13
 * Time: 13:21
 * To change this template use File | Settings | File Templates.
 */
public class NodeAgent extends Agent {

    private HashSet<AID> roadSet = new LinkedHashSet<>();
    private HashMap<AID, PriceRuleObj<AID, Price>> distanceTable = new HashMap<>();
    private HashMap<AID, AID> carSet = new HashMap<>();
    private ACLMessage previousMessage;
    private ACLMessage previousReply;

    public NodeAgent() {
    }

    public NodeAgent(HashSet<AID> roadSet) {
        this.roadSet = roadSet;

    }

    public NodeAgent(Object[] roadSet) {
        init(roadSet);
    }

    private void init(Object[] roadSet) {
        this.roadSet = new HashSet<>();
        for (Object obj : roadSet) {
            this.roadSet.add((AID) obj);
        }
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
        paintLog(Constants.STATUS, new StringEnvelope("READY"));

        addBehaviour(new CyclicBehaviour(this) {

            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    ACLMessage reply = null;
                    try {
                        if (!msg.getSender().getLocalName().equals("ams")) {
                            reply = chooseAction(msg);
                            previousMessage = msg;
                            previousReply = reply;
                        }
                    } catch (UnreadableException | IOException e) {
                        ExceptionUtils.handleException(e);
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
        PurposeHandler ph = (PurposeHandler) msg.getContentObject();
        paintLog(ph.getPurpose());
        switch (ph.getPurpose()) {
            case Constants.ACTION_FIND_DESTINATION: {
                AID dest = (AID) ph.getObj()[0];
                if (dest.equals(getAID())) {
                    // Found destination ask previous to calculate distance
                    reply = msg.createReply();
                    reply.setPerformative(Constants.MESSAGE);
                    PurposeHandler ph1 = new PurposeHandler(Constants.ACTION_CALCULATE_DISTANCE, new PriceRuleObj<AID, Price>(dest, new Price()));
                    paintLog(Constants.STATUS, new StringEnvelope("DISTANCE"));
                    paintLog(Constants.LOG, new StringEnvelope("Node " + getAID() + "found that it is finish node for some way so its answer previous node ("
                            + msg.getSender() + ") to begin calculation price for this way"));
                    reply.setContentObject(ph1);

                } else {
                    if (distanceTable.containsKey(dest)) {
                        PriceRuleObj<AID, Price> dist = distanceTable.get(dest);
                        if (dist.isEmpty()) {

                        } else {
                            reply = msg.createReply();
                            reply.setPerformative(Constants.MESSAGE);
                            dist.setAddress(dest);
                            PurposeHandler ph1 = new PurposeHandler(Constants.ACTION_CALCULATE_DISTANCE, dist);
                            paintLog(Constants.STATUS, new StringEnvelope("DISTANCE"));
                            paintLog(Constants.LOG, new StringEnvelope("Node " + getAID() + "for request to find way to" + dest + "found that it already contain cached working way to this node so its answer previous node ("
                                    + msg.getSender() + ") to proceed calculation price for this way with some calulated part"));
                            reply.setContentObject(ph1);
                        }
                    } else {
                        distanceTable.put(dest, new PriceRuleObj<AID, Price>());
                        askForDistance(dest);
                    }
                }
                break;
            }
            case Constants.ACTION_CALCULATED_DISTANCE: {
                PriceRuleObj<AID, Price> dist = (PriceRuleObj<AID, Price>) ph.getObj()[0];
                AID dest = dist.getAddress();
                if (distanceTable.containsKey(dest)) {
                    PriceRuleObj<AID, Price> previousBestDist = distanceTable.get(dest);
                    if (previousBestDist.isEmpty()) {
                        dist.setAddress(msg.getSender());
                        distanceTable.put(dest, dist);
                        askToCalculate(dest, dist);
                    } else {
                        if (dist.compareTo(previousBestDist) < 0) {
                            dist.setAddress(msg.getSender());
                            distanceTable.put(dest, dist);
                        }
                    }
                    if (carSet.containsValue(dest)) {
                        permitMotion(dest);
                    }
                } else {
                    paintLog(Constants.LOG, new StringEnvelope("Tupic in" + getAID() + "while searching " + dest));
                    //bad behaviour
                }
                break;
            }
            case Constants.ACTION_ASK_FOR_ROAD_AID: {
                AID dest = (AID) ph.getObj()[0];
                if (dest.equals(getAID())) {
                    reply = msg.createReply();
                    reply.setPerformative(Constants.MESSAGE);
                    PurposeHandler ph1 = new PurposeHandler(Constants.ACTION_DESTINATED);
                    paintLog(Constants.STATUS, new StringEnvelope("FOUND_DESTINATION"));
                    paintLog(Constants.LOG, new StringEnvelope("Node " + getAID() + "was asked for road leading to " + dest +
                            " and realize this transport has no needness to drive (it`s already here)"));
                    reply.setContentObject(ph1);
                } else {
                    if (distanceTable.containsKey(dest)) {
                        PriceRuleObj<AID, Price> dist = distanceTable.get(dest);
                        if (dist.isEmpty()) {
                            //in this case it should never used
                            paintLog(Constants.LOG, new StringEnvelope("Node " + getAID() + "realize that smth bad was happens: it contains way to " + dest +
                                    " but price for this way is null(("));
                            paintLog(Constants.STATUS, new StringEnvelope("DISTANCE"));
                            askForDistance(dest); // check
                        } else {
                            reply = msg.createReply();
                            reply.setPerformative(Constants.MESSAGE);
                            reply.setContent(Constants.ACTION_FOUND_DESTINATION);
                            PurposeHandler ph1 = new PurposeHandler(Constants.ACTION_FOUND_DESTINATION, dist.getAddress());
                            paintLog(Constants.LOG, new StringEnvelope("Node " + getAID() + "was asked for road leading to " + dest +
                                    " and as its already contains way to it answer with road name " + dist));
                            paintLog(Constants.STATUS, new StringEnvelope("FOUND_DESTINATION"));
                            reply.setContentObject(ph1);
                        }
                    } else {
                        distanceTable.put(dest, new PriceRuleObj<AID, Price>());
                        carSet.put(msg.getSender(), dest);
                        paintLog(Constants.STATUS, new StringEnvelope("DISTANCE"));
                        askForDistance(dest);
                    }
                }
                break;

            }
        }
        return reply;
    }

    private void permitMotion(AID destination) throws IOException {
        paintLog(Constants.LOG, new StringEnvelope("Node " + getAID() + "realize that some cars waiting from it answer about the road leading to ("
                + destination + ") can start their journey as the way was found."));
        ACLMessage msg = new ACLMessage(Constants.MESSAGE);
        PurposeHandler ph = new PurposeHandler(Constants.ACTION_FOUND_DESTINATION, distanceTable.get(destination).getAddress());
        paintLog(Constants.STATUS, new StringEnvelope("FOUND_DESTINATION"));
        msg.setContentObject(ph);
        for (AID car : carSet.keySet()) {
            AID dest = carSet.get(car);
            if (dest.equals(destination)) {
                msg.addReceiver(car);
            }
        }
        send(msg);
    }

    private void askToCalculate(AID destination, PriceRuleObj<AID, Price> dist) throws IOException {
        ACLMessage msg = new ACLMessage(Constants.MESSAGE);
        paintLog(Constants.LOG, new StringEnvelope("Node " + getAID() + "receive request that way for some destination ("
                + destination + ") it was asking before found so now this way is calculating its price."));
        PriceRuleObj<AID, Price> dist1 = new PriceRuleObj<>(destination, dist.getDistance());
        PurposeHandler ph = new PurposeHandler(Constants.ACTION_CALCULATE_DISTANCE, dist1);
        paintLog(Constants.STATUS, new StringEnvelope("DISTANCE"));
        msg.setContentObject(ph);
        for (AID road : roadSet) {
            if (!road.equals(dist.getAddress())) {
                msg.addReceiver(road);
            }
        }
        send(msg);
    }

    private void askForDistance(AID dest) throws IOException {
        paintLog(Constants.LOG, new StringEnvelope("Node " + getAID() + " ask all his roads to find way to (" + dest + ") "));
        for (AID road : roadSet) {
            ACLMessage msg = new ACLMessage(Constants.MESSAGE);
            PurposeHandler ph = new PurposeHandler(Constants.ACTION_FIND_DESTINATION, dest);
            paintLog(Constants.STATUS, new StringEnvelope("DESTINATION"));
            msg.setContentObject(ph);
            msg.addReceiver(road);
            send(msg);
        }
    }

    private void paintLog(String event) {
        paintLog(event, new Serializable[0]);
    }

    private void paintLog(String event, jade.util.leap.Serializable... args) {
        if (event.equals(Constants.STATUS) && !Constants.STATUS_LOG_ENABLED) {
            return;
        }
        ACLMessage msg = new ACLMessage(Constants.MESSAGE);
        msg.addReceiver(new AID(Constants.LOG_AGENT_NAME, AID.ISLOCALNAME));
        PurposeHandler ph = new PurposeHandler(event, args);
        try {
            msg.setContentObject(ph);
        } catch (IOException e) {
            ExceptionUtils.handleException(e);
        }
        send(msg);
    }
}
