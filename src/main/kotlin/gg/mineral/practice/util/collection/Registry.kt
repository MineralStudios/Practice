package gg.mineral.practice.util.collection

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.util.function.Function

class Registry<T, Q>(private val function: Function<T, Q>) {
    private val keyValueMap: MutableMap<Q, T> = Object2ObjectOpenHashMap()

    val registeredObjects: Collection<T>
        get() = keyValueMap.values

    fun iterator(): Iterator<Map.Entry<Q, T>> = keyValueMap.entries.iterator()

    fun clear() = keyValueMap.clear()

    fun get(key: Q) = keyValueMap[key]

    fun put(value: T) {
        keyValueMap[function.apply(value)] = value
    }

    fun unregister(value: T) {
        keyValueMap.remove(function.apply(value))
    }
}
