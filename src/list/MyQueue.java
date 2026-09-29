package list;

import java.util.NoSuchElementException;
import java.util.Objects;

public class MyQueue<T> {

    private Object[] data;
    private int head;
    private int size;

    public MyQueue() {
        this.data = new Object[10];
        this.head = 0;
        this.size = 0;
    }

    private void ensureCapacity() {
        if (size < data.length) return;

        Object[] newData = new Object[data.length * 2];
        for (int i = 0; i < size; i++) {
            newData[i] = data[(head + i) % data.length];
        }
        data = newData;
        head = 0;
    }

    public void enqueue(T x) {
        ensureCapacity();
        int index = (head + size) % data.length;
        data[index] = x;
        size++;
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue vacía");
        }
        T value = (T) data[head];
        data[head] = null;
        head = (head + 1) % data.length;
        size--;
        return value;
    }

    public T front() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue vacía");
        }
        return (T) data[head];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public boolean delete(T value) {
        for (int i = 0; i < size; i++) {
            T current = (T) data[(head + i) % data.length];
            if (Objects.equals(current, value)) {
                for (int j = i; j < size - 1; j++) {
                    data[(head + j) % data.length] = data[(head + j + 1) % data.length];
                }
                data[(head + size - 1) % data.length] = null;
                size--;
                return true;
            }
        }
        return false;
    }
}
