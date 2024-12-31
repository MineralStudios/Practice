package gg.mineral.practice.util.collection;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

@RequiredArgsConstructor
public class Registry<T, Q> {
    private final Map<Q, T> keyValueMap = new Object2ObjectOpenHashMap<>();
    private final Function<T, Q> function;

    public Collection<T> getRegisteredObjects() {
        return keyValueMap.values();
    }

    public Iterator<Entry<Q, T>> iterator() {
        return keyValueMap.entrySet().iterator();
    }

    public void clear() {
        keyValueMap.clear();
    }

    public T get(Q key) {
        return keyValueMap.get(key);
    }

    public void put(T value) {
        keyValueMap.put(function.apply(value), value);
    }

    public void unregister(T value) {
        keyValueMap.remove(function.apply(value));
    }
}
