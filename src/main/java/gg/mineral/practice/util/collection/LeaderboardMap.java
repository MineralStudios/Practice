package gg.mineral.practice.util.collection;

import java.util.List;

import gg.mineral.api.collection.GlueList;

public class LeaderboardMap {
    int size;

    public LeaderboardMap(int size) {
        this.size = size;
    }

    public LeaderboardMap() {
        this(10);
    }

    public static class Entry {
        String key;
        int value;

        public Entry(String key, int value) {
            this.key = key;
            this.value = value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    GlueList<Entry> entryList = new GlueList<>();

    private int binarySearch(int value) {
        int lastIndex = entryList.size();
        int firstIndex = 0;
        int midIndex = lastIndex / 2;

        while (firstIndex != lastIndex) {
            int midValue = get(midIndex).getValue();

            if (midValue > value) {
                // below on leaderboard
                firstIndex = midIndex + 1;
            } else if (midValue < value) {
                // above on leaderboard
                lastIndex = midIndex;
            } else {
                return midIndex;
            }

            midIndex = (firstIndex + lastIndex) / 2;
        }

        return firstIndex;
    }

    private int findPosition(int elo) {
        return entryList.isEmpty() ? 0
                : elo <= get(entryList.size() - 1).getValue() ? entryList.size() : binarySearch(elo);
    }

    public Entry get(int index) {
        return entryList.get(index);
    }

    public void put(String key, int value) {
        entryList.add(findPosition(value), new Entry(key, value));

        if (entryList.size() > size) {
            entryList.remove(entryList.size() - 1);
        }
    }

    public void replace(String key, int value, int oldValue) {

        Entry oldEntry = entryList.remove(findPosition(oldValue));
        oldEntry.setValue(value);

        entryList.add(findPosition(value), oldEntry);

        if (entryList.size() > size) {
            entryList.remove(entryList.size() - 1);
        }
    }

    public List<Entry> getEntries() {
        return entryList;
    }
}
