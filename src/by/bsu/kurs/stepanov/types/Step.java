package by.bsu.kurs.stepanov.types;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 11.05.13
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
public class Step {

    protected Coordinates from;
    protected Coordinates where;

    public Step(Coordinates from, Coordinates where) {
        this.from = from;
        this.where = where;
    }
}
