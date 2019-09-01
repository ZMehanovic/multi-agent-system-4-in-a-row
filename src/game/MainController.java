package game;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

import static game.BoardSingleton.RESET_COMMAND;

public class MainController implements Initializable {
    public GridPane gameGrid;
    public Label lblWinner;
    private AgentController playerAgent, gameAgent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupGrid();
    }

    private void setupGrid() {
        resetGrid();
        Image selectionImage = new Image("images/selection_button.png");
        Image emptyFieldImage = new Image("images/empty_field.png");

        for (int i = 0; i < gameGrid.getColumnCount(); i++) {
            setImageToGrid(selectionImage, i, 0);
        }
        for (int columnIndex = 0, rowIndex = 1; rowIndex < gameGrid.getRowCount(); ) {
            setImageToGrid(emptyFieldImage, columnIndex, rowIndex);
            if (++columnIndex == gameGrid.getColumnCount()) {
                columnIndex = 0;
                rowIndex++;
            }
        }

        BoardSingleton.getInstance().setGameGrid(gameGrid);
        BoardSingleton.getInstance().setLabel(lblWinner);
    }

    private void resetGrid() {
        if (gameGrid.getChildren().size() > 0) {
            gameGrid.getChildren().removeAll(gameGrid.getChildren());
        }
    }

    private void setImageToGrid(Image emptyFieldImage, int columnIndex, int rowIndex) {
        ImageView emptyField = new ImageView(emptyFieldImage);

        emptyField.fitHeightProperty();
        emptyField.fitWidthProperty();
        emptyField.setFitHeight(100);
        emptyField.setFitWidth(100);
        emptyField.setId(PieceEnum.EMPTY.toString());
        GridPane.setRowIndex(emptyField, rowIndex);
        GridPane.setColumnIndex(emptyField, columnIndex);

        gameGrid.add(emptyField, columnIndex, rowIndex);
    }

    public void handleGridClick(MouseEvent mouseEvent) throws StaleProxyException {
        if(!lblWinner.getText().isEmpty()){
            lblWinner.setText("Please reset the board");
            return;
        }
        Node node = mouseEvent.getPickResult().getIntersectedNode();
        Integer rowIndex = GridPane.getRowIndex(node);
        if (rowIndex != null && rowIndex == 0) {
            playerAgent.putO2AObject(GridPane.getColumnIndex(node), AgentController.ASYNC);
        }

    }

    public void setPlayerAgent(AgentController playerController) {
        playerAgent = playerController;
    }

    public void setGameAgent(AgentController gameController) {
        this.gameAgent = gameController;
    }

    public void resetBoard(MouseEvent mouseEvent) throws StaleProxyException {
        gameAgent.putO2AObject(RESET_COMMAND, AgentController.ASYNC);
        setupGrid();
        lblWinner.setText("");
    }
}
