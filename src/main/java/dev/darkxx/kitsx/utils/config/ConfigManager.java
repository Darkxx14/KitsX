package dev.darkxx.kitsx.utils.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static ConfigManager instance;
    private final Plugin plugin;
    private final Map<String, FileConfiguration> configurations = new HashMap<>();

    private ConfigManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public static ConfigManager get(Plugin plugin) {
        if (instance == null) {
            instance = new ConfigManager(plugin);
        }
        return instance;
    }

    public FileConfiguration getConfig(String filePath) {
        if (!configurations.containsKey(filePath)) {
            create(filePath);
        }
        return configurations.get(filePath);
    }

    public void saveConfig(String filePath) {
        File configFile = new File(plugin.getDataFolder(), filePath);
        FileConfiguration config = getConfig(filePath);
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save config file: " + filePath);
        }
    }

    public void reloadConfig(String filePath) {
        File configFile = new File(plugin.getDataFolder(), filePath);
        configurations.put(filePath, YamlConfiguration.loadConfiguration(configFile));
    }

    public void create(String filePath) {
        File configFile = new File(plugin.getDataFolder(), filePath);
        if (!configFile.exists()) {
            plugin.saveResource(filePath, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        configurations.put(filePath, config);
    }

    public void set(String filePath, String path, Object value) {
        FileConfiguration config = getConfig(filePath);
        config.set(path, value);
    }

    public Object get(String filePath, String path) {
        FileConfiguration config = getConfig(filePath);
        return config.get(path);
    }

    public boolean contains(String filePath, String path) {
        FileConfiguration config = getConfig(filePath);
        return config.contains(path);
    }

    public Object getItemStack(String filePath, String path) {
        FileConfiguration config = getConfig(filePath);
        return config.getItemStack(path);
    }

    public void remove(String filePath, String path) {
        FileConfiguration config = getConfig(filePath);
        config.set(path, null);
    }
}
