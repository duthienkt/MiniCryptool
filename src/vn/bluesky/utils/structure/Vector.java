package vn.bluesky.utils.structure;

import vn.bluesky.utils.data.ArrayUtil;

import java.util.Comparator;

/**
 * Created by asus on 9/20/2016.
 */
public class Vector<T> {
    private T[] data;
    private int count;
    private final Class<T> clazz;

    public Vector(Class<T> clazz) {
        this.clazz = clazz;
        count = 0;
        data = ArrayUtil.allocArray(clazz, 256);
    }

    @SuppressWarnings("unchecked")
    public Vector<T> pushBack(T value) {

        if (count >= data.length) {
            data = (T[]) ArrayUtil.resizeArray(clazz, data, data.length * 2);
        }
        data[count++] = value;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Vector<T> popBack() {
        count--;
        if (data.length > 256)
            if (count <= data.length / 2)
                data = ArrayUtil.resizeArray(clazz, data, data.length / 2);
        return this;
    }

    public T getBack() {
        return data[count - 1];
    }

    public Vector<T> swapItem(int i, int j) {
        T temp;
        temp = data[i];
        data[i] = data[j];
        data[j] = temp;
        return this;
    }

    public Vector<T> swapWithBackItem(int index) {
        return swapItem(index, count - 1);
    }

    public Vector<T> removeWithBackItem(int index) {
        data[index] = data[count - 1];
        popBack();
        return this;
    }


    public T get(int index) {
        return data[index];
    }


    public Vector<T> set() {
        return this;
    }

    public int size() {
        return count;
    }


    private void quickSort(Comparator<T> comparator, int low, int high) {
        if (low >= high) return;
        int i = low;
        int j = high;
        T p = data[(i + j) / 2];

        do {
            while (comparator.compare(data[i], p) < 0) ++i;
            while (comparator.compare(data[j], p) > 0) --j;
            if (i <= j) {
                if (i != j) swapItem(i, j);
                ++i;
                --j;
            }

        } while (i < j);

        quickSort(comparator, low, j);
        quickSort(comparator, i, high);

    }

    public void sort(Comparator<T> comparator) {
        quickSort(comparator, 0, count - 1);
    }


}
