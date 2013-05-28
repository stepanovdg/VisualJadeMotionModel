package by.bsu.kurs.stepanov.visualisation;

import by.bsu.kurs.stepanov.types.Coordinates;
import jade.core.AID;

import javax.swing.*;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 27.02.13
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */
public class Scene extends JFrame implements Map {

    // Variables declaration - do not modify
    private javax.swing.JPanel jPanel2;
    private HashMap<AID,Coordinates> trajectories = new HashMap<>();
    // End of variables declaration

    public Scene() {
        initComponents();
    }


    private void initComponents() {
        jPanel2 = new javax.swing.JPanel();
        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        pack();
    }// </editor-fold>

    public void drawStep(Coordinates oldCoord, Coordinates newCoord) {
        jPanel2.getGraphics().drawLine(oldCoord.getLongitude().intValue(),oldCoord.getLatitude().intValue(),
                newCoord.getLongitude().intValue(),newCoord.getLatitude().intValue());
    }

    //set ui visible//
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Scene().setVisible(true);
            }
        });
    }


    @Override
    public void refresh() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void showLegends() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void filter(AID... aidsToSafe) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
