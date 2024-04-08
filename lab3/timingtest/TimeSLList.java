package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
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
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        int x = 1;
        int M = 10000;
        while (x <= 128) {
            SLList<Integer> testList = new SLList<>();
            int targetSize = x * 1000;
            for (int i = 0; i < targetSize; i++) {
                testList.addLast(1);
            }

            Stopwatch sw = new Stopwatch();
            for (int i = 0; i < M; i++) {
                testList.getLast();
            }
            double timeInSecond = sw.elapsedTime();

            Ns.addLast(testList.size());
            times.addLast(timeInSecond);
            opCounts.addLast(M);
            x <<= 1;
        }
        printTimingTable(Ns, times, opCounts);
    }

}
