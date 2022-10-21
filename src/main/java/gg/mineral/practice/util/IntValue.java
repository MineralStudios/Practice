package gg.mineral.practice.util;

public interface IntValue<T> {
    public int get(T obj);

    public void increment(T obj);

    public void decrement(T obj);
}
