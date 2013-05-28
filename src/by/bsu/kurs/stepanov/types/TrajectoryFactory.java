package by.bsu.kurs.stepanov.types;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 11.05.13
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
public class TrajectoryFactory {

    public static Trajectory getTrajectory(ACLMessage msg){
        //TODO  add pool for traectories
        String command = msg.getContent();
        String[] args = command.split(Constants.TRAJECTORY_SPLITTER);
        Coordinates from = new Coordinates(args[0]);
        Coordinates where = new Coordinates(args[1]);
        return new Trajectory(from,where);
    }

    public static Step getStep(ACLMessage msg){
          //TODO  add pool for traectories
          String command = msg.getContent();
          String[] args = command.split(Constants.TRAJECTORY_SPLITTER);
          Coordinates from = new Coordinates(args[0]);
          Coordinates where = new Coordinates(args[1]);
          return new Step(from,where);
      }

    public static AID getDestinationAddress(ACLMessage msg){
        String command = msg.getContent();
        String[] args = command.split(Constants.AID_NAME_GUID_SPLITTER);
        AID dest = new AID(args[0],Boolean.valueOf(args[1]));
        return dest;
    }
}
