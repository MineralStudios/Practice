package gg.mineral.practice.util.config


import gg.mineral.practice.util.config.yaml.FileConfiguration
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class CachedProp<T>(
    protected val config: FileConfiguration,
    protected val path: String,
    protected val default: T
) : ReadWriteProperty<Any?, T> {
    private var cache: T? = null

    protected abstract fun readValue(): T
    protected abstract fun writeValue(value: T)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (cache == null)
            cache = readValue()

        return cache!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        cache = value
        writeValue(value)
        config.save()
    }
}
