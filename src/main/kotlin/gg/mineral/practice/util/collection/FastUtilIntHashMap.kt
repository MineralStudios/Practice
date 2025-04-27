package gg.mineral.practice.util.collection

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.server.v1_8_R3.IntHashMap

/**
 * A version of IntHashMap that uses Fastutil's Int2ObjectOpenHashMap internally.
 *
 * All operations (get, put, remove, clear, etc.) are overridden to delegate to Fastutil’s map.
 */
class FastUtilIntHashMap<V> : IntHashMap<V>() {

    private val fastMap = Int2ObjectOpenHashMap<V>()

    /**
     * Returns the value associated with the given key.
     */
    override fun get(key: Int): V? {
        return fastMap.get(key)
    }

    /**
     * Checks whether the specified key exists in the map.
     * (Corresponds to the original method [b].)
     */
    override fun b(key: Int): Boolean {
        return fastMap.containsKey(key)
    }

    /**
     * Associates the given value with the specified key.
     * (Corresponds to the original method [a].)
     */
    override fun a(key: Int, value: V) {
        fastMap.put(key, value)
    }

    /**
     * Removes the mapping for the specified key and returns the removed value.
     * (Corresponds to the original method [d].)
     */
    override fun d(key: Int): V? {
        return fastMap.remove(key)
    }

    /**
     * Clears all mappings from the map.
     * (Corresponds to the original method [c].)
     */
    override fun c() {
        fastMap.clear()
    }

    /**
     * Returns the number of key-value mappings in the map.
     */
    fun size(): Int = fastMap.size
}
