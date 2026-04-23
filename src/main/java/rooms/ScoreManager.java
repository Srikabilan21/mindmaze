package rooms;

import java.util.HashMap;

public class ScoreManager {
    private static ScoreManager instance = null;
    private HashMap<String, Integer> scores = new HashMap<>();

    private ScoreManager() {}

    public static ScoreManager getInstance() {
        if (instance == null) instance = new ScoreManager();
        return instance;
    }

    public void setScore(String roomName, int score) {
        scores.put(roomName, score);
    }

    public int getTotalScore(String roomName) {
        return scores.getOrDefault(roomName, 0);
    }
}
