package rooms;

import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ResultRoom {

    private Stage stage;

    public ResultRoom(Stage stage) {
        this.stage = stage;
    }

    public void showResults() {
        ScoreManager sm = ScoreManager.getInstance();

        VBox memoryBox = createPieChart("Memory Room", sm.getTotalScore("MemoryRoom"), 8, Color.CORNFLOWERBLUE);
        VBox logicBox = createPieChart("Logic Room", sm.getTotalScore("LogicRoom"), 10, Color.MEDIUMSEAGREEN);
        VBox typingBox = createPieChart("Typing Room", sm.getTotalScore("TypingRoom"), 15, Color.ORANGERED);
        VBox reflexBox = createPieChart("Reflex Room", sm.getTotalScore("ReflexRoom"), 5, Color.GOLD);
        VBox mazeBox = createPieChart("Maze Room", sm.getTotalScore("MazeRoom"), 20, Color.MEDIUMPURPLE);

        // Grid layout: 2 per row
        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: #f0f8ff;");

        grid.add(memoryBox, 0, 0);
        grid.add(logicBox, 1, 0);
        grid.add(typingBox, 0, 1);
        grid.add(reflexBox, 1, 1);
        grid.add(mazeBox, 0, 2);

        Scene scene = new Scene(grid, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("📊 Game Results");
        stage.show();
    }

    private VBox createPieChart(String roomName, int score, int maxScore, Color color) {
        String category = getCategory(roomName, score);
        PieChart.Data achieved = new PieChart.Data("Score", score);
        PieChart.Data remaining = new PieChart.Data("Remaining", Math.max(0, maxScore - score));

        PieChart pieChart = new PieChart();
        pieChart.getData().addAll(achieved, remaining);
        pieChart.setTitle(roomName + " (" + category + ")");
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(false);
        pieChart.setPrefSize(300, 300);

        // Apply colors
        achieved.getNode().setStyle("-fx-pie-color: " + toRgbString(color) + ";");
        remaining.getNode().setStyle("-fx-pie-color: #d3d3d3;"); // light gray for remaining

        Label suggestion = new Label(getSuggestion(category));
        suggestion.setStyle("-fx-font-size: 16px; -fx-text-fill: darkblue; -fx-font-weight: bold;");

        VBox box = new VBox(10, pieChart, suggestion);
        box.setStyle("-fx-alignment: center;");
        return box;
    }

    private String toRgbString(Color c) {
        return String.format("rgb(%d, %d, %d)",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255));
    }

    private String getCategory(String roomName, int score) {
        switch (roomName) {
            case "Memory Room":
                if (score >= 8) return "Excellent";
                if (score >= 6) return "Good";
                if (score >= 4) return "Neutral";
                if (score >= 2) return "Bad";
                return "Poor";
            case "Logic Room":
                if (score >= 10) return "Excellent";
                if (score >= 8) return "Good";
                if (score >= 6) return "Neutral";
                if (score >= 4) return "Bad";
                return "Poor";
            case "Typing Room":
                if (score >= 10) return "Excellent";
                if (score >= 8) return "Good";
                if (score >= 6) return "Neutral";
                if (score >= 4) return "Bad";
                return "Poor";
            case "Reflex Room":
                if (score >= 5) return "Excellent";
                if (score >= 4) return "Good";
                if (score >= 3) return "Neutral";
                if (score >= 2) return "Bad";
                return "Poor";
            case "Maze Room":
                if (score >= 12) return "Excellent";
                if (score >= 9) return "Good";
                if (score >= 7) return "Neutral";
                if (score >= 5) return "Bad";
                return "Poor";
            default:
                return "Neutral";
        }
    }

    private String getSuggestion(String category) {
        switch(category) {
            case "Excellent": return "🎉 Outstanding! Keep it up.";
            case "Good": return "👍 Good job! You can still improve.";
            case "Neutral": return "🙂 Average. Practice more.";
            case "Bad": return "⚠ You need more practice.";
            case "Poor": return "❌ Consider trying again.";
            default: return "";
        }
    }
}
