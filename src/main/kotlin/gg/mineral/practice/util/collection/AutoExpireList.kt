package gg.mineral.practice.util.collection;

import gg.mineral.practice.PracticePlugin;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class AutoExpireList<K> implements Iterable<K> {

    public AutoExpireList() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, this::removeExpired, 20, 20);
    }

    private final Object2LongOpenHashMap<K> map = new Object2LongOpenHashMap<>();

    public void add(K e) {
        map.put(e, System.currentTimeMillis());
    }

    public void removeExpired() {
        map.object2LongEntrySet().removeIf(entry -> System.currentTimeMillis() - entry.getLongValue() >= 30000);
    }

    public void clear() {
        map.clear();
    }

    public ObjectIterator<Entry<K>> entryIterator() {
        return map.object2LongEntrySet().iterator();
    }

    public @NotNull Iterator<K> iterator() {
        return map.keySet().iterator();
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }
}
