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

public class KitManager {
    private final Plugin plugin;
    private String configPath;
    HashMap<String, Kit> kits = new HashMap<>();

    public KitManager(Plugin plugin, String path) {
        this.plugin = plugin;
        configPath = path;
        reload();
    }

    public Iterable<Kit> getApplicable(Permissible permissible) {
        List<Kit> applicableKits = new ArrayList<>();
        for (Map.Entry<String, Kit> kitEntry : kits.entrySet()) {
            if(kitEntry.getValue().isApplicable(permissible))
                applicableKits.add(kitEntry.getValue());
        }
        return applicableKits;
    }
    public boolean isApplicable(Permissible permissible, String kitName) {
        return permissible.hasPermission("kitter.kit.*") || permissible.hasPermission("kitter.kit." + kitName);
    }

    private boolean exists(String kitName) {
        return kits.containsKey(kitName);
    }

    public Kit get(String kitName) {
        return kits.get(kitName);
    }

    public void reload() {
        kits.clear();
        ConfigurationSection config = getConfig();
        for (Map.Entry<String, Object> kitEntry : config.getValues(false).entrySet()) {
            Kit kit = new Kit(kitEntry.getKey(), config);
            kits.put(kitEntry.getKey(), kit);
        }

    }

    public void save(String kitName, Player player) {
        Kit kit = get(kitName);
        if(kit == null)
            kit = new Kit(kitName);
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
