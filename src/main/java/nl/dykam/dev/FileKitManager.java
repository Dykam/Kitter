package nl.dykam.dev;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FileKitManager extends YamlKitManager {
    private File file;
    private String path;
    private FileConfiguration configFile;
    private ConfigurationSection config;

    public FileKitManager(File file) {
        this(file, null);
    }

    public FileKitManager(File file, String path) {
        this.file = file;
        this.path = path;
        reload();
    }

    @Override
    public void reload() {
        config = configFile = YamlConfiguration.loadConfiguration(file);
        if (path != null) {
            config = configFile.getConfigurationSection(path);
            if(config == null)
                config = configFile.createSection(path);
        }
        kits.clear();
        for (Map.Entry<String, Object> kitEntry : config.getValues(false).entrySet()) {
            YamlKit kit = new YamlKit(kitEntry.getKey(), config);
            kits.put(kitEntry.getKey(), kit);
        }

    }

    @Override
    public void save(String kitName, Player player) {
        YamlKit kit = get(kitName);
        if(kit == null)
            kit = new YamlKit(kitName);
        kit.read(player);
        kit.save(getConfig());
        try {
            configFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        kits.put(kitName, kit);
    }

    @Override
    protected ConfigurationSection getConfig() {
        return config;
    }
}
