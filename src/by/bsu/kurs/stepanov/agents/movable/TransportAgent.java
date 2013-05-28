package by.bsu.kurs.stepanov.agents.movable;

import by.bsu.kurs.stepanov.types.Constants;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 21.05.13
 * Time: 13:19
 * To change this template use File | Settings | File Templates.
 */
public class TransportAgent extends Agent {

    public AID getSituated() {
        return situated;
    }

    public void setSituated(AID situated) {
        this.situated = situated;
    }

    public AID getDestination() {
        return destination;
    }

    public void setDestination(AID destination) {
        this.destination = destination;
    }

    private AID situated;
    private AID destination;

    private void init(Object[] roadSet) {
        setSituated((AID) roadSet[0]);
        setDestination((AID) roadSet[1]);
    }

    @Override
    protected void setup() {
        init(getArguments());
        System.out.println("Transport " + getAID().getName() + " created.");
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
        System.out.println(msg);
        ACLMessage reply = null;
        switch (msg.getPerformative()) {
            case Constants.ACTION_FOUND_DESTINATION: {
                AID road = (AID) msg.getContentObject();
                startMotion(road);
                setSituated(road);
                break;
            }
            case Constants.ACTION_STOP_MOTION: {
                AID road = msg.getSender();
                AID toNode = (AID) msg.getContentObject();
                askForDestination(toNode);
                break;
            }
            case Constants.ACTION_DESTINATED: {
                System.out.println("Transport " + getAID().getName() + " ends his journey.");
                setSituated(getDestination());
                setDestination(null);
                break;
            }
            case Constants.START: {
                System.out.println("Transport " + getAID().getName() + " begin his journey from "
                        + getSituated() + " to " + getDestination());
                askForDestination(getSituated());
                break;
            }
        }
        return reply;
    }

    private void askForDestination(AID toNode) throws IOException {
        ACLMessage msg = new ACLMessage(Constants.ACTION_ASK_FOR_ROAD_AID);
        msg.setContentObject(getDestination());
        msg.addReceiver(toNode);
        setSituated(toNode);
        send(msg);
    }

    private void startMotion(AID road) throws IOException {
        ACLMessage msg = new ACLMessage(Constants.ACTION_START_MOTION);
        msg.setContentObject(getSituated());
        msg.addReceiver(road);
        send(msg);
    }
}
