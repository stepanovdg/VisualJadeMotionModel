package by.bsu.kurs.stepanov.agents.control;

import by.bsu.kurs.stepanov.types.Constants;
import by.bsu.kurs.stepanov.types.Step;
import by.bsu.kurs.stepanov.types.Trajectory;
import by.bsu.kurs.stepanov.types.TrajectoryFactory;
import by.bsu.kurs.stepanov.visualisation.Scene;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 20.02.13
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */
public class PointTemplate extends Agent {

    private long pollingTime = Constants.POLLING_TIME;
    private HashMap<AID,Trajectory> traffics = new HashMap<>();
    private Scene globalMap = new Scene();

    public PointTemplate() {
        super();
    }

    protected void setup() {
        System.out.println("Station " + getAID().getName() + " ready to work.");
        addBehaviour(new CyclicBehaviour(this) {

            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    //checkSender(msg);
                    System.out.println("-" +
                            myAgent.getLocalName() +
                            " received: "
                            + msg.getContent());
                    //Вывод на экран локального имени агента и полученного сообщения
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Pong");
                    //Содержимое сообщения
                    send(reply); //отправляем сообщения
                }
                block();
            }
        });

    }

    private void checkSender(ACLMessage msg){
        AID sender = msg.getSender();
        if(traffics.containsKey(sender)){
           refreshTrack(msg);
        }  else{
           Trajectory traj = TrajectoryFactory.getTrajectory(msg);
           traffics.put(sender,traj);
        }
    }

    private void refreshTrack(ACLMessage msg) {
        AID sender = msg.getSender();
        Trajectory traj = traffics.get(sender);
        Step step = TrajectoryFactory.getStep(msg);
        traj.addStep(step);
       traffics.put(sender,traj);
    }
}
