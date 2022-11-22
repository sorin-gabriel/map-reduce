import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Reduce implements Callable<ReduceResult> {
    private final String filePath;
    private final ArrayList<MapResult> mapResults;
    private double rank;
    private int maxLength;
    private int maxNumber;

    public Reduce(String filePath) {
        this.filePath = filePath;
        this.mapResults = new ArrayList<>();
    }

    public ArrayList<MapResult> getMapResults() {
        return mapResults;
    }

    private int fib(int n) {
        if (n == 0) return 0;
        else if (n == 1) return 1;
        else {
            int fib_0 = 0;
            int fib_1 = 1;
            int fib_n;
            do {
                fib_n = fib_1 + fib_0;
                fib_0 = fib_1;
                fib_1 = fib_n;
            } while (n-- > 2);
            return fib_n;
        }
    }

    @Override
    public ReduceResult call() {
        int wordsCount = 0;
        for (MapResult r : mapResults) {
            for (Integer wordLength : r.getWordLengthDistribution().keySet()) {
                rank += fib(wordLength+1) * r.getWordLengthDistribution().get(wordLength);
                wordsCount += r.getWordLengthDistribution().get(wordLength);
            }

            if (r.getMaximumLengthWords().size() > 0) {
                String s = r.getMaximumLengthWords().get(0);
                if (s.length() > maxLength) {
                    maxLength = s.length();
                    maxNumber = r.getMaximumLengthWords().size();
                } else if (s.length() == maxLength) {
                    maxNumber += r.getMaximumLengthWords().size();
                }
            }
        }
        rank /= wordsCount;

        return new ReduceResult(filePath, rank, maxLength, maxNumber);
    }
}
