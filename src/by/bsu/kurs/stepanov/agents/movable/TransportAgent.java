package by.bsu.kurs.stepanov.agents.movable;

import by.bsu.kurs.stepanov.types.Constants;
import by.bsu.kurs.stepanov.types.PurposeHandler;
import by.bsu.kurs.stepanov.types.StringEnvelope;
import by.bsu.kurs.stepanov.utils.ExceptionUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Serializable;

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
        paintLog(Constants.READY, situated, destination);
        addBehaviour(new CyclicBehaviour(this) {

            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    ACLMessage reply = null;
                    try {
                        reply = chooseAction(msg);
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
        // System.out.println(msg);
        ACLMessage reply = null;
        PurposeHandler ph = (PurposeHandler) msg.getContentObject();
//        System.out.print("CAR purpose= " + ph.getPurpose() + this.getAID() + "\n");
        switch (ph.getPurpose()) {
            case Constants.ACTION_FOUND_DESTINATION: {
                AID road = (AID) ph.getObj()[0];
                startMotion(road);
                setSituated(road);
                paintLog(Constants.LOG, new StringEnvelope("Transport " + getAID() + " start move on road " + road));
                break;
            }
            case Constants.ACTION_STOP_MOTION: {
                AID road = msg.getSender();
                AID toNode = (AID) ph.getObj()[0];
                askForDestination(toNode);
                paintLog(Constants.LOG, new StringEnvelope("Transport " + getAID() + " end moving on road "
                        + road + "and ask node " + toNode + " about next road"));

                break;
            }
            case Constants.ACTION_DESTINATED: {
                paintLog(Constants.FINISH, getDestination());
                setSituated(getDestination());
                setDestination(null);
                break;
            }
            case Constants.START: {
                /*System.out.println("Transport " + getAID().getName() + " begin his journey from "
                        + getSituated() + " to " + getDestination());  */
                paintLog(Constants.START, getSituated());
                askForDestination(getSituated());
                break;
            }
        }
        return reply;
    }

    private void askForDestination(AID toNode) throws IOException {
        ACLMessage msg = new ACLMessage(Constants.MESSAGE);
        PurposeHandler ph = new PurposeHandler(Constants.ACTION_ASK_FOR_ROAD_AID, getDestination());
        msg.setContentObject(ph);
        msg.addReceiver(toNode);
        setSituated(toNode);
        send(msg);
    }

    private void startMotion(AID road) throws IOException {
        ACLMessage msg = new ACLMessage(Constants.MESSAGE);
        PurposeHandler ph = new PurposeHandler(Constants.ACTION_START_MOTION, getSituated());
        msg.setContentObject(ph);
        msg.addReceiver(road);
        send(msg);
    }

    private void paintLog(String event) {
        paintLog(event, new Serializable[0]);
    }

    private void paintLog(String event, Serializable... args) {
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
