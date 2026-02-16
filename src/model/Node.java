package model;

import java.time.LocalDateTime;

public class Node {
    private int key;
    private int val;
    private LocalDateTime expiryTime;
    private Node next;
    private Node prev;

    Node(int key, int val, LocalDateTime expiryTime) {
        this.key = key;
        this.val = val;
        this.expiryTime = expiryTime;
        next = null;
        prev = null;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryTime);
    }
}
