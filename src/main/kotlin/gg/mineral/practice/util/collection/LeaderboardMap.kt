package gg.mineral.practice.util.collection

import gg.mineral.api.collection.GlueList

class LeaderboardMap(val size: Int = 10) {

    class Entry(val key: String, var value: Int)

    var entries: GlueList<Entry> = GlueList()

    private fun binarySearch(value: Int): Int {
        var lastIndex = entries.size
        var firstIndex = 0
        var midIndex = lastIndex / 2

        while (firstIndex != lastIndex) {
            val midValue: Int = get(midIndex).value

            if (midValue > value) firstIndex = midIndex + 1
            else if (midValue < value) lastIndex = midIndex
            else return midIndex

            midIndex = (firstIndex + lastIndex) / 2
        }

        return firstIndex
    }

    private fun findPosition(elo: Int): Int {
        return if (entries.isEmpty())
            0
        else
            if (elo <= get(entries.size - 1).value) entries.size else binarySearch(elo)
    }

    private fun findPositionOfEntry(elo: Int): Int {
        val lastIndex = entries.size - 1
        return if (entries.isEmpty())
            0
        else
            if (elo == get(lastIndex).value) lastIndex else binarySearch(elo)
    }

    fun get(index: Int): Entry = entries[index]

    fun put(key: String, value: Int) {
        entries.add(findPosition(value), Entry(key, value))
        if (entries.size > size) entries.removeAt(entries.size - 1)
    }

    fun putNoDuplicate(key: String, value: Int) {
        for (entry in entries) if (entry.key == key) return

        put(key, value)
    }

    fun putOrReplace(key: String, value: Int, oldValue: Int) {
        val oldPosition = findPositionOfEntry(oldValue)
        val oldEntry = if (entries.size - 1 < oldPosition) null else entries[oldPosition]

        if (oldEntry != null && oldEntry.key.equals(key, ignoreCase = true)) entries.removeAt(oldPosition)

        entries.add(findPosition(value), Entry(key, value))

        if (entries.size > size) entries.removeAt(entries.size - 1)
    }

    fun replace(value: Int, oldValue: Int) {
        val oldEntry = entries.removeAt(findPositionOfEntry(oldValue))
        oldEntry.value = value

        entries.add(findPosition(value), oldEntry)

        if (entries.size > size) entries.removeAt(entries.size - 1)
    }
}
