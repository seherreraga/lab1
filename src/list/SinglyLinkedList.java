package list;

import java.util.NoSuchElementException;

public class SinglyLinkedList<T> implements List<T> {

    private final class Node implements Position<T> {
        private final SinglyLinkedList<T> owner = SinglyLinkedList.this;
        private T element;
        private Node next;

        private Node(T element) {
            this.element = element;
            this.next = null;
        }

        @Override
        public T getElement() {
            return element;
        }
    }

    private Node head;
    private int size;

    public SinglyLinkedList() {
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
        newNode.next = head;
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
        size++;
        return newNode;
    }

    @Override
    public Position<T> addBefore(Position<T> p, T x) {
        Node pos = validatePosition(p);

        Node newNode = new Node(x);

        if (pos == head) {
            newNode.next = head;
            head = newNode;
            size++;
            return newNode;
        }

        Node prev = null;
        Node current = head;
        while (current != null && current != pos) {
            prev = current;
            current = current.next;
        }

        if (current == null) {
            throw new IllegalArgumentException("La posición no pertenece a esta lista");
        }

        prev.next = newNode;
        newNode.next = pos;
        size++;
        return newNode;
    }

    @Override
    public Position<T> addAfter(Position<T> p, T x) {
        Node pos = validatePosition(p);

        Node newNode = new Node(x);
        newNode.next = pos.next;
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
        head = head.next;
        size--;
        return value;
    }

    @Override
    public T popBack() {
        if (isEmpty()) {
            throw new NoSuchElementException("Lista vacía");
        }

        if (head.next == null) {
            T value = head.element;
            head = null;
            size--;
            return value;
        }

        Node prev = null;
        Node current = head;
        while (current.next != null) {
            prev = current;
            current = current.next;
        }

        T value = current.element;
        prev.next = null;
        size--;
        return value;
    }

    @Override
    public void erase(Position<T> p) {
        Node pos = validatePosition(p);

        if (pos == head) {
            head = head.next;
            size--;
            return;
        }

        Node prev = null;
        Node current = head;
        while (current != null && current != pos) {
            prev = current;
            current = current.next;
        }

        if (current == null) {
            throw new IllegalArgumentException("La posición no pertenece a esta lista");
        }

        prev.next = pos.next;
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
