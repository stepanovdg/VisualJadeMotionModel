package by.bsu.kurs.stepanov.visualisation;

import jade.core.AID;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 11.05.13
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
public interface Map {

   public  void refresh();

    public void showLegends();

    public void filter(AID... aidsToSafe);



}
