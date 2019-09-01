/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * <p>
 * GNU Lesser General Public License
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package game.agents;

import game.BoardSingleton;
import game.PieceEnum;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;

import static game.BoardSingleton.*;

public class GameAgent extends Agent {

    public static Map<Integer, Integer> validMoves = new HashMap<>();

    static {
        initializeBoard();
    }

    protected void setup() {
        setEnabledO2ACommunication(true, 0);
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = this.getAgent().receive();
                if (message != null && message.getContent() != null) {
                    Integer columnIndex = Integer.valueOf(message.getContent());

                    if (validMoves.containsKey(columnIndex) && validMoves.get(columnIndex) > 0) {
                        if (message.getAllUserDefinedParameters().get(PLAYER_KEY).equals(PLAYER_HUMAN)) {
                            executeMove(columnIndex, PieceEnum.RED);
                        } else {
                            executeMove(columnIndex, PieceEnum.YELLOW);
                        }
                    }
                } else {
                    resolveCommand(getO2AObject());
                }
                block();
            }
        });
    }

    private void informBotPlayer() {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.setContent("Your turn");
        message.addReplyTo(this.getAID());
        message.addReceiver(new AID("BotAgent", AID.ISLOCALNAME));
        send(message);
    }

    private void resolveCommand(Object message) {
        if (message != null) {
            switch (message.toString()) {
                case RESET_COMMAND: {
                    initializeBoard();
                }
            }
        }
    }

    private void executeMove(Integer columnIndex, PieceEnum pieceType) {
        Integer rowIndex = validMoves.get(columnIndex);
        GridPane gameGrid = BoardSingleton.getInstance().getGameGrid();
        Platform.runLater(() -> {
            boolean isHuman = pieceType.equals(PieceEnum.RED);
            ImageView piece = isHuman ? getInstance().getRedPiece() : getInstance().getYellowPiece();
            GridPane.setRowIndex(piece, rowIndex);
            GridPane.setColumnIndex(piece, columnIndex);
            gameGrid.add(piece, columnIndex, rowIndex);
            Integer nextRow = validMoves.get(columnIndex) - 1;
            validMoves.put(columnIndex, nextRow);
            String winner = checkForWinner(pieceType, columnIndex, rowIndex.intValue(), gameGrid);
            if (winner != null) {
                BoardSingleton.getInstance().getLabel().setText("Winner is: " + winner + "!!!!!!!!");
            } else if (isHuman) {
                informBotPlayer();
            }
        });
    }

    private String checkForWinner(PieceEnum pieceType, int columnIndex, int rowIndex, GridPane gameGrid) {
        String winner = null;
        int minColumn = getMinIndex(columnIndex);
        int maxColumn = getMaxIndex(columnIndex);
        int minRow = getMinRowIndex(rowIndex);
        int maxRow = getMaxIndex(rowIndex);
        boolean resultByRow = checkIfWinByRow(minColumn, maxColumn, rowIndex, gameGrid, pieceType, 0);
        boolean resultByColumn = checkIfWinByColumn(maxRow, minRow, columnIndex, gameGrid, pieceType, 0);
        boolean resultByBotLeftToRightDiagonal = checkIfWinByLeftToRightDiagonal(getMaxRowForLeftDiagonal(rowIndex, columnIndex), minRow, minColumn, maxColumn, gameGrid, pieceType, 0);
        boolean resultByTopLeftToRightDiagonal = checkIfWinByTopLeftToRightDiagonal(getMinRowForLeftDiagonal(rowIndex, columnIndex), maxRow, getMinColumnForLeftDiagonal(rowIndex, columnIndex), maxColumn, gameGrid, pieceType, 0);
        if (resultByRow || resultByColumn || resultByBotLeftToRightDiagonal || resultByTopLeftToRightDiagonal) {
            if (PieceEnum.RED.equals(pieceType)) {
                winner = PLAYER_HUMAN;
            } else {
                winner = PLAYER_BOT;
            }
        }

        return winner;
    }

    private int getMaxRowForLeftDiagonal(int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            return rowIndex == 6 ? rowIndex : rowIndex + 1;
        } else if (columnIndex == 2) {
            if (rowIndex == 6 || rowIndex == 5) {
                return 6;
            }
            return rowIndex + 2;
        }
        return getMaxIndex(rowIndex);
    }

    private int getMinColumnForLeftDiagonal(int rowIndex, int columnIndex) {
        if (rowIndex == 1) {
            return columnIndex;
        } else if (rowIndex == 2 && columnIndex > 0) {
            return columnIndex - 1;
        } else if (rowIndex == 3 && columnIndex > 1) {
            return columnIndex - 2;
        }
        return getMinIndex(columnIndex);
    }

    private int getMinRowForLeftDiagonal(int rowIndex, int columnIndex) {
        if (columnIndex < 3 && rowIndex > columnIndex) {
            return columnIndex - columnIndex;
        }
        return getMinIndex(columnIndex);
    }

    private boolean checkIfWinByLeftToRightDiagonal(int maxRow, int minRow, int minColumn, int maxColumn, GridPane gameGrid, PieceEnum pieceType, int repeats) {
        if (repeats == 4) {
            return true;
        }
        Node node = getNodeFromGridPane(gameGrid, minColumn, maxRow);
        boolean finalResult = false;
        if (node != null) {
            boolean isCorrectPiece = node.getId().equals(pieceType.toString());
            finalResult = checkIfWinByLeftToRightDiagonal(maxRow - 1, minRow, minColumn + 1, maxColumn, gameGrid, pieceType, isCorrectPiece ? repeats + 1 : 0);
        } else if (minColumn < maxColumn) {
            finalResult = checkIfWinByLeftToRightDiagonal(maxRow - 1, minRow, minColumn + 1, maxColumn, gameGrid, pieceType, 0);
        }

        return finalResult;
    }

    private boolean checkIfWinByTopLeftToRightDiagonal(int minRow, int maxRow, int minColumn, int maxColumn, GridPane gameGrid, PieceEnum pieceType, int repeats) {
        if (repeats == 4) {
            return true;
        }
        Node node = getNodeFromGridPane(gameGrid, minColumn, minRow);
        boolean finalResult = false;
        if (node != null) {
            boolean isCorrectPiece = node.getId().equals(pieceType.toString());
            finalResult = checkIfWinByTopLeftToRightDiagonal(minRow + 1, maxRow, minColumn + 1, maxColumn, gameGrid, pieceType, isCorrectPiece ? repeats + 1 : 0);
        } else if (minRow < maxRow) {
            finalResult = checkIfWinByTopLeftToRightDiagonal(minRow + 1, maxRow, minColumn + 1, maxColumn, gameGrid, pieceType, 0);
        }

        return finalResult;
    }

    private boolean checkIfWinByColumn(int maxRow, int minRow, Integer columnIndex, GridPane gameGrid, PieceEnum pieceType, int repeats) {
        if (repeats == 4) {
            return true;
        }
        Node node = getNodeFromGridPane(gameGrid, columnIndex, maxRow);
        boolean finalResult = false;
        if (node != null) {
            boolean isCorrectPiece = node.getId().equals(pieceType.toString());
            finalResult = checkIfWinByColumn(maxRow - 1, minRow, columnIndex, gameGrid, pieceType, isCorrectPiece ? repeats + 1 : 0);
        }

        return finalResult;
    }

    private int getMaxIndex(Integer columnIndex) {
        return columnIndex + 3 <= 6 ? columnIndex + 3 : 6;
    }

    private int getMinIndex(int columnIndex) {
        return columnIndex - 3 >= 0 ? columnIndex - 3 : 0;
    }

    private int getMinRowIndex(int rowIndex) {
        return rowIndex - 3 > 0 ? rowIndex - 3 : 1;
    }

    private boolean checkIfWinByRow(int minColumn, int maxColumn, int rowIndex, GridPane gameGrid, PieceEnum pieceType, int repeats) {
        if (repeats == 4) {
            return true;
        }
        Node node = getNodeFromGridPane(gameGrid, minColumn, rowIndex);
        boolean finalResult = false;
        if (node != null) {
            boolean isCorrectPiece = node.getId().equals(pieceType.toString());
            finalResult = checkIfWinByRow(minColumn + 1, maxColumn, rowIndex, gameGrid, pieceType, isCorrectPiece ? repeats + 1 : 0);
        } else if (minColumn < maxColumn) {
            finalResult = checkIfWinByRow(minColumn + 1, maxColumn, rowIndex, gameGrid, pieceType, 0);
        }

        return finalResult;
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (node != null && node.getId() != null &&
                    !node.getId().equals(PieceEnum.EMPTY.toString()) &&
                    GridPane.getColumnIndex(node) == col &&
                    GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private static void initializeBoard() {
        validMoves.put(0, 6);
        validMoves.put(1, 6);
        validMoves.put(2, 6);
        validMoves.put(3, 6);
        validMoves.put(4, 6);
        validMoves.put(5, 6);
        validMoves.put(6, 6);
    }
}

