package game;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainScreen extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Connect 4");
        primaryStage.setScene(new Scene(root, 950, 800));
        primaryStage.show();
        MainController controller = loader.getController();
        initGameAgent(controller);
    }

    private void initGameAgent(MainController controller) {
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "true");
        profile.setParameter(Profile.MAIN_PORT, "12344");
        ContainerController containerController = Runtime.instance().createMainContainer(profile);
        try {
            AgentController gameController = containerController.createNewAgent("GameAgent", "game.agents.GameAgent", null);
            gameController.start();
            AgentController playerController = containerController.createNewAgent("PlayerAgent", "game.agents.PlayerAgent", null);
            playerController.start();
            AgentController botController = containerController.createNewAgent("BotAgent", "game.agents.BotAgent", null);
            botController.start();

            controller.setPlayerAgent(playerController);
            controller.setGameAgent(gameController);
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
