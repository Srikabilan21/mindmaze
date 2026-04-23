package rooms;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class ReflexRoom extends Application {

    private Label instructionLabel;
    private Label timerLabel;
    private Label resultLabel;
    private Button clickButton;
    private int score = 0;
    private int round = 1;

    private final int MAX_ROUNDS = 5;
    private long startTime;
    private boolean waitingForClick = false;

    private double totalReactionTime = 0;

    private Stage stage;
    private GameFlow gameFlow;

    // ✅ Constructor for GameFlow
    public ReflexRoom(Stage stage, GameFlow gameFlow) {
        this.stage = stage;
        this.gameFlow = gameFlow;
    }

    // ✅ Scene creation
    public Scene createScene() {
        instructionLabel = new Label("⚡ Wait for the signal...");
        instructionLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: darkblue;");

        timerLabel = new Label("⏱ Round " + round + "/" + MAX_ROUNDS);
        timerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: red;");

        resultLabel = new Label();
        resultLabel.setStyle("-fx-font-size: 16px;");

        clickButton = new Button("Click when ready!");
        clickButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        clickButton.setDisable(true);
        clickButton.setOnAction(e -> handleClick());

        VBox layout = new VBox(20, instructionLabel, timerLabel, clickButton, resultLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 40;");

        Scene scene = new Scene(layout, 600, 400);

        startReflexRound();

        return scene;
    }

    private void startReflexRound() {
        instructionLabel.setText("⚡ Wait for GREEN button...");
        resultLabel.setText("");
        clickButton.setDisable(true);
        clickButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");
        waitingForClick = false;

        Random random = new Random();
        int delay = 1000 + random.nextInt(4000);

        Timeline delayTimeline = new Timeline(new KeyFrame(Duration.millis(delay), e -> {
            instructionLabel.setText("✅ CLICK NOW!");
            clickButton.setDisable(false);
            clickButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-color: lightgreen;");
            waitingForClick = true;
            startTime = System.currentTimeMillis();
        }));
        delayTimeline.setCycleCount(1);
        delayTimeline.play();
    }

    private void handleClick() {
        if (!waitingForClick) {
            resultLabel.setText("⛔ Too early! - No points");
            playSound("wrong-47985.mp3");
        } else {
            long reactionTime = System.currentTimeMillis() - startTime;
            totalReactionTime += reactionTime;
            resultLabel.setText("⚡ Reaction Time: " + reactionTime + " ms");

            if (reactionTime < 500) {
                score++;
                playSound("correct-98705.mp3");
            } else {
                playSound("wrong-47985.mp3");
            }
        }

        waitingForClick = false;
        clickButton.setDisable(true);
        clickButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px;");

        if (round >= MAX_ROUNDS) {
            showFinalScore();
        } else {
            round++;
            timerLabel.setText("⏱ Round " + round + "/" + MAX_ROUNDS);
            Timeline nextRoundDelay = new Timeline(new KeyFrame(Duration.seconds(2), e -> startReflexRound()));
            nextRoundDelay.setCycleCount(1);
            nextRoundDelay.play();
        }
    }

    // ✅ Save score to ScoreManager
    private void showFinalScore() {
        double avgReaction = totalReactionTime / MAX_ROUNDS;

        Label scoreLabel = new Label("🏁 Final Reflex Score: " + score + " / " + MAX_ROUNDS);
        scoreLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: green;");

        Label avgLabel = new Label(String.format("📊 Average Reaction Time: %.2f ms", avgReaction));
        avgLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: blue;");

        // ✅ Save to ScoreManager
        ScoreManager.getInstance().setScore("ReflexRoom", score);

        Button nextRoomBtn = new Button("➡ Next Room");
        nextRoomBtn.setOnAction(e -> {
            if (gameFlow != null) {
                gameFlow.loadNextRoom();
            }
        });

        VBox layout = new VBox(20, scoreLabel, avgLabel, nextRoomBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 40;");

        Scene finalScene = new Scene(layout, 600, 400);
        finalScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(finalScene);
    }

    private void playSound(String filename) {
        try {
            String path = getClass().getResource("/sounds/" + filename).toExternalForm();
            Media sound = new Media(path);
            MediaPlayer player = new MediaPlayer(sound);
            player.play();
        } catch (Exception e) {
            System.out.println("Sound error: " + e.getMessage());
        }
    }

    private void animateButton(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
        st.setToX(1.1);
        st.setToY(1.1);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage; // standalone run
        Scene scene = createScene();
        stage.setScene(scene);
        stage.setTitle("⚡ Reflex Room Only");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
