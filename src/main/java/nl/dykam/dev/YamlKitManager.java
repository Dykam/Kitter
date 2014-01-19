package nl.dykam.dev;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlKitManager implements KitManager {
    private final Plugin plugin;
    private String configPath;
    HashMap<String, YamlKit> kits = new HashMap<>();

    public YamlKitManager(Plugin plugin, String path) {
        this.plugin = plugin;
        configPath = path;
        reload();
    }

    @Override
    public Iterable<Kit> getApplicable(Permissible permissible) {
        List<Kit> applicableKits = new ArrayList<>();
        for (Map.Entry<String, YamlKit> kitEntry : kits.entrySet()) {
            if(kitEntry.getValue().isApplicable(permissible))
                applicableKits.add(kitEntry.getValue());
        }
        return applicableKits;
    }
    @Override
    public boolean isApplicable(Permissible permissible, String kitName) {
        return permissible.hasPermission("kitter.kit.*") || permissible.hasPermission("kitter.kit." + kitName);
    }

    @Override
    public boolean exists(String kitName) {
        return kits.containsKey(kitName);
    }

    @Override
    public YamlKit get(String kitName) {
        return kits.get(kitName);
    }

    public void reload() {
        kits.clear();
        ConfigurationSection config = getConfig();
        for (Map.Entry<String, Object> kitEntry : config.getValues(false).entrySet()) {
            YamlKit kit = new YamlKit(kitEntry.getKey(), config);
            kits.put(kitEntry.getKey(), kit);
        }

    }

    public void save(String kitName, Player player) {
        YamlKit kit = get(kitName);
        if(kit == null)
            kit = new YamlKit(kitName);
        kit.read(player);
        kit.save(getConfig());
        plugin.saveConfig();
        kits.put(kitName, kit);
    }

    public void remove(String kitName) {
        getConfig().set(kitName, null);
        kits.remove(kitName);
    }

    private ConfigurationSection getConfig() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection(configPath);
        if(section == null)
            section = config.createSection(configPath);
        return section;
    }
}
