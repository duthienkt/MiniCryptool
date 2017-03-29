package vn.bluesky.utils.structure;

import vn.bluesky.utils.data.ArrayUtil;

import java.util.Collection;
import java.util.Set;



/**
 * Created by asus on 9/20/2016.
 */
public class StringMap<T> {

    public final int base;
    private final T[] data;
    private final String[] keys;
    private int n;

    public StringMap(Class<T> clazz, int base) {
        this.base = base;
        data = ArrayUtil.allocArray(clazz, base);
        keys = new String[base];
        n = 0;
    }

    public int size() {
        return n;
    }

    public boolean isEmpty() {
        return n == 0;
    }


    public boolean containsKey(String o) {

        return false;
    }


    public T get(Object key) {
        return null;
    }


    public T put(String s, T t) {
        return null;
    }


    public T remove(Object o) {
        return null;
    }


    public void clear() {

    }


    public Set<String> keySet() {
        return null;
    }


    public Collection<T> values() {
        return null;
    }


}
