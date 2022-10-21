package gg.mineral.practice.util;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FileConfiguration {
    String fileName;
    String fileDirectory;
    File configFile;
    File directory;
    org.bukkit.configuration.file.FileConfiguration config;

    public FileConfiguration(String fileName, String fileDirectory) {
        this.fileName = fileName;
        this.fileDirectory = fileDirectory;
        this.directory = new File(fileDirectory);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        this.configFile = new File(fileDirectory + "/" + fileName);

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration(String FileName) {
        this.fileName = FileName;
        this.configFile = new File(FileName);

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public Boolean getBoolean(String string, boolean b) {
        return config.getBoolean(string, b);
    }

    public Integer getInt(String string, Integer i) {
        return config.getInt(string, i);
    }

    public void set(String string, Object obj) {
        config.set(string, obj);
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public Vector getVector(String string, Object o) {
        return (Vector) config.get(string, o);
    }

    public String getString(String string, String s) {
        return config.getString(string, s);
    }

    public ItemStack getItemstack(String string, ItemStack itemStack) {
        return (ItemStack) config.get(string, itemStack);
    }

    public ConfigurationSection getConfigurationSection(String string) {
        return config.getConfigurationSection(string);
    }

    public double getDouble(String string, double d) {
        return config.getDouble(string, d);
    }

    public void remove(String s) {
        set(s, null);
    }

    public org.bukkit.configuration.file.FileConfiguration bukkitConfig() {
        return config;
    }
}
