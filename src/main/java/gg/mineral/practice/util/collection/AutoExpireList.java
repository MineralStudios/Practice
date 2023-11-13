package gg.mineral.practice.util.collection;

import java.util.Iterator;

import org.bukkit.Bukkit;

import gg.mineral.practice.PracticePlugin;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public class AutoExpireList<K> implements Iterable<K> {

    public AutoExpireList() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
            removeExpired();
        }, 20, 20);
    }

    Object2LongOpenHashMap<K> map = new Object2LongOpenHashMap<>();

    public void add(K e) {
        map.put(e, System.currentTimeMillis());
    }

    public void removeExpired() {
        map.object2LongEntrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue() >= 30000);
    }

    public void clear() {
        map.clear();
    }

    public ObjectIterator<Entry<K>> entryIterator() {
        return map.object2LongEntrySet().iterator();
    }

    public Iterator<K> iterator() {
        return map.keySet().iterator();
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }
}
