package game;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class BoardSingleton {

    private static BoardSingleton INSTANCE = null;
    public static final String RESET_COMMAND = "RESET";
    public static final String PLAYER_KEY = "PLAYER";
    public static final String PLAYER_HUMAN = "HUMAN";
    public static final String PLAYER_BOT = "BOT";

    private GridPane gameGrid;
    private Label label;

    public static BoardSingleton getInstance(){
        if(INSTANCE == null){
            INSTANCE = new BoardSingleton();
        }
        return INSTANCE;
    }

    private BoardSingleton (){

    }

    private void setDefaultImageSize(ImageView image){
        image.fitHeightProperty();
        image.fitWidthProperty();
        image.setFitHeight(100);
        image.setFitWidth(100);
    }

    public ImageView getRedPiece() {
        ImageView redPiece = new ImageView(new Image("images/red_player.png"));
        setDefaultImageSize(redPiece);
        redPiece.setId(PieceEnum.RED.toString());
        return redPiece;
    }

    public ImageView getYellowPiece() {
        ImageView yellowPiece = new ImageView(new Image("images/yellow_player.png"));
        setDefaultImageSize(yellowPiece);
        yellowPiece.setId(PieceEnum.YELLOW.toString());
        return yellowPiece;
    }

    public ImageView getEmptyPiece() {
        ImageView emptyPiece = new ImageView(new Image("images/empty_field.png"));
        setDefaultImageSize(emptyPiece);
        emptyPiece.setId(PieceEnum.EMPTY.toString());
        return emptyPiece;
    }

    public void setGameGrid(GridPane gameGrid) {
        this.gameGrid = gameGrid;
    }

    public GridPane getGameGrid() {
        return gameGrid;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}
