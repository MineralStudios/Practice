package gg.mineral.practice.util.config

import gg.mineral.practice.util.config.yaml.FileConfiguration


class BoolProp(config: FileConfiguration, path: String, default: Boolean) : CachedProp<Boolean>(config, path, default) {
    override fun readValue(): Boolean = config.getBoolean(path, default)
    override fun writeValue(value: Boolean) {
        config[path] = value
    }
}