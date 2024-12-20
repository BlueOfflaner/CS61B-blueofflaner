package deque;

import java.util.Iterator;

/**
 * @author blueofflaner <blueofflaner@gmail.com>
 * Created on 2024-04-09
 */
@SuppressWarnings("unchecked")
public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    public LinkedListDeque() {
        head = new Node<T>();
        tail = new Node<T>();
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node<T> node = new Node<>(item);
        node.next = head.next;
        node.next.prev = node;
        head.next = node;
        node.prev = head;
        size++;
    }

    @Override
    public void addLast(T item) {
        Node<T> node = new Node<>(item);
        node.prev = tail.prev;
        node.prev.next = node;
        tail.prev = node;
        node.next = tail;
        size++;
    }

    @Override
    public T removeFirst() {
        if (head.next != tail) {
            Node<T> node = head.next;
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
            return node.data;
        }
        return null;
    }

    @Override
    public T removeLast() {
        if (tail.prev != head) {
            Node<T> node = tail.prev;
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
            return node.data;
        }
        return null;
    }

    @Override
    public T get(int index) {
        Node<T> cur = head.next;
        int cnt = 0;
        while (cur != tail) {
            if (cnt == index) {
                return cur.data;
            }
            cur = cur.next;
            cnt++;
        }
        return null;
    }

    public T getRecursive(int index) {
        return (T) recursiveTraversal(head.next, 0, index);
    }

    private T recursiveTraversal(Node<T> cur, int count, int target) {
        if (cur == tail) {
            return null;
        }
        if (count == target) {
            return cur.data;
        }
        return (T) recursiveTraversal(cur.next, count + 1, target);
    }

    @Override
    public void printDeque() {
        System.out.println(this);
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator<>();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> deque = (Deque<T>) o;
        if (size() != deque.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!deque.get(i).equals(get(i))) {
                return false;
            }
        }
        return true;
    }

    private Node head;
    private Node tail;
    private int size;
    private class Node<T> {
        T data;
        Node prev;
        Node next;

        Node() { }
        Node(T data) {
            this.data = data;
        }
    }

    private class LinkedListDequeIterator<T> implements Iterator<T> {
        Node<T> cur = head;
        @Override
        public boolean hasNext() {
            return cur.next != tail;
        }

        @Override
        public T next() {
            if (hasNext()) {
                cur = cur.next;
                return cur.data;
            }
            return null;
        }
    }
}
