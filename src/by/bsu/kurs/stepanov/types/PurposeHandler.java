package by.bsu.kurs.stepanov.types;

import jade.util.leap.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 16.11.13
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
public class PurposeHandler implements Serializable {

    private String purpose = "";
    private Serializable[] obj;

    public PurposeHandler(String purpose) {
        this.purpose = purpose;
        this.obj = null;
    }

    public PurposeHandler(String purpose, Serializable... obj) {
        this.purpose = purpose;
        this.obj = obj;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Serializable[] getObj() {
        return obj;
    }

    public void setObj(Serializable... obj) {
        this.obj = obj;
    }
}
