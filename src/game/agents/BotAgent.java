package game.agents;

import game.BoardSingleton;
import game.PieceEnum;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.scene.layout.GridPane;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import static game.BoardSingleton.*;
import static game.agents.GameAgent.validMoves;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class BotAgent extends Agent {

    protected void setup() {
        setEnabledO2ACommunication(true, 0);
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                Object message = receive();
                if (message != null) {
                    ACLMessage move = new ACLMessage(ACLMessage.INFORM);
                    move.setContent(handleNextMove());
                    move.addReplyTo(this.getAgent().getAID());
                    move.addUserDefinedParameter(PLAYER_KEY, PLAYER_BOT);
                    move.addReceiver(new AID("GameAgent", AID.ISLOCALNAME));
                    send(move);
                }
                block();
            }
        });
    }

    private String handleNextMove() {
        Map<Integer, List<Integer>> humanPieces = new HashMap<>();
        Map<Integer, List<Integer>> botPieces = new HashMap<>();
        BoardSingleton.getInstance().getGameGrid().getChildren().forEach(node -> {
            if (GridPane.getColumnIndex(node) != null) {
                setCurrentMoves(GridPane.getColumnIndex(node), GridPane.getRowIndex(node), node.getId(),
                        humanPieces, botPieces);
            }
        });

//        TODO currently it is just random :(
//        Integer winningMove = checkForWinningMove(botPieces);
        Integer randomMove = getRandomMove();
        return String.valueOf(randomMove);
    }

    private Integer getRandomMove() {
        Random random = new Random();
        List<Integer> columnsAvailable = validMoves.entrySet().stream()
                .filter(map -> map.getValue() > 0)
                .map(map -> map.getKey())
                .collect(Collectors.toList());

        return columnsAvailable.get(random.nextInt(columnsAvailable.size()));
    }

    private void setCurrentMoves(Integer columnIndex, Integer rowIndex, String id, Map<Integer, List<Integer>> humanPieces, Map<Integer, List<Integer>> botPieces) {
        if (rowIndex > 0) {
            switch (PieceEnum.valueOf(id)) {
                case RED:
                    resolveCurrentPlayerMoves(columnIndex, rowIndex, humanPieces);
                    break;
                case YELLOW:
                    resolveCurrentPlayerMoves(columnIndex, rowIndex, botPieces);
            }
        }
    }

    private void resolveCurrentPlayerMoves(Integer columnIndex, Integer rowIndex, Map<Integer, List<Integer>> moves) {
        moves.computeIfAbsent(columnIndex, k -> new ArrayList<>()).add(rowIndex);
    }
}
