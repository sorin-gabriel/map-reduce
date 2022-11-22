import java.nio.file.Paths;

public class ReduceResult implements Comparable<ReduceResult> {
    private final String filePath;
    private final double rank;
    private final int length;
    private final int number;

    public ReduceResult(String filePath, double rank, int length, int number) {
        this.filePath = filePath;
        this.rank = rank;
        this.length = length;
        this.number = number;
    }

    @Override
    public int compareTo(ReduceResult o) {
        return Double.compare(o.rank, rank);
    }

    @Override
    public String toString() {
        return Paths.get(filePath).getFileName() + "," + String.format("%.2f", rank) + "," + length + "," + number;
    }
}
