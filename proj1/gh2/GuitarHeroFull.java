package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * @author blueofflaner <blueofflaner@gmail.com>
 * Created on 2024-04-12
 */
public class GuitarHeroFull {

    private static final int SIZE = 37;
    public static final double[] CONCERT = new double[SIZE];
    private static final GuitarString[] GUITAR_STRINGS = new GuitarString[SIZE];
    static {
        for (int i = 0; i < SIZE; i++) {
            CONCERT[i] = 440.0 * Math.pow(2, (i - 24.0) / 12.0);
            GUITAR_STRINGS[i] = new GuitarString(CONCERT[i]);
        }
    }
    private static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static void main(String[] args) {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);
                if (index == -1) {
                    continue;
                }
                GUITAR_STRINGS[index].pluck();
            }

            double sample = 0;
            for (int i = 0; i < SIZE; i++) {
                sample += GUITAR_STRINGS[i].sample();
            }

            StdAudio.play(sample);

            for (int i = 0; i < SIZE; i++) {
                GUITAR_STRINGS[i].tic();
            }
        }
    }

}
