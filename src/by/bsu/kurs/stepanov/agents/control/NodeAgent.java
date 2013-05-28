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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static jade.lang.acl.ACLMessage.FAILURE;

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
    private ACLMessage previousMessage;

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
        System.out.println("Node " + getAID().getName() + " ready to work.");

        addBehaviour(new CyclicBehaviour(this) {

            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    ACLMessage reply = null;
                    try {
                        if(msg.getSender().getLocalName().equals("ams")){
                            chooseAction(previousMessage);
                        }else{
                            reply = chooseAction(msg);
                            previousMessage = msg;
                        }

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
        System.out.println(msg);
        ACLMessage reply = null;
        switch (msg.getPerformative()) {
            case Constants.ACTION_FIND_DESTINATION: {
                //  AID dest = TrajectoryFactory.getDestinationAddress(msg);
                AID dest = (AID) msg.getContentObject();
                if (dest.equals(getAID())) {
                    reply = msg.createReply();
                    reply.setPerformative(Constants.ACTION_CALCULATE_DISTANCE);
                    reply.setContentObject(new PriceRuleObj<AID, Price>(dest, new Price()));

                } else {
                    if (distanceTable.containsKey(dest)) {
                        PriceRuleObj<AID, Price> dist = distanceTable.get(dest);
                        if (dist.isEmpty()) {

                        } else {
                            reply = msg.createReply();
                            reply.setPerformative(Constants.ACTION_CALCULATE_DISTANCE);
                            dist.setAddress(dest);
                            reply.setContentObject(dist);
                        }
                    } else {
                        distanceTable.put(dest, new PriceRuleObj<AID, Price>());
                        askForDistance(dest);
                    }
                }
                break;
            }
            case Constants.ACTION_CALCULATED_DISTANCE: {
                //  AID dest = TrajectoryFactory.getDestinationAddress(msg);
                PriceRuleObj<AID, Price> dist = (PriceRuleObj<AID, Price>) msg.getContentObject();
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

                } else {
                    //bad behaviour
                }
                break;
            }
            case Constants.ACTION_ASK_FOR_ROAD_AID: {
                System.out.println("asked for road aid at" + getName() + " aid" + getAID());
                AID dest = (AID) msg.getContentObject();
                System.out.println("get dest" + dest + " aid" + getAID());
                if (dest.equals(getAID())) {
                    reply = msg.createReply();
                    reply.setPerformative(Constants.ACTION_DESTINATED);
                } else {
                    if (distanceTable.containsKey(dest)) {
                        PriceRuleObj<AID, Price> dist = distanceTable.get(dest);
                        if (dist.isEmpty()) {
                            //in this case it should never used
                        } else {
                            reply = msg.createReply();
                            reply.setPerformative(Constants.ACTION_FOUND_DESTINATION);
                            reply.setContentObject(dist.getAddress());
                        }
                    } else {
                        distanceTable.put(dest, new PriceRuleObj<AID, Price>());
                        askForDistance(dest);
                    }
                }
                break;

            }
            case FAILURE: {
                reply = chooseAction(previousMessage);
                break;
            }
        }
        return reply;  //To change body of created methods use File | Settings | File Templates.
    }

    private void askToCalculate(AID destination, PriceRuleObj<AID, Price> dist) throws IOException {
        ACLMessage msg = new ACLMessage(Constants.ACTION_CALCULATE_DISTANCE);
        PriceRuleObj<AID, Price> dist1 = new PriceRuleObj<>(destination, dist.getDistance());
        msg.setContentObject(dist1);
        for (AID road : roadSet) {
            if (!road.equals(dist.getAddress())) {
                msg.addReceiver(road);
            }
        }
        send(msg);
    }

    private void askForDistance(AID dest) throws IOException {
        for (AID road : roadSet) {
            ACLMessage msg = new ACLMessage(Constants.ACTION_FIND_DESTINATION);
            System.out.println("To road " + road);
            msg.setContentObject(dest);
            msg.addReceiver(road);
            System.out.println("To road  objects setted" + road);
            send(msg);
        }
    }
}