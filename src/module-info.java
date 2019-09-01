module vas {
    requires javafx.fxml;
    requires javafx.controls;
    requires jade;

    opens game;
    opens game.agents;
    opens images;
}