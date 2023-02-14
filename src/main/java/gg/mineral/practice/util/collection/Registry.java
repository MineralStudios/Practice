package gg.mineral.practice.util.collection;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Registry<T, Q> {
    Map<Q, T> keyValueMap = new Object2ObjectOpenHashMap<>();
    final Function<T, Q> function;

    public Collection<T> getRegisteredObjects() {
        return keyValueMap.values();
    }

    public void clear() {
        keyValueMap.clear();
    }

    public T get(String key) {
        return keyValueMap.get(key);
    }

    public void put(T value) {
        keyValueMap.put(function.apply(value), value);
    }

    public void unregister(T value) {
        keyValueMap.remove(function.apply(value));
    }
}
