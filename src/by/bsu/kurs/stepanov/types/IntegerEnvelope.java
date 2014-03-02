package by.bsu.kurs.stepanov.types;

import jade.util.leap.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 01.03.14
 * Time: 19:18
 * To change this template use File | Settings | File Templates.
 */
public class IntegerEnvelope implements Serializable {
    public IntegerEnvelope(Integer percent) {
        this.percent = percent;
    }

    public Integer percent;
}
