package vn.bluesky.utils.structure;

import vn.bluesky.utils.data.ArrayUtil;

import java.util.Random;

/**
 * Created by DuThien on 23/03/2017.
 */
public class Heap<T extends Heap.HeapNode> {

    T[] H;
    int percent = 0;
    Random random = new Random(System.currentTimeMillis());
    private int n;
    public Heap(Class<T> clazz, int maxN) {
        H = ArrayUtil.allocArray(clazz, maxN);
        n = 0;
    }

    public void setProbabilityOfSwap(int percent) {
        this.percent = percent;
    }

    private boolean randomZ() {
        return Math.abs(random.nextInt()) % 101 <= percent;
    }

    public T top() {
        return H[0];
    }

    public void removeTop() {
        H[0] = H[--n];
        downHeap(0);

    }

    public void push(T x) {
        H[n] = x;
        upHeap(n++);
    }

    public int size() {
        return n;
    }

    public void removeAll() {
        n = 0;
    }

    private void upHeap(int i) {
        int j;
        T t;
        while (i > 0) {
            j = (i - 1) / 2;
            int code = H[j].compare(H[i]);
            if (code > 0)
                break;
            if (code == 0 && !randomZ()) break;
            t = H[i];
            H[i] = H[j];
            H[j] = t;
            i = j;
        }
    }

    private void downHeap(int i) {
        int j;
        T t;
        while (i * 2 + 1 < n) {
            j = i * 2 + 1;
            if (j + 1 < n) {
                int code = H[j + 1].compare(H[j]);
                if (code > 0 ||(code == 0 && randomZ())) ++j;
            }
            int code = H[i].compare(H[j]);
            if (code > 0 ||(code == 0 && randomZ())) break;
            t = H[i];
            H[i] = H[j];
            H[j] = t;
            i = j;
        }
    }

    public interface HeapNode{
        int compare(HeapNode x);
    }

}
