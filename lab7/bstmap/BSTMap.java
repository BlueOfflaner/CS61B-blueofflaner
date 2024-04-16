package bstmap;

import java.util.Iterator;
import java.util.Set;

/**
 * @author blueofflaner <blueofflaner@gmail.com>
 * Created on 2024-04-16
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private BSTNode root;

    private int size;

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return searchNode(root, key) != null;
    }

    @Override
    public V get(K key) {
        BSTNode res = searchNode(root, key);
        return res == null ? null : res.val;
    }

    private BSTNode searchNode(BSTNode cur, K key) {
        if (cur == null) {
            return null;
        }
        if (key.compareTo(cur.key) < 0) {
            return searchNode(cur.left, key);
        } else if (key.compareTo(cur.key) > 0) {
            return searchNode(cur.right, key);
        } else {
            return cur;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = addNode(root, key, value);
    }

    private BSTNode addNode(BSTNode cur, K key, V value) {
        if (cur == null) {
            size++;
            return new BSTNode(key, value);
        }
        if (key.compareTo(cur.key) >= 0) {
            cur.right = addNode(cur.right, key, value);
        } else {
            cur.left = addNode(cur.left, key, value);
        }
        return cur;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        printNode(root);
    }

    private void printNode(BSTNode cur) {
        if (cur == null) {
            return;
        }
        printNode(cur.left);
        System.out.print(cur.val + " ");
        printNode(cur.right);
    }

    private class BSTNode {
        K key;
        V val;
        BSTNode left;
        BSTNode right;

        BSTNode() { }
        BSTNode(K k, V v) {
            key = k;
            val = v;
        }
    }
}
