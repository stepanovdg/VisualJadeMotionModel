package chernovick;

import by.bsu.kurs.stepanov.types.Coordinates;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 18.03.13
 * Time: 12:15
 * To change this template use File | Settings | File Templates.
 */
public class Trajectory extends Step {

    private ArrayList<Step> steps = new ArrayList<>();

    public Trajectory(Coordinates from, Coordinates where) {
        super(from, where);
    }

    public void addStep(Step coord) {
        steps.add(coord);
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public boolean isUknownRoute() {
        return steps.isEmpty();
    }
}
