import java.io.RandomAccessFile;
import java.util.concurrent.Callable;

public class Map implements Callable<MapResult> {
    private final String filePath;
    private final int docOffset;
    private final int fragSize;
    private final MapResult result;

    public Map(String filePath, int docOffset, int fragSize) {
        this.filePath = filePath;
        this.docOffset = docOffset;
        this.fragSize = fragSize;
        this.result = new MapResult(filePath);
    }

    @Override
    public MapResult call() {
        try (RandomAccessFile f = new RandomAccessFile(filePath, "r")) {

            f.seek(Math.max(0, docOffset-1));

            boolean discardFirstWord = false;
            int c = f.read();
            if (docOffset > 0 && Character.isLetterOrDigit(c)) {
                discardFirstWord = true;
            }
            f.seek(docOffset);

            int bytesRead = 0;
            while (discardFirstWord) {
                bytesRead++;
                c = f.read();
                if (!Character.isLetterOrDigit(c)) {
                    discardFirstWord = false;
                }
            }

            int currentWordLength = 0;
            int maximumWordLength = 0;
            StringBuilder currentWord = new StringBuilder();
            boolean continueLastWord = false;
            do {
                c = f.read();
                bytesRead++;

                if (Character.isLetterOrDigit(c)) {
                    currentWord.append((char)c);
                    currentWordLength++;
                    if (bytesRead == fragSize) continueLastWord = true;
                } else if (currentWordLength > 0) {
                    if (currentWordLength > maximumWordLength) {
                        result.getMaximumLengthWords().clear();
                        result.getMaximumLengthWords().add(currentWord.toString());
                        maximumWordLength = currentWordLength;
                    } else if (currentWordLength == maximumWordLength) result.getMaximumLengthWords().add(currentWord.toString());

                    result.getWordLengthDistribution().putIfAbsent(currentWordLength, 0);
                    result.getWordLengthDistribution().put(currentWordLength, result.getWordLengthDistribution().get(currentWordLength)+1);

                    currentWordLength = 0;
                    currentWord.delete(0, currentWord.length());
                    continueLastWord = false;
                } else currentWordLength = 0;

            } while (bytesRead < fragSize || continueLastWord);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
