import java.util.ArrayList;
import java.util.HashMap;

public class MapResult {
    private final String filePath;
    private final HashMap<Integer, Integer> wordLengthDistribution;
    private final ArrayList<String> maximumLengthWords;

    public MapResult(String filePath) {
        this.filePath = filePath;
        this.wordLengthDistribution = new HashMap<>();
        this.maximumLengthWords = new ArrayList<>();
    }

    public String getDocName() {
        return filePath;
    }

    public HashMap<Integer, Integer> getWordLengthDistribution() {
        return wordLengthDistribution;
    }

    public ArrayList<String> getMaximumLengthWords() {
        return maximumLengthWords;
    }
}
