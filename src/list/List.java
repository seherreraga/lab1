package list;

public interface List<T> {
    Position<T> pushFront(T x);
    Position<T> pushBack(T x);
    Position<T> addBefore(Position<T> p, T x);
    Position<T> addAfter(Position<T> p, T x);

    T popFront();
    T popBack();

    void erase(Position<T> p);
    Position<T> find(T x);

    boolean isEmpty();
    int size();

    T front();
    T back();
}
