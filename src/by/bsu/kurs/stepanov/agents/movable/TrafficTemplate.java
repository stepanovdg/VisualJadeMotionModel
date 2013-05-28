package by.bsu.kurs.stepanov.agents.movable;


import by.bsu.kurs.stepanov.types.Coordinates;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 20.02.13
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */

public class TrafficTemplate extends Agent {

    private UUID id;
    private Coordinates selfCoordinates;
    private ArrayList<Coordinates> neighborsCoordinateses = new ArrayList<Coordinates>();

    @Override
    protected void setup() {
        System.out.println("Привет! агент " + getAID().getName() + " готов.");
        addBehaviour(new CyclicBehaviour(this) // Поведение агента исполняемое в цикле
        {

            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println(" – " + myAgent.getLocalName()
                            + " received: "
                            + msg.getContent());
                } //Вывод на экран локального имени агента и полученного сообщения
                block();
                //Блокируем поведение, пока в очереди сообщений агента не появится хотя бы одно сообщение
            }
        });
        AMSAgentDescription[] agents = null;
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults(new Long(-1));
            agents = AMSService.search(this, new AMSAgentDescription(), c);
        } catch (Exception e) {
            System.out.println("Problem searching AMS: " + e);

            e.printStackTrace();
        }

        for (int i = 0; i < 4; i++) {
            AID agentID = agents[i].getName();
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(agentID); // id агента которому отправляем сообщение
            msg.setLanguage("Russian"); //Язык
            msg.setContent("Ping"); //Содержимое сообщения

            send(msg); //отправляем сообщение
        }
    }
}
