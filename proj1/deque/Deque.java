package deque;

/**
 * @author blueofflaner <blueofflaner@gmail.com>
 * Created on 2024-04-09
 */
public interface Deque<T> {
    void addFirst(T item);

    void addLast(T item);

    default boolean isEmpty() {
        return size() == 0;
    }

    int size();

    void printDeque();

    T removeFirst();

    T removeLast();

    T get(int index);

    boolean equals(Object o);
}
