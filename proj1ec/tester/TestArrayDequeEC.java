package tester;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import static org.junit.Assert.assertEquals;

/**
 * @author blueofflaner <blueofflaner@gmail.com>
 * Created on 2024-04-16
 */
public class TestArrayDequeEC {

    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> ans = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> std = new ArrayDequeSolution<>();

        String message = "";
        int size = StdRandom.uniform(5, 100);
        for (int i = 0; i < size; i++) {
            int opt = StdRandom.uniform(2);
            int x = StdRandom.uniform(100);
            if (opt == 0) {
                ans.addFirst(x);
                std.addFirst(x);
                message += "addFirst(" + x + ")\n";
            } else {
                ans.addLast(x);
                std.addLast(x);
                message += "addLast(" + x + ")\n";
            }
        }

        Integer expect;
        Integer actual;
        for (int i = 0; i < size; i++) {
            int opt = StdRandom.uniform(2);
            if (opt == 0) {
                actual = ans.removeFirst();
                expect = std.removeFirst();
                message += "removeFirst()\n";
            } else {
                actual = ans.removeLast();
                expect = std.removeLast();
                message += "removeLast()\n";
            }
            assertEquals(message, expect, actual);
        }
    }
}
