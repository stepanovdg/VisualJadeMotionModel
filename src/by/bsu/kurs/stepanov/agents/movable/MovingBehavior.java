package by.bsu.kurs.stepanov.agents.movable;


import by.bsu.kurs.stepanov.types.Coordinates;
import jade.core.behaviours.CyclicBehaviour;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 27.02.13
 * Time: 22:08
 * To change this template use File | Settings | File Templates.
 */
public class MovingBehavior extends CyclicBehaviour {

    private Panel scene;
    private Coordinates oldCoordinates;
    private Coordinates currentCoordinates;
    private Coordinates newCoordinates;

    /**
     * Default constructor. It does not set the owner agent.
     */
    public MovingBehavior(Coordinates currentCoordinates, Panel scene) {
        this.currentCoordinates = currentCoordinates;
        this.scene = scene;
    }

    public Panel getScene() {
        return scene;
    }

    public void setScene(Panel scene) {
        this.scene = scene;
    }

    public Coordinates getOldCoordinates() {
        return oldCoordinates;
    }

    public void setOldCoordinates(Coordinates oldCoordinates) {
        this.oldCoordinates = oldCoordinates;
    }

    public Coordinates getNewCoordinates() {
        return newCoordinates;
    }

    public void setNewCoordinates(Coordinates newCoordinates) {
        this.newCoordinates = newCoordinates;
    }

    /**
     * Runs the behaviour. This abstract method must be implemented by
     * <code>Behaviour</code>subclasses to perform ordinary behaviour
     * duty. An agent schedules its behaviours calling their
     * <code>action()</code> method; since all the behaviours belonging
     * to the same agent are scheduled cooperatively, this method
     * <b>must not</b> enter in an endless loop and should return as
     * soon as possible to preserve agent responsiveness. To split a
     * long and slow task into smaller section, recursive behaviour
     * aggregation may be used.
     *
     * @see jade.core.behaviours.CompositeBehaviour
     */
    @Override
    public void action() {
        int targ = (int)(Math.random()*8);
        int distance = (int)(Math.random()*1000);
        int speed = (int)(Math.random()*distance/10);



    }
}
