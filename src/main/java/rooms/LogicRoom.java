package rooms;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class LogicRoom {

    private Stage stage;
    private GameFlow gameFlow;

    private Label questionLabel;
    private Label resultLabel;
    private Label timerLabel;

    private Button trueButton;
    private Button falseButton;

    private int currentQuestionIndex = 0;
    private boolean currentAnswer;
    private Timeline timer;
    private int timeLeft;

    private final int TOTAL_QUESTIONS = 10; // adjust if needed
    private final int TIME_LIMIT = 10; // seconds per question

    // Stores score for each question (1 for correct, 0 for wrong/skipped)
    private List<Integer> questionScores = new ArrayList<>();

    // Master question bank
    private final String[][] logicQuestions = {
            {"The human body has 3 lungs", "false"},
            {"Photosynthesis happens in plant leaves", "true"},
            {"Albert Einstein developed the theory of relativity", "true"},
            {"The sun rises in the west", "false"},
            {"The Pythagorean theorem applies to right triangles", "true"},
            {"There are 24 hours in a day", "true"},
            {"Diamonds are made of carbon", "true"},
            {"The Earth is flat", "false"},
            {"Blood is blue inside the body", "false"},
            {"The Great Pyramid of Giza is in Egypt", "true"},
            {"Newton discovered gravity", "true"},
            {"Fire is a state of matter", "false"},
            {"Lightning is hotter than the surface of the sun", "true"},
            {"The Earth has two moons", "false"},
            {"The capital of Japan is Tokyo", "true"},
            {"Polar bears live in Antarctica", "false"},
            {"The Milky Way is a galaxy", "true"},
            {"An ostrich can fly", "false"},
            {"Bananas grow on trees", "false"},
            {"A hexagon has 6 sides", "true"},
            {"Water is composed of hydrogen and oxygen", "true"},
            {"The Sahara is in South America", "false"},
            {"Iron is a metal", "true"},
            {"Mercury is the hottest planet", "false"},
            {"Shakespeare wrote 'Romeo and Juliet'", "true"},
            {"Earth is the third planet from the Sun", "true"},
            {"Bees make honey", "true"},
            {"The speed of light is slower than the speed of sound", "false"},
            {"The human skeleton supports the body", "true"},
            {"Fish breathe using lungs", "false"},
            {"Chocolate comes from cocoa beans", "true"},
            {"The moon orbits the Earth", "true"},
            {"2 + 2 = 5", "false"},
            {"The Nile is in Africa", "true"},
            {"The largest mammal is the blue whale", "true"},
            {"The Earth’s atmosphere is mostly oxygen", "false"},
            {"The currency of the USA is the dollar", "true"},
            {"Snakes have legs", "false"},
            {"The heart pumps blood", "true"},
            {"Caterpillars turn into butterflies", "true"},
            {"Venus is the brightest planet in the night sky", "true"},
            {"All deserts are hot", "false"},
            {"The Earth spins on its axis", "true"},
            {"Humans can live on Mars without equipment", "false"},
            {"The internet was invented in the 1960s", "true"},
            {"The brain controls the body", "true"},
            {"(5*3)+3=18", "true"},
            {"(12/3)+4=8", "true"},
            {"(7*2)-5=9", "true"},
            {"(9+6)/3=5", "true"},
            {"(8*2)+1=17", "true"},
            {"(15-6)*2=20", "false"},
            {"(3*3)+7=16", "true"},
            {"(20/5)+9=13", "true"},
            {"(18-4)/2=7", "true"},
            {"(6*4)-10=14", "true"},
            {"(10+5)*2=30", "true"},
            {"(25/5)+2=9", "false"},
            {"(9*3)-8=19", "true"},
            {"(8+12)/4=5", "true"},
            {"(14*2)-5=23", "true"},
            {"(6*6)+2-1=37", "true"},
            {"(21/7)+0+9=12", "true"},
            {"(10*3)-7=25", "false"},
            {"(18/2)+6=15", "true"},
            {"(20-8*2)*2=34", "false"},
            {"(11*4/2)+6=27", "false"},
            {"(15+5)/4=5", "true"},
            {"(9*2)-11=7", "true"},
            {"(30/5)+8=14", "true"},
            {"(12*3)-10=26", "true"},
            {"(40/4*2)+4=14", "false"},
            {"(7*5)-8=28", "false"},
            {"(9+11)/2=10", "true"},
            {"(50/10)+12=17", "true"},
            {"(16*2)-3=29", "true"},
            {"(6+4)*3=30", "true"},
            {"(45/9)-1=4", "true"},
            {"(12*2)+7=31", "true"},
            {"(18/3)+3=12", "false"},
            {"(25-9)+6=32", "false"},
            {"(5*5)+10=35", "true"},
            {"(40/5)-3=5", "true"},
            {"(30-6)/4=6", "true"},
            {"(8*7)-20=36", "true"},
            {"(100/20)+15=20", "true"},
            {"(14+6)/5=4", "true"},
            {"(22-7)*2=28", "false"},
            {"(9*9)-30=61", "false"},
            {"(12+18)/6=5", "true"},
            {"(60/10)+19=15", "false"},
            {"(11*3)-5+5=28", "false"},
            {"(7+13)/2=10", "true"},
            {"(50/5)+2=12", "true"},
            {"(8*8)-5=59", "true"},
            {"(90/9)+1=11", "true"}
    };

    private List<String[]> shuffledQuestions;

    public LogicRoom(Stage stage, GameFlow gameFlow) {
        this.stage = stage;
        this.gameFlow = gameFlow;
    }

    public Scene createScene() {
        questionLabel = new Label();
        questionLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        timerLabel = new Label("Time Left: " + TIME_LIMIT);
        timerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: red;");

        resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: darkblue;");

        trueButton = new Button("True ✅");
        falseButton = new Button("False ❌");

        trueButton.setOnAction(e -> checkAnswer(true));
        falseButton.setOnAction(e -> checkAnswer(false));

        VBox layout = new VBox(20, questionLabel, timerLabel, trueButton, falseButton, resultLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 40;");

        Scene scene = new Scene(layout, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        shuffledQuestions = new ArrayList<>(Arrays.asList(logicQuestions));
        Collections.shuffle(shuffledQuestions);

        loadNextQuestion();

        return scene;
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex >= TOTAL_QUESTIONS) {
            showFinalScore();
            return;
        }

        String[] q = shuffledQuestions.get(currentQuestionIndex);
        String questionText = q[0];
        currentAnswer = Boolean.parseBoolean(q[1]);

        questionLabel.setText("🧠 " + questionText);
        resultLabel.setText("");

        startTimer();
    }

    private void checkAnswer(boolean userAnswer) {
        if (timer != null) timer.stop();

        if (userAnswer == currentAnswer) {
            questionScores.add(1);
            resultLabel.setText("✅ Correct!");
            playSound("correct-98705.mp3");
        } else {
            questionScores.add(0);
            resultLabel.setText("❌ Wrong!");
            playSound("wrong-47985.mp3");
        }

        animateButton(userAnswer ? trueButton : falseButton);

        currentQuestionIndex++; // ✅ moved here

        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(this::loadNextQuestion);
        }).start();
    }

    private void startTimer() {
        timeLeft = TIME_LIMIT;
        timerLabel.setText("Time Left: " + timeLeft);

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerLabel.setText("Time Left: " + timeLeft);

            if (timeLeft <= 0) {
                timer.stop();
                resultLabel.setText("⏰ Time's up!");
                questionScores.add(0);

                currentQuestionIndex++; // ✅ also here

                new Thread(() -> {
                    try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                    javafx.application.Platform.runLater(this::loadNextQuestion);
                }).start();
            }
        }));

        timer.setCycleCount(TIME_LIMIT);
        timer.play();
    }

    private void showFinalScore() {
        int totalScore = questionScores.stream().mapToInt(Integer::intValue).sum();

        Label scoreLabel = new Label("🎉 Final Score: " + totalScore + " / " + TOTAL_QUESTIONS);
        scoreLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: green;");

        Button nextRoomBtn = new Button("➡ Next Room");
        nextRoomBtn.setOnAction(e -> gameFlow.loadNextRoom());

        VBox layout = new VBox(20, scoreLabel, nextRoomBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 40;");

        Scene finalScene = new Scene(layout, 600, 400);
        finalScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        ScoreManager.getInstance().setScore("LogicRoom", totalScore);

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
}
