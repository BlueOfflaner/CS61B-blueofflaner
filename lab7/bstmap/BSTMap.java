package bstmap;

import java.util.Iterator;
import java.util.Set;

/**
 * @author blueofflaner <blueofflaner@gmail.com>
 * Created on 2024-04-16
 */
public class BSTMap<K extends Comparable, V> implements Map61B {

    BSTNode root;

    int size;

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return searchNode(root, (K) key) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(Object key) {
        BSTNode res = searchNode(root, (K) key);
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
    @SuppressWarnings("unchecked")
    public void put(Object key, Object value) {
        root = addNode(root, (K) key, (V) value);
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
    public Set keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator iterator() {
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
