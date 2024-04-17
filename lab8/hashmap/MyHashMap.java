package hashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private double LOAD_FACTOR = 0.75;
    private int size;
    private HashSet<K> keySet = new HashSet<>();

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(DEFAULT_INITIAL_CAPACITY);
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.LOAD_FACTOR = maxLoad;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    @SuppressWarnings("unchecked")
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    private int hash(K key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    // Your code won't compile until you do so!

    private int getKeyPos(K key) {
        return (buckets.length - 1) & hash(key);
    }

    private void resize() {
        resize(buckets.length << 1);
    }

    private void resize(int newCapacity) {
        int oldCapacity = buckets.length;
        Collection<Node>[] nBuckets = createTable(newCapacity);
        for (int i = 0; i < oldCapacity; i++) {
            Collection<Node> lowBucket = createBucket();
            Collection<Node> highBucket = createBucket();
            for (Node node : buckets[i]) {
                if ((hash(node.key) & oldCapacity) == 0) {
                    lowBucket.add(node);
                } else {
                    highBucket.add(node);
                }
            }
            nBuckets[i].addAll(lowBucket);
            nBuckets[i + oldCapacity].addAll(highBucket);
        }
        buckets = nBuckets;
    }

    public Node getNode(K key) {
        int pos = getKeyPos(key);
        for (Node node : buckets[pos]) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        buckets = createTable(DEFAULT_INITIAL_CAPACITY);
        keySet.clear();
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        Node node = getNode(key);
        return node == null ? null : node.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int pos = getKeyPos(key);
        for (Node node : buckets[pos]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        buckets[pos].add(createNode(key, value));
        keySet.add(key);
        size++;
        if (size >= buckets.length * LOAD_FACTOR) {
            resize();
        }
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public V remove(K key) {
        Node node = getNode(key);
        if (node == null) {
            return null;
        }
        buckets[getKeyPos(key)].remove(node);
        keySet.remove(key);
        size--;
        return node.value;
    }

    @Override
    public V remove(K key, V value) {
        return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    private class MyHashMapIterator implements Iterator<K> {
        private int cnt = 0;
        private LinkedList<Node> nodes;

        private void init() {
            for (int i = 0; i < buckets.length; i++) {
                nodes.addAll(buckets[i]);
            }
        }

        public MyHashMapIterator() {
            init();
        }

        @Override
        public boolean hasNext() {
            return cnt < size;
        }

        @Override
        public K next() {
            if (hasNext()) {
                cnt++;
                return nodes.remove().key;
            }
            throw new NoSuchElementException();
        }
    }
}
