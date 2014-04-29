package by.bsu.kurs.stepanov.agents.control;

import by.bsu.kurs.stepanov.types.Constants;
import by.bsu.kurs.stepanov.types.PurposeHandler;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 22.05.13
 * Time: 19:27
 * To change this template use File | Settings | File Templates.
 */
public class AtomicMotionAgent extends Agent {

    private ArrayList<AID> receivers = new ArrayList<>();

    private void init(Object[] roadSet) {
        this.receivers = new ArrayList<>();
        for (Object obj : roadSet) {
            this.receivers.add((AID) obj);
        }
    }

    @Override
    protected void setup() {
        init(getArguments());
        System.out.println("Atomic Timer " + getAID().getName() + " ready to work.");
        final ACLMessage msg = new ACLMessage(Constants.MESSAGE);
        PurposeHandler ph = new PurposeHandler(Constants.ACTION_TIMER);
        try {
            msg.setContentObject(ph);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (AID road : receivers) {
            msg.addReceiver(road);
        }
        addBehaviour(new WakerBehaviour(this, Constants.POLLING_TIME) {
            /**
             * This method is invoked when the deadline defined in the
             * constructor is reached (or when the timeout specified in the
             * constructor expires).
             * Subclasses are expected to define this method specifying the action
             * that must be performed at that time.
             */
            @Override
            protected void onWake() {
                send(msg);
                reset();
            }

        });
    }

}
