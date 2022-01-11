package ms.uk.eclipse.util;

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

    int lastIndex;

    private int binarySearch(int value, int lastValue) {
        int mid = entryList.size() / 2;
        int initalValue = get(mid).getValue();

        if (initalValue > value) {
            while ((mid == lastValue ? lastValue : get(mid).getValue()) > value) {
                mid = (mid + lastIndex) / 2;

                if (mid >= lastIndex) {
                    break;
                }
            }
        } else {
            while (get(mid).getValue() < value) {
                mid /= 2;

                if (mid <= 0) {
                    break;
                }
            }
        }

        return mid;
    }

    private int findPosition(int elo) {
        if (entryList.size() == 0) {
            return 0;
        }

        int lastValue = get(lastIndex = entryList.size() - 1).getValue();
        return elo <= lastValue ? -1 : binarySearch(elo, lastValue);
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
    }

    public List<Entry> getEntries() {
        return entryList;
    }
}
