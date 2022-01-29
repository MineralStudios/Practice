package ms.uk.eclipse.util;

import java.util.Iterator;
import java.util.List;

import land.strafe.api.collection.GlueList;

public class LeaderboardMap {

    final static Entry DEFAULT_ENTRY = new Entry("Empty", 1000);
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

    private int binaryLinearSearch(int value) {
        int lastIndex = entryList.size() - 1;
        int firstIndex = 0;
        int mid = lastIndex / 2;
        int midValue = get(mid).getValue();

        if (midValue > value) {
            firstIndex = mid + 1;

            for (int i = firstIndex; i <= lastIndex; i++) {
                midValue = get(i).getValue();

                if (midValue > value) {
                    continue;
                }

                return i;
            }
        } else if (midValue < value) {
            lastIndex = mid;

            for (int i = firstIndex; i <= lastIndex; i++) {
                midValue = get(i).getValue();

                if (midValue > value) {
                    continue;
                }

                return i;
            }
        } else {
            return mid;
        }

        return -1;
    }

    private int findPosition(int elo) {
        if (entryList.size() == 0) {
            return 0;
        }

        boolean full = entryList.size() >= size;
        int lastValue = get(entryList.size() - 1).getValue();
        boolean lastPosition = elo <= lastValue;

        if (lastPosition) {
            if (full) {
                return -1;
            }

            return entryList.size();
        }

        return binaryLinearSearch(elo);
    }

    public Entry get(int index) {
        Entry value = entryList.get(index);

        if (value == null) {
            return DEFAULT_ENTRY;
        }

        return value;
    }

    public void put(String key, int value) {
        int position = findPosition(value);

        if (position == -1) {
            return;
        }

        entryList.add(position, new Entry(key, value));

        if (entryList.size() > size) {
            for (int i = size; i < entryList.size(); i++) {
                entryList.remove(i);
            }
        }

        boolean encountered = false;

        Iterator<Entry> iterator = entryList.iterator();

        while (iterator.hasNext()) {
            Entry entry = iterator.next();

            if (entry.getKey().equalsIgnoreCase(key)) {
                if (encountered) {
                    iterator.remove();
                    break;
                }

                encountered = true;
            }
        }
    }

    public List<Entry> getEntries() {
        return entryList;
    }
}
