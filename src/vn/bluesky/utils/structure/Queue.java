package vn.bluesky.utils.structure;
import java.lang.reflect.Array;

public class Queue<T> {
    protected T Ts[];
    int l, r;
    private final int maxN;
    private int count = 0;

    public final int maxSize;

    public int getCount() {
        return count;
    }

    public Queue(Class type, int maxSize) {
        this.maxSize = maxSize;
        maxN = maxSize+1;
        Ts = (T[]) Array.newInstance(type, maxN);
        l = 0;
        r = -1;
    }

    synchronized public Queue add(T T) {
        r = (r + 1) % maxN;
        Ts[r] = T;
        count++;
        return this;
    }

    synchronized public void clear()
    {
        l = (r + 1) % maxN;
    }

    public boolean empty() {
        return (r + 1) % maxN == l;
    }

    synchronized public T pop() {

        if (empty()) return null;
        T res = Ts[l];
        l = (l + 1) % maxN;
        count--;
        return res;
    }
}
