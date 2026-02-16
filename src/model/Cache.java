package model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Cache {
    private final Node root;
    private Node tail;
    private int size;
    private final int capacity;
    private final long expireInMins;
    private final Map<Integer, Node> nodes;
    private final ReentrantLock lock;

    Cache (int capacity, long expireInMins) {
        root = new Node(-1, -1, null);
        tail = root;
        size = 0;
        this.capacity = capacity;
        this.nodes = new ConcurrentHashMap<>();
        this.expireInMins = expireInMins;
        lock = new ReentrantLock(true);
    }

    public boolean add(int key, int val) {
        lock.lock();

        try {
            Node node;
            if (nodes.containsKey(key)) {
                node = nodes.get(key);
                node.setVal(val);
                node.setExpiryTime(LocalDateTime.now().plusMinutes(expireInMins));
            } else {
                node = new Node(key, val, LocalDateTime.now().plusMinutes(expireInMins));
                if (size == capacity) {
                    evictLRU();
                }
                size++;
            }

            nodes.put(key, node);
            moveToHead(node);
        } finally {
            lock.unlock();
        }

        return true;
    }

    private void evictLRU() {
        int key = tail.getKey();
        removeFromList(tail);
        size--;
        nodes.remove(key);
    }

    private void moveToHead(Node node) {
        removeFromList(node);
        Node currHead = root.getNext();
        root.setNext(node);
        node.setNext(currHead);
        node.setPrev(root);

        if (currHead != null) {
            currHead.setPrev(node);
        }

        if (tail == root) {
            tail = node;
        }
    }

    public int get(int key) {
        lock.lock();

        try {
            if (!nodes.containsKey(key)) return -1;

            Node node = nodes.get(key);
            if (node.isExpired()) {
                evict(node);
                return -1;
            }

            moveToHead(node);
            return node.getVal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private void evict(Node node) {
        if (node == tail) {
            evictLRU();
        } else {
            removeFromList(node);
            nodes.remove(node.getKey());
            size--;
        }
    }

    private void removeFromList(Node node) {
        Node prev = node.getPrev();
        Node next = node.getNext();
        if (prev != null) prev.setNext(next);
        if (next != null) next.setPrev(prev);

        if (node == tail) {
            tail = prev;
        }
        node.setPrev(null);
        node.setNext(null);
    }
}
