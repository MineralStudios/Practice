package gg.mineral.practice.util.config

import gg.mineral.api.config.FileConfiguration

class IntProp(config: FileConfiguration, path: String, default: Int) : CachedProp<Int>(config, path, default) {
    override fun readValue(): Int = config.getInt(path, default)
    override fun writeValue(value: Int) {
        config[path] = value
    }
}