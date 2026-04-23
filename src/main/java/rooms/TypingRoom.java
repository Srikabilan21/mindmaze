package rooms;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TypingRoom extends Application {

    private Pane gamePane;
    private TextField inputField;
    private Label scoreLabel;
    private Label timerLabel;

    private final String[] words = {
            "JAVA", "MIND", "LOGIC", "BRAIN", "DATABASE",
            "SPEED", "FOCUS", "COMPUTING", "TYPE", "CODE",
            "DEBUG", "REACT", "DATA", "MEMORY", "SKILL"
    };

    private final List<Label> fallingWords = new ArrayList<>();
    private final Random random = new Random();
    private final List<Double> wordSpeeds = new ArrayList<>();

    private int score = 0;
    private int timeLeft = 30;
    private Timeline gameLoop;
    private Timeline spawnTimeline;
    private Timeline timer;

    private Stage stage;
    private GameFlow gameFlow;

    public TypingRoom(Stage stage, GameFlow gameFlow) {
        this.stage = stage;
        this.gameFlow = gameFlow;
    }

    public Scene createScene() {
        gamePane = new Pane();
        gamePane.setPrefSize(600, 400);
        gamePane.setStyle("-fx-background-color: black;");

        inputField = new TextField();
        inputField.setPromptText("Type word and press Enter...");
        inputField.setStyle("-fx-font-size: 16px;");
        inputField.setOnAction(e -> checkWord());

        scoreLabel = new Label("⭐ Score: 0");
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(Font.font(18));

        timerLabel = new Label("⏱ Time: " + timeLeft);
        timerLabel.setTextFill(Color.RED);
        timerLabel.setFont(Font.font(18));

        VBox topBar = new VBox(5, scoreLabel, timerLabel);
        topBar.setAlignment(Pos.TOP_CENTER);
        topBar.setLayoutX(230);
        topBar.setLayoutY(10);

        gamePane.getChildren().addAll(topBar, inputField);
        inputField.setLayoutX(150);
        inputField.setLayoutY(360);
        inputField.setPrefWidth(300);

        startGame();

        return new Scene(gamePane, 600, 400);
    }

    private void startGame() {
        // Game loop: update falling words positions
        gameLoop = new Timeline(new KeyFrame(Duration.millis(50), e -> updateWords()));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();

        // Spawn new words every 1 second
        spawnTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> spawnWord()));
        spawnTimeline.setCycleCount(Timeline.INDEFINITE);
        spawnTimeline.play();

        // Countdown timer
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerLabel.setText("⏱ Time: " + timeLeft);
            if (timeLeft <= 0) endGame();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateWords() {
        Iterator<Label> iterator = fallingWords.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Label word = iterator.next();
            double speed = wordSpeeds.get(index);
            word.setLayoutY(word.getLayoutY() + speed);

            if (word.getLayoutY() > 340) {
                gamePane.getChildren().remove(word);
                iterator.remove();
                wordSpeeds.remove(index);
            } else {
                index++;
            }
        }
    }

    private void spawnWord() {
        String randomWord = words[random.nextInt(words.length)];
        Label wordLabel = new Label(randomWord);
        wordLabel.setTextFill(Color.LIGHTGREEN);
        wordLabel.setFont(Font.font(20));
        wordLabel.setLayoutX(random.nextInt(550));
        wordLabel.setLayoutY(0);

        fallingWords.add(wordLabel);
        wordSpeeds.add(1.5 + random.nextDouble() * 2.0); // Random speed 1.5-3.5
        gamePane.getChildren().add(wordLabel);
    }

    private void checkWord() {
        String typed = inputField.getText().trim().toUpperCase();
        inputField.clear();

        Iterator<Label> iterator = fallingWords.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Label word = iterator.next();
            if (word.getText().equals(typed)) {
                score++;
                scoreLabel.setText("⭐ Score: " + score);
                gamePane.getChildren().remove(word);
                iterator.remove();
                wordSpeeds.remove(index);
                break;
            } else {
                index++;
            }
        }
    }

    private void endGame() {
        gameLoop.stop();
        spawnTimeline.stop();
        timer.stop();
        inputField.setDisable(true);

        // Save score
        ScoreManager.getInstance().setScore("TypingRoom", score);

        Label gameOver = new Label("🏁 Game Over!\nFinal Score: " + score);
        gameOver.setTextFill(Color.BROWN);
        gameOver.setFont(Font.font(28));

        Button actionButton;
        if (gameFlow == null) {
            actionButton = new Button("🔙 Back to Menu");
            actionButton.setOnAction(e -> {
                GameManager gm = new GameManager();
                stage.setScene(gm.createMainMenu(stage));
            });
        } else {
            actionButton = new Button("➡ Next Room");
            actionButton.setOnAction(e -> gameFlow.loadNextRoom());
        }

        VBox layout = new VBox(20, gameOver, actionButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 40;");

        Scene finalScene = new Scene(layout, 600, 400);
        stage.setScene(finalScene);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = createScene();
        stage.setScene(scene);
        stage.setTitle("☔ Typing Room Only");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
