package gg.mineral.practice.util.config

import gg.mineral.api.config.FileConfiguration

class StringProp(config: FileConfiguration, path: String, default: String) : CachedProp<String>(config, path, default) {
    override fun readValue(): String = config.getString(path, default)
    override fun writeValue(value: String) {
        config[path] = value
    }
}