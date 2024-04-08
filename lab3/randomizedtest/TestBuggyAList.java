package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testRemoveLast() {
        AListNoResizing<Integer> aList = new AListNoResizing<>();
        BuggyAList<Integer> bList = new BuggyAList<>();
        for (int i = 0; i < 10; i++) {
            aList.addLast(i);
            bList.addLast(i);
        }
        assertEquals(aList.size(), bList.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(aList.removeLast(), bList.removeLast());
        }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> bL = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                bL.addLast(randVal);
//                System.out.println("addLast(" + randVal + ")");
                assertEquals(L.size(), bL.size());
            } else {
                if (L.size() == 0) {
                    continue;
                }

                if (operationNumber == 1) {
//                    System.out.println("getLast(" + L.getLast() + ")");
                    assertEquals(L.getLast(), bL.getLast());
                } else {
                    assertEquals(L.removeLast(), bL.removeLast());
                    assertEquals(L.size(), bL.size());
//                    System.out.println("removeLast()");
                }
            }
        }
    }
}
