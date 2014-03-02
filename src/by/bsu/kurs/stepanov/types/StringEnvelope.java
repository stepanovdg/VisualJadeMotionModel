package by.bsu.kurs.stepanov.types;

import jade.util.leap.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 02.03.14
 * Time: 19:06
 * To change this template use File | Settings | File Templates.
 */
public class StringEnvelope implements Serializable {
    public StringEnvelope(String string) {
        this.string = string;
    }

    private String string;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
