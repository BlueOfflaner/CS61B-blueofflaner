package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author blueofflaner <blueofflaner@gmail.com>
 * Created on 2024-04-16
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private BSTNode root;

    private HashSet<K> keySet = new HashSet<>();

    private int size;

    @Override
    public void clear() {
        root = null;
        size = 0;
        keySet.clear();
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
        root = addNode(root, key, value, null);
    }

    private BSTNode addNode(BSTNode cur, K key, V value, BSTNode parent) {
        if (cur == null) {
            size++;
            keySet.add(key);
            BSTNode res = new BSTNode(key, value);
            res.parent = parent;
            return res;
        }
        if (key.compareTo(cur.key) > 0) {
            cur.right = addNode(cur.right, key, value, cur);
        } else if (key.compareTo(cur.key) < 0) {
            cur.left = addNode(cur.left, key, value, cur);
        } else {
            cur.val = value;
        }
        return cur;
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public V remove(K key) {
        V resValue = get(key);
        if (resValue != null) {
            size--;
        }
        root = removeNode(root, key);
        keySet.remove(key);
        return resValue;
    }

    @Override
    public V remove(K key, V value) {
        return remove(key);
    }

    private BSTNode removeNode(BSTNode cur, K key) {
        if (cur == null) {
            return null;
        }
        if (key.compareTo(cur.key) > 0) {
            cur.right = removeNode(cur.right, key);
        } else if (key.compareTo(cur.key) < 0)  {
            cur.left = removeNode(cur.left, key);
        } else {
            if (cur.left == null && cur.right != null) {
                BSTNode tmp = cur.right;
                cur.right.parent = cur.parent;
                cur = null;
                return tmp;
            } else if (cur.right == null && cur.left != null) {
                BSTNode tmp = cur.left;
                cur.left.parent = cur.parent;
                cur = null;
                return tmp;
            } else if (cur.left != null && cur.right != null) {
                BSTNode tmp = getSuccessor(cur);
                cur.key = tmp.key;
                cur.val = tmp.val;
                cur.right = removeNode(cur.right, cur.key);
            } else {
                cur = null;
            }
        }
        return cur;
    }

    @Override
    public Iterator<K> iterator() {
        return new IteratorOfBSTMap(root);
    }

    private class IteratorOfBSTMap implements Iterator<K> {
        private BSTNode node;

        public IteratorOfBSTMap(BSTNode n) {
            this.node = n;
        }

        @Override
        public boolean hasNext() {
            return getSuccessor(node) != null;
        }

        @Override
        public K next() {
            if (hasNext()) {
                node = getSuccessor(node);
                return node.key;
            }
            return null;
        }
    }

    private BSTNode getSuccessor(BSTNode node) {
        if (node.right != null) {
            return getMin(node.right);
        } else {
            BSTNode cur = node;
            BSTNode parent = cur.parent;
            while (parent != null && cur == parent.right) {
                cur = parent;
                parent = cur.parent;
            }
            return parent;
        }
    }

    private BSTNode getMin(BSTNode node) {
        BSTNode cur = node;
        while (cur.left != null) {
            cur = cur.left;
        }
        return cur;
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
        BSTNode parent;

        BSTNode() { }
        BSTNode(K k, V v) {
            key = k;
            val = v;
        }
    }
}
