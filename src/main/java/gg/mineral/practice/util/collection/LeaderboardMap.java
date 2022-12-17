package gg.mineral.practice.util.collection;

import gg.mineral.api.collection.GlueList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class LeaderboardMap {
    final int size;

    public LeaderboardMap() {
        this(10);
    }

    @AllArgsConstructor
    public static class Entry {
        @Setter
        @Getter
        String key;
        @Setter
        @Getter
        int value;
    }

    @Getter
    GlueList<Entry> entries = new GlueList<>();

    private int binarySearch(int value) {
        int lastIndex = entries.size();
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
        return entries.isEmpty() ? 0
                : elo <= get(entries.size() - 1).getValue() ? entries.size() : binarySearch(elo);
    }

    private int findPositionOfEntry(int elo) {
        int lastIndex = entries.size() - 1;
        return entries.isEmpty() ? 0
                : elo == get(lastIndex).getValue() ? lastIndex : binarySearch(elo);
    }

    public Entry get(int index) {
        return entries.get(index);
    }

    public void put(String key, int value) {
        entries.add(findPosition(value), new Entry(key, value));

        if (entries.size() > size) {
            entries.remove(entries.size() - 1);
        }
    }

    public void putOrReplace(String key, int value, int oldValue) {
        int oldPosition = findPositionOfEntry(oldValue);
        Entry oldEntry = entries.size() - 1 < oldPosition ? null : entries.get(oldPosition);

        if (oldEntry != null && oldEntry.getKey().equalsIgnoreCase(key)) {
            entries.remove(oldPosition);
        }

        entries.add(findPosition(value), new Entry(key, value));

        if (entries.size() > size) {
            entries.remove(entries.size() - 1);
        }
    }

    public void replace(String key, int value, int oldValue) {

        Entry oldEntry = entries.remove(findPositionOfEntry(oldValue));
        oldEntry.setValue(value);

        entries.add(findPosition(value), oldEntry);

        if (entries.size() > size) {
            entries.remove(entries.size() - 1);
        }
    }
}
