import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MapReduce {

    public static int fragSize;
    public static int docsNumber;
    public static String[] docsNames;

    public static ArrayList<Map> mapTasks = new ArrayList<>();
    public static HashMap<String, Reduce> reduceTasks = new HashMap<>();
    public static ArrayList<ReduceResult> results = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        if (args.length < 3) {
            System.err.println("Usage: MapReduce <workers> <in_file> <out_file>");
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(args[0]));

        Scanner s = new Scanner(new File(args[1]));
        fragSize = Integer.parseInt(s.nextLine());

        docsNumber = Integer.parseInt(s.nextLine());
        docsNames = new String[docsNumber];
        for (int i = 0; i < docsNumber; i++) {
            docsNames[i] = s.nextLine();
            File doc = new File(docsNames[i]);
            System.out.println(docsNames[i]);
            for (int j = 0; j < doc.length(); j += fragSize) {
                mapTasks.add(new Map(docsNames[i], j, (int) Math.min(fragSize, doc.length() - j)));
            }
        }

        List<Future<MapResult>> mapFutures = executor.invokeAll(mapTasks);

        for (Future<MapResult> f : mapFutures) {
            MapResult r = f.get();
            reduceTasks.putIfAbsent(r.getDocName(), new Reduce(r.getDocName()));
            reduceTasks.get(r.getDocName()).getMapResults().add(r);
        }

        List<Future<ReduceResult>> reduceFutures = executor.invokeAll(reduceTasks.values());

        for (Future<ReduceResult> f : reduceFutures) {
            ReduceResult r = f.get();
            results.add(r);
        }

        Collections.sort(results);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(args[2]));
        bufferedWriter.write("Name, Rank, Max length, Max number");
        bufferedWriter.newLine();
        for (ReduceResult r : results) {
            bufferedWriter.write(r.toString());
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

        executor.shutdown();
    }
}
