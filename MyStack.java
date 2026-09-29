package list;

import java.util.NoSuchElementException;
import java.util.Objects;

public class MyStack<T> {

    private Object[] data;
    private int size;

    public MyStack() {
        this.data = new Object[10];
        this.size = 0;
    }

    private void ensureCapacity() {
        if (size < data.length) return;

        Object[] newData = new Object[data.length * 2];
        System.arraycopy(data, 0, newData, 0, size);
        data = newData;
    }

    public void push(T x) {
        ensureCapacity();
        data[size++] = x;
    }

    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack vacío");
        }
        T value = (T) data[--size];
        data[size] = null;
        return value;
    }

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack vacío");
        }
        return (T) data[size - 1];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public boolean delete(T value) {
        for (int i = size - 1; i >= 0; i--) {
            if (Objects.equals(data[i], value)) {
                for (int j = i; j < size - 1; j++) {
                    data[j] = data[j + 1];
                }
                data[--size] = null;
                return true;
            }
        }
        return false;
    }
}
