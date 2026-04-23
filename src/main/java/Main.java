import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rooms.GameManager;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        GameManager manager = new GameManager();
        Scene menu = manager.createMainMenu(primaryStage);

        primaryStage.setTitle("MindMaze: The Brain Reflex Lab");
        primaryStage.setScene(menu);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}