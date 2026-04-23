package rooms;

import javafx.stage.Stage;
import javafx.scene.Scene;

public class GameFlow {
    private Stage stage;
    private int currentRoomIndex = 0;

    public GameFlow(Stage stage) {
        this.stage = stage;
    }

    // Start the first room
    public void start() {
        loadNextRoom();
    }

    // Move to the next room
    public void loadNextRoom() {
        Scene nextScene = null;

        switch (currentRoomIndex) {
            case 0 -> {
                LogicRoom logicRoom = new LogicRoom(stage, this);
                nextScene = logicRoom.createScene();
            }
            case 1 -> {
                TypingRoom typingRoom = new TypingRoom(stage, this);
                nextScene = typingRoom.createScene();
            }
            case 2 -> {
                MemoryRoom memoryRoom = new MemoryRoom(stage, this);
                nextScene = memoryRoom.createScene();
            }
            case 3 -> {
                ReflexRoom reflexRoom = new ReflexRoom(stage, this);
                nextScene = reflexRoom.createScene();
            }
            case 4 -> {
                MazeRoom mazeRoom = new MazeRoom(stage,this);
                nextScene = mazeRoom.createScene();
            }
            default -> {
                // Game finished → show final scene
                nextScene = createGameOverScene();
            }
        }

        if (nextScene != null) {
            stage.setScene(nextScene);
        }

        currentRoomIndex++;
    }

    // Simple "Game Over" scene without GameManager
    private Scene createGameOverScene() {
        javafx.scene.control.Label gameOverLabel = new javafx.scene.control.Label(
                "🏁 Congratulations! You finished all rooms!"
        );
        gameOverLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: green;");

        javafx.scene.layout.VBox layout = new javafx.scene.layout.VBox(20, gameOverLabel);
        layout.setStyle("-fx-padding: 40; -fx-alignment: center;");

        return new Scene(layout, 600, 400);
    }
}
