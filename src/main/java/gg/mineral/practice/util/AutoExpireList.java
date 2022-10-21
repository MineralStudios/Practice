package gg.mineral.practice.util;

import java.util.Iterator;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public class AutoExpireList<K> implements Iterable<K> {

    Object2LongLinkedOpenCustomHashMap<K> map = new Object2LongLinkedOpenCustomHashMap<K>(100, 0.75F,
            new Hash.Strategy<K>() {

                @Override
                public boolean equals(K arg0, K arg1) {
                    return arg0.equals(arg1);
                }

                @Override
                public int hashCode(K arg0) {
                    return arg0.hashCode();
                }

            }) {

        @Override
        public long put(K k, long v) {
            if (System.currentTimeMillis() - this.getLong(k) >= 60000) {
                removeLastLong();
            }

            return super.putAndMoveToFirst(k, v);
        }
    };

    public void add(K e) {
        map.put(e, System.currentTimeMillis());
    }

    public void remove(K e) {
        map.removeLong(e);
    }

    public ObjectBidirectionalIterator<it.unimi.dsi.fastutil.objects.Object2LongMap.Entry<K>> entryIterator() {
        return map.object2LongEntrySet().iterator();
    }

    @Override
    public Iterator<K> iterator() {
        return map.keySet().iterator();
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }
}
