package rooms;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MazeRoom {

    private final int SIZE = 10;
    private final String WALL = "⬛";
    private final String PATH = "⬜";
    private final String PLAYER = "🟢";
    private final String GOAL = "🚩";

    private int playerX = 1;
    private int playerY = 1;
    private int level = 1;

    private GridPane gridPane;
    private Text timerText;
    private Timeline timer;
    private int timeLeft = 20; // default
    private String[][] mazeGrid;
    private int score = 0; // total score
    private int levelScore = 0;

    private Stage stage;
    private GameFlow gameFlow;

    private boolean isGameActive = true;

    public MazeRoom(Stage stage, GameFlow gameFlow) {
        this.stage = stage;
        this.gameFlow = gameFlow;
    }

    public Scene createScene() {
        if (level > 2) {
            showGameCompleted();
            return null;
        }

        loadMazeByLevel();

        gridPane = new GridPane();
        drawMaze();

        timerText = new Text("⏱ Time Left: " + timeLeft);
        timerText.setFont(Font.font(18));
        timerText.setFill(Color.RED);

        VBox root = new VBox(10, timerText, gridPane);
        root.setStyle("-fx-padding: 30;");
        root.setAlignment(javafx.geometry.Pos.CENTER);

        Scene scene = new Scene(root, 600, 500);

        gridPane.setFocusTraversable(true);
        scene.setOnKeyPressed(event -> {
            if (!isGameActive) return;
            if (event.getCode() == KeyCode.UP) move(-1, 0);
            else if (event.getCode() == KeyCode.DOWN) move(1, 0);
            else if (event.getCode() == KeyCode.LEFT) move(0, -1);
            else if (event.getCode() == KeyCode.RIGHT) move(0, 1);
        });

        Platform.runLater(() -> gridPane.requestFocus());
        startTimer();

        return scene;
    }

    private void startTimer() {
        if (timer != null) timer.stop();

        // Set timer for each level
        if (level == 1) timeLeft = 20; // Level 1 = 20s
        else timeLeft = Math.max(20, 30 - (level * 5)); // Level 2 formula

        timerText.setText("⏱ Time Left: " + timeLeft);

        isGameActive = true;

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerText.setText("⏱ Time Left: " + timeLeft);
            if (timeLeft <= 0) {
                timer.stop();
                isGameActive = false;
                showTimeUp();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void move(int dx, int dy) {
        if (!isGameActive) return;

        int newX = playerX + dx;
        int newY = playerY + dy;

        if (!isValidMove(newX, newY)) return;

        if (mazeGrid[newX][newY].equals(GOAL)) {
            mazeGrid[playerX][playerY] = PATH;
            playerX = newX;
            playerY = newY;
            mazeGrid[playerX][playerY] = PLAYER;
            drawMaze();
            timer.stop();
            isGameActive = false;

            levelScore = timeLeft / 2; // ✅ Score = timeLeft / 2
            score += levelScore;

            playSound("correct-98705.mp3");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Level Completed!");
            alert.setHeaderText(null);
            alert.setContentText("🎉 Level " + level + " completed!\nScore: " + levelScore);
            alert.showAndWait();

            level++;
            if (level > 2) {
                showGameCompleted();
            } else {
                Platform.runLater(() -> stage.setScene(createScene()));
            }
            return;
        }

        mazeGrid[playerX][playerY] = PATH;
        playerX = newX;
        playerY = newY;
        mazeGrid[playerX][playerY] = PLAYER;
        drawMaze();
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && y >= 0 && x < SIZE && y < SIZE && !mazeGrid[x][y].equals(WALL);
    }

    private void drawMaze() {
        gridPane.getChildren().clear();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Text cell = new Text(mazeGrid[i][j]);
                cell.setFont(Font.font(24));
                StackPane stack = new StackPane(cell);
                stack.setMinSize(40, 40);
                gridPane.add(stack, j, i);
            }
        }
    }

    private void loadMazeByLevel() {
        if (level == 1) {
            mazeGrid = new String[][]{
                    {"⬛","⬛","⬛","⬛","⬛","⬛","⬛","⬛","⬛","⬛"},
                    {"⬛","🟢","⬜","⬛","⬛","⬜","⬜","⬜","⬜","⬛"},
                    {"⬛","⬜","⬜","⬛","⬛","⬜","⬛","⬜","⬜","⬛"},
                    {"⬛","⬜","⬛","⬛","⬜","⬜","⬛","⬜","⬛","⬛"},
                    {"⬛","⬜","⬛","⬛","⬜","⬛","⬛","⬜","⬜","⬛"},
                    {"⬛","⬜","⬜","⬜","⬜","⬛","⬜","⬛","⬜","⬛"},
                    {"⬛","⬜","⬛","⬛","⬛","⬛","⬜","⬛","⬜","⬛"},
                    {"⬛","⬜","⬜","⬜","⬜","⬜","⬜","⬛","⬜","⬛"},
                    {"⬛","⬛","⬛","⬛","⬛","⬛","⬛","⬛","🚩","⬛"},
                    {"⬛","⬛","⬛","⬛","⬛","⬛","⬛","⬛","⬛","⬛"}
            };
        } else if (level == 2) {
            mazeGrid = new String[][]{
                    {"⬛","⬛","⬛","⬛","⬛","⬛","⬛","⬛","⬛","⬛"},
                    {"⬛","🟢","⬜","⬛","⬜","⬜","⬜","⬜","⬜","⬛"},
                    {"⬛","⬛","⬜","⬛","⬜","⬛","⬛","⬜","⬜","⬛"},
                    {"⬛","⬜","⬜","⬜","⬜","⬜","⬛","⬜","⬛","⬛"},
                    {"⬛","⬜","⬛","⬛","⬛","⬜","⬛","⬜","⬜","⬛"},
                    {"⬛","⬜","⬜","⬛","⬜","⬜","⬜","⬛","⬜","⬛"},
                    {"⬛","⬛","⬜","⬛","⬜","⬛","⬜","⬛","⬜","⬛"},
                    {"⬛","⬜","⬜","⬛","⬜","⬛","⬜","⬜","⬛","⬛"},
                    {"⬛","⬛","⬛","⬛","⬜","⬜","⬛","⬜","🚩","⬛"},
                    {"⬛","⬛","⬛","⬛","⬛","⬛","⬛","⬛","⬛","⬛"}
            };
        }
        playerX = 1;
        playerY = 1;
    }

    private void showTimeUp() {
        if (timer != null) timer.stop();
        isGameActive = false;

        levelScore = 0;
        score += levelScore;

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Time's Up!");
            alert.setHeaderText(null);
            alert.setContentText("⏰ You failed the maze.\nScore: " + levelScore);
            alert.showAndWait();

            // Restart the SAME level
            stage.setScene(createScene());
        });
    }

    private void showGameCompleted() {
        ScoreManager.getInstance().setScore("MazeRoom", score);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("All Levels Completed!");
        alert.setHeaderText(null);
        alert.setContentText("🎉 You've completed all levels of the Maze Room!\nTotal Score: " + score);
        alert.showAndWait();

        ResultRoom resultRoom = new ResultRoom(stage);
        resultRoom.showResults();
    }

    private void playSound(String filename) {
        try {
            String path = getClass().getResource("/sounds/" + filename).toExternalForm();
            Media media = new Media(path);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Failed to play sound: " + e.getMessage());
        }
    }
}
