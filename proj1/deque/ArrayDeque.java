package deque;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author blueofflaner <blueofflaner@gmail.com>
 * Created on 2024-04-09
 */
public class ArrayDeque<T> implements Deque<T> {

    private static final int DEFAULT_CAPACITY = 8;
    private final Object[] DEFAULT_CAPACITY_EMPTY_ELEMENTS = new Object[DEFAULT_CAPACITY];
    private static final double FACTOR = 0.25;
    private Object[] elements;
    private int size;
    private int head;
    private int tail;

    public ArrayDeque() {
        elements = DEFAULT_CAPACITY_EMPTY_ELEMENTS;
        size = 0;
        head = 0;
        tail = -1;
    }

    public ArrayDeque(int initialCapacity) {
        elements = new Object[initialCapacity];
        size = 0;
        head = 0;
        tail = -1;
    }

    @Override
    public void addFirst(T item) {
        if (size == elements.length) {
            resize(size << 1);
        }
        if (isEmpty()) {
            elements[head] = item;
            tail = 0;
            size++;
            return;
        }
        elements[(head + elements.length - 1) % elements.length] = item;
        head = (head + elements.length - 1) % elements.length;
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size == elements.length) {
            resize(size << 1);
        }
        elements[(tail + elements.length + 1) % elements.length] = item;
        tail = (tail + elements.length + 1) % elements.length;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        System.out.println(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T data = (T) elements[head];
        elements[head] = null;
        size--;
        head = (head + elements.length + 1) % elements.length;
        if (elements.length >= 16 && size < elements.length * FACTOR) {
            resize(elements.length >> 2);
        }
        if (isEmpty()) {
            head = 0;
            tail = -1;
        }
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T data = (T) elements[tail];
        elements[tail] = null;
        size--;
        tail = (tail + elements.length - 1) % elements.length;
        if (elements.length >= 16 && size < elements.length * FACTOR) {
            resize(elements.length >> 2);
        }
        if (isEmpty()) {
            head = 0;
            tail = -1;
        }
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= elements.length || isEmpty()) {
            return null;
        }
        return (T) elements[(index + head) % elements.length];
    }

    void resize(int newCapacity) {
        Object[] oldArray = elements;
        int oldCapacity = oldArray == null ? 0 : elements.length;
        Object[] newArray = new Object[newCapacity];
        if (head <= tail) {
            System.arraycopy(oldArray, head, newArray, 0, size);
        }
        else {
            int headSectionSize = oldArray.length - head;
            int tailSectionSize = size - headSectionSize;
            System.arraycopy(oldArray, head, newArray, 0, headSectionSize);
            System.arraycopy(oldArray, 0, newArray, headSectionSize, tailSectionSize);
        }
        elements = newArray;
        head = 0;
        tail = size - 1;
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator<>();
    }

    private class ArrayDequeIterator<T> implements Iterator<T> {
        int cur = head;

        @Override
        public boolean hasNext() {
            return cur != tail;
        }

        @Override
        public T next() {
            if (hasNext()) {
                cur = (cur + 1) % elements.length;
            }
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(get(i % elements.length)).append(" ");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        return toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, get(head), tail == -1 ? null : get(tail));
    }
}
