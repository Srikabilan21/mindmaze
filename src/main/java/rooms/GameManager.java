package rooms;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameManager {

    public Scene createMainMenu(Stage stage) {
        Button startBtn = new Button("▶ Start Game");
        startBtn.setPrefWidth(200);

        startBtn.setOnAction(e -> {
            // Start the full game sequence
            GameFlow gameFlow = new GameFlow(stage);
            gameFlow.start(); // LogicRoom will be first
        });

        VBox layout = new VBox(20, startBtn);
        layout.setStyle("-fx-padding: 40; -fx-alignment: center;");

        Scene menuScene = new Scene(layout, 600, 400);

        // Optional: apply CSS if available
        try {
            menuScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {
            System.out.println("Stylesheet not found: " + ex.getMessage());
        }

        return menuScene;
    }
}
