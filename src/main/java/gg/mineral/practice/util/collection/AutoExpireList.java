package gg.mineral.practice.util.collection;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AutoExpireList<K> implements Iterable<K> {

    LinkedHashMap<K, Long> map = new LinkedHashMap<K, Long>(100, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<K, Long> eldest) {
            return (System.currentTimeMillis() - eldest.getValue() >= 60000);
        }
    };

    public void add(K e) {
        map.put(e, System.currentTimeMillis());
    }

    public void remove(K e) {
        map.remove(e);
    }

    public void clear() {
        map.clear();
    }

    public Iterator<Entry<K, Long>> entryIterator() {
        return map.entrySet().iterator();
    }

    public Iterator<K> iterator() {
        return map.keySet().iterator();
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }
}
