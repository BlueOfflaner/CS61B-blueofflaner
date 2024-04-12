package deque;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import edu.princeton.cs.algs4.StdRandom;

/**
 * @author blueofflaner <blueofflaner@gmail.com>
 * Created on 2024-04-12
 */
public class ArrayDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        ArrayDeque<String> lld1 = new ArrayDeque<String>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create ArrayDeques with different parameterized types*/
    public void multipleParamTest() {

        ArrayDeque<String>  lld1 = new ArrayDeque<String>();
        ArrayDeque<Double>  lld2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());

    }

    @Test
    public void resizeDequeTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 32; i++) {
            lld1.addLast(i);
        }
        int x = 1;
        for (double i = 0; i < 16; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }
        int y = 1;
        for (double i = 31; i > 16; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }

    }

    @Test
    public void addTest() {
        ArrayDeque<String> arrayDeque = new ArrayDeque<>();

        assertTrue("Should be empty", arrayDeque.isEmpty());

        arrayDeque.addFirst("front");
        assertEquals("Should have size 1", 1, arrayDeque.size());

        arrayDeque.addLast("middle");
        assertEquals("Should have size 2", 2, arrayDeque.size());

        arrayDeque.addLast("back");
        assertEquals("Should have size 3", 3, arrayDeque.size());

        System.out.println("Printing out deque: ");
        arrayDeque.printDeque();
    }

    @Test
    public void addWithResizingTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        for (int i = 0; i < 20; i++) {
            arrayDeque.addLast(i);
        }

        assertEquals("Should have size 20", 20, arrayDeque.size());
    }

    @Test
    public void addBigAmountTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        int M = 1000000;

        for (int i = 0; i < M; i++) {
            arrayDeque.addLast(i);
        }

        assertEquals("Should have size 1000000", M, arrayDeque.size());
    }

    @Test
    public void removeTest() {
        ArrayDeque<String> arrayDeque = new ArrayDeque<>();

        arrayDeque.addFirst("front");
        arrayDeque.addLast("middle");
        arrayDeque.addLast("back");

        assertEquals("Should remove last item", "back", arrayDeque.removeLast());
        assertEquals("Should remove first item", "front", arrayDeque.removeFirst());

        assertEquals("Should have size 1", 1, arrayDeque.size());
    }

    @Test
    public void removeWithResizingTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        for (int i = 0; i < 20; i++) {
            arrayDeque.addLast(i);
        }

        for (int i = 0; i < 20; i++) {
            assertEquals("Should be equal", i, (int) arrayDeque.removeFirst());
        }

        assertTrue("Should be empty", arrayDeque.isEmpty());

        for (int i = 0; i < 20; i++) {
            arrayDeque.addLast(i);
        }

        assertEquals("Should have size 20", 20, arrayDeque.size());
    }

    @Test
    public void removeBigAmountTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        int M = 1000000;

        for (int i = 0; i < M; i++) {
            arrayDeque.addLast(i);
        }

        assertEquals("Should have size 1000000", M, arrayDeque.size());

        for (int i = 0; i < M; i++) {
            assertEquals("Should be equal", i, (int) arrayDeque.removeFirst());
        }

        assertTrue("Should be empty", arrayDeque.isEmpty());
    }

    @Test
    public void getTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        for (int i = 0; i < 20; i++) {
            arrayDeque.addLast(i);
        }

        for (int i = 0; i < 20; i++) {
            assertEquals("Should be equal", i, (int) arrayDeque.get(i));
        }

        assertNull("Should be null when index out of bound", arrayDeque.get(20));
    }

    @Test
    public void getBigAmountTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        int M = 1000000;

        for (int i = 0; i < M; i++) {
            arrayDeque.addLast(i);
        }

        for (int i = 0; i < M; i++) {
            assertEquals("Should be equal", i, (int) arrayDeque.get(i));
        }
    }

    @Test
    public void equalsTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ArrayDeque<Integer> ad2 = new ArrayDeque<>();

        ad1.addLast(0);
        ad2.addLast(0);
        assertEquals(ad1, ad2);

        ad1.addLast(1);
        assertNotEquals(ad1, ad2);

        ad2.addLast(2);
        assertNotEquals(ad1, ad2);
    }

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        LinkedListDeque<Integer> linkedListDeque = new LinkedListDeque<>();

        int N = 1000000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 6);
            // System.out.print(operationNumber + "  ");
            if (operationNumber == 0) {
                int randVal = StdRandom.uniform(0, 100);
                arrayDeque.addFirst(randVal);
                // linkedListDeque.addFirst(randVal);
                // System.out.print(randVal);
                // assertEquals(linkedListDeque, arrayDeque);
            } else if (operationNumber == 1) {
                int randVal = StdRandom.uniform(0, 100);
                arrayDeque.addLast(randVal);
                // linkedListDeque.addLast(randVal);
                // System.out.print(randVal);
                // assertEquals(linkedListDeque, arrayDeque);
            } else if (arrayDeque.size() == 0) {
                assertTrue(arrayDeque.isEmpty());
            } else if (operationNumber == 2) {
                assertTrue(arrayDeque.size() > 0);
            } else if (operationNumber == 3) {
                arrayDeque.removeFirst();
                // linkedListDeque.removeFirst();
                // assertEquals(linkedListDeque, arrayDeque);
            } else if (operationNumber == 4) {
                arrayDeque.removeLast();
                // linkedListDeque.removeLast();
                // assertEquals(linkedListDeque, arrayDeque);
            } else if (operationNumber == 5) {
                int randIndex = StdRandom.uniform(0, arrayDeque.size());
                arrayDeque.get(randIndex);
                // System.out.print(randIndex);
                // assertEquals(linkedListDeque.get(randIndex), arrayDeque.get(randIndex));
            }
            // System.out.println();
        }
    }


    @Test
    public void nullTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        arrayDeque.addFirst(66);
        arrayDeque.addLast(98);
        System.out.println(arrayDeque);

        LinkedListDeque<Integer> linkedListDeque = new LinkedListDeque<>();
        linkedListDeque.addFirst(66);
        linkedListDeque.addLast(98);
        System.out.println(linkedListDeque.get(1));
    }
}
