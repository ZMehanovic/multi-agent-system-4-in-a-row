package game.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import static game.BoardSingleton.PLAYER_HUMAN;
import static game.BoardSingleton.PLAYER_KEY;

public class PlayerAgent extends Agent {
    protected void setup() {
        setEnabledO2ACommunication(true, 0);
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                Object message = getO2AObject();
                if(message != null){
                    System.out.println("message received:" + message);
                    ACLMessage move = new ACLMessage(ACLMessage.INFORM);
                    move.setContent(message.toString());
                    move.addReplyTo(this.getAgent().getAID());
                    move.addUserDefinedParameter(PLAYER_KEY, PLAYER_HUMAN);
                    move.addReceiver(new AID("GameAgent", AID.ISLOCALNAME));
                    send(move);
                }
               block();
            }
        });
    }

}
