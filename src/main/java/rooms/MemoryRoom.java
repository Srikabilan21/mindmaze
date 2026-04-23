package rooms;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryRoom extends Application {

    private List<Button> cards = new ArrayList<>();
    private Button firstSelected = null;
    private Button secondSelected = null;
    private int score = 0;
    private int timeLeft = 60;
    private Timeline timer;
    private Text scoreText;
    private Text timerText;
    private Stage stage;
    private GameFlow gameFlow;

    // ✅ Constructor for GameFlow integration
    public MemoryRoom(Stage stage, GameFlow gameFlow) {
        this.stage = stage;
        this.gameFlow = gameFlow;
    }

    // ✅ Scene creation method
    public Scene createScene() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        scoreText = new Text("⭐ Score: 0");
        scoreText.setFont(Font.font(18));

        timerText = new Text("⏱ Time: " + timeLeft);
        timerText.setFont(Font.font(18));

        VBox topBar = new VBox(10, scoreText, timerText);
        topBar.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, topBar, grid);
        root.setAlignment(Pos.CENTER);

        setupCards(grid);
        startTimer();

        return new Scene(root, 600, 400);
    }

    private void setupCards(GridPane grid) {
        List<String> values = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            values.add(String.valueOf(i));
            values.add(String.valueOf(i));
        }
        Collections.shuffle(values);

        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                Button card = new Button("?");
                card.setMinSize(80, 80);
                String value = values.get(index);
                card.setUserData(value);

                card.setOnAction(e -> handleCardClick(card));

                grid.add(card, col, row);
                cards.add(card);
                index++;
            }
        }
    }

    private void handleCardClick(Button card) {
        if (firstSelected != null && secondSelected != null) return;

        card.setText(card.getUserData().toString());

        if (firstSelected == null) {
            firstSelected = card;
        } else if (secondSelected == null && card != firstSelected) {
            secondSelected = card;
            checkForMatch();
        }
    }

    private void checkForMatch() {
        if (firstSelected.getUserData().equals(secondSelected.getUserData())) {
            score++;
            scoreText.setText("⭐ Score: " + score);
            firstSelected.setDisable(true);
            secondSelected.setDisable(true);
            resetSelection();

            if (score == 8) { // All pairs found
                endGame(true);
            }
        } else {
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                firstSelected.setText("?");
                secondSelected.setText("?");
                resetSelection();
            }));
            delay.play();
        }
    }

    private void resetSelection() {
        firstSelected = null;
        secondSelected = null;
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerText.setText("⏱ Time: " + timeLeft);
            if (timeLeft <= 0) {
                endGame(false);
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void endGame(boolean won) {
        timer.stop();

        // ✅ Save score into ScoreManager
        ScoreManager.getInstance().setScore("MemoryRoom", score);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(won ? "🎉 You Won!" : "⏰ Time's Up!");
        alert.setContentText("Final Score: " + score);

        alert.setOnHidden(e -> {
            if (gameFlow == null) {
                // Standalone mode → Back to menu
                GameManager gm = new GameManager();
                stage.setScene(gm.createMainMenu(stage));
            } else {
                // Part of main game → Move to next room
                gameFlow.loadNextRoom();
            }
        });

        alert.show();
    }

    @Override
    public void start(Stage stage) {
        // ✅ Standalone run
        Scene scene = createScene();
        stage.setScene(scene);
        stage.setTitle("🧠 Memory Room Only");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
