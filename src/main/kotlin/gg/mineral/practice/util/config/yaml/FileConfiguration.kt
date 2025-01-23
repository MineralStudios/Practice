package gg.mineral.practice.util.config.yaml

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.io.File
import java.io.IOException

class FileConfiguration(fileName: String, fileDirectory: String = "") {
    private var configFile: File
    private var directory: File? = null
    var config: FileConfiguration

    init {
        this.directory = File(fileDirectory)
        if (!directory!!.exists()) directory!!.mkdirs()

        this.configFile = File("$fileDirectory/$fileName")
        if (!configFile.exists()) {
            try {
                configFile.createNewFile()
            } catch (i: IOException) {
                i.printStackTrace()
            }
        }

        this.config = YamlConfiguration.loadConfiguration(this.configFile)
    }

    fun getBoolean(string: String?, b: Boolean) = config.getBoolean(string, b)

    fun getInt(string: String?, i: Int) = config.getInt(string, i)

    operator fun set(string: String, obj: Any?) {
        config[string] = obj
        this.save()
    }

    fun save() {
        try {
            config.save(this.configFile)
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }

    fun getVector(string: String, o: Any) = config[string, o] as Vector

    fun getString(string: String, s: String) = config.getString(string, s)

    fun getItemstack(string: String, itemStack: ItemStack?) = config[string, itemStack] as ItemStack

    fun getConfigurationSection(string: String): ConfigurationSection? = config.getConfigurationSection(string)

    fun getDouble(string: String, d: Double) = config.getDouble(string, d)

    fun remove(s: String) = this.set(s, null)

    fun bukkitConfig() = this.config
}