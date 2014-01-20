package nl.dykam.dev;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class ConfigKitManager extends YamlKitManager {
    private final Plugin plugin;
    private String configPath;

    public ConfigKitManager(Plugin plugin, String path) {
        this.plugin = plugin;
        configPath = path;
        reload();
    }

    @Override
    public void reload() {
        kits.clear();
        ConfigurationSection config = getConfig();
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
        plugin.saveConfig();
        kits.put(kitName, kit);
    }

    @Override
    protected ConfigurationSection getConfig() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection(configPath);
        if(section == null)
            section = config.createSection(configPath);
        return section;
    }
}
