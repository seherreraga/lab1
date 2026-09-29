package list;

import java.util.NoSuchElementException;

public class DoublyLinkedList<T> implements List<T> {

    private final class Node implements Position<T> {
        private final DoublyLinkedList<T> owner = DoublyLinkedList.this;
        private T element;
        private Node prev;
        private Node next;

        private Node(T element) {
            this.element = element;
            this.prev = null;
            this.next = null;
        }

        @Override
        public T getElement() {
            return element;
        }
    }

    private Node head;
    private int size;

    public DoublyLinkedList() {
        this.head = null;
        this.size = 0;
    }

    private Node validatePosition(Position<T> p) {
        if (p == null) {
            throw new IllegalArgumentException("Posición nula");
        }
        if (!(p instanceof Node)) {
            throw new IllegalArgumentException("La posición no pertenece a esta lista");
        }
        Node node = (Node) p;
        if (node.owner != this) {
            throw new IllegalArgumentException("La posición no pertenece a esta lista");
        }
        return node;
    }

    @Override
    public Position<T> pushFront(T x) {
        Node newNode = new Node(x);

        if (head == null) {
            head = newNode;
            size++;
            return newNode;
        }

        newNode.next = head;
        head.prev = newNode;
        head = newNode;
        size++;
        return newNode;
    }

    @Override
    public Position<T> pushBack(T x) {
        Node newNode = new Node(x);

        if (head == null) {
            head = newNode;
            size++;
            return newNode;
        }

        Node current = head;
        while (current.next != null) {
            current = current.next;
        }

        current.next = newNode;
        newNode.prev = current;
        newNode.next = null;
        size++;
        return newNode;
    }

    @Override
    public Position<T> addBefore(Position<T> p, T x) {
        Node pos = validatePosition(p);

        Node newNode = new Node(x);

        if (pos == head) {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
            size++;
            return newNode;
        }

        Node prev = pos.prev;
        newNode.prev = prev;
        newNode.next = pos;
        pos.prev = newNode;

        if (prev != null) {
            prev.next = newNode;
        }

        size++;
        return newNode;
    }

    @Override
    public Position<T> addAfter(Position<T> p, T x) {
        Node pos = validatePosition(p);

        Node newNode = new Node(x);

        newNode.prev = pos;
        newNode.next = pos.next;

        if (pos.next != null) {
            pos.next.prev = newNode;
        }

        pos.next = newNode;
        size++;
        return newNode;
    }

    @Override
    public T popFront() {
        if (isEmpty()) {
            throw new NoSuchElementException("Lista vacía");
        }

        T value = head.element;

        if (head.next == null) {
            head = null;
        } else {
            head = head.next;
            head.prev = null;
        }

        size--;
        return value;
    }

    @Override
    public T popBack() {
        if (isEmpty()) {
            throw new NoSuchElementException("Lista vacía");
        }

        Node current = head;
        while (current.next != null) {
            current = current.next;
        }

        T value = current.element;

        if (current.prev == null) {
            head = null;
        } else {
            current.prev.next = null;
        }

        size--;
        return value;
    }

    @Override
    public void erase(Position<T> p) {
        Node pos = validatePosition(p);

        if (pos.prev != null) {
            pos.prev.next = pos.next;
        } else {
            head = pos.next;
        }

        if (pos.next != null) {
            pos.next.prev = pos.prev;
        }

        size--;
    }

    @Override
    public Position<T> find(T x) {
        Node current = head;
        while (current != null) {
            if (current.element == null ? x == null : current.element.equals(x)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T front() {
        if (isEmpty()) {
            throw new NoSuchElementException("Lista vacía");
        }
        return head.element;
    }

    @Override
    public T back() {
        if (isEmpty()) {
            throw new NoSuchElementException("Lista vacía");
        }

        Node current = head;
        while (current.next != null) {
            current = current.next;
        }
        return current.element;
    }
}
