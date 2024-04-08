package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        int x = 1;
        while (x <= 128) {
            AList<Integer> testList = new AList<>();
            int targetSize = x * 1000;
            Stopwatch sw = new Stopwatch();
            for (int i = 0; i < targetSize; i++) {
                testList.addLast(1);
            }
            double timeInSecond = sw.elapsedTime();
            Ns.addLast(testList.size());
            times.addLast(timeInSecond);
            opCounts.addLast(targetSize);
            x <<= 1;
        }
        printTimingTable(Ns, times, opCounts);
    }
}
