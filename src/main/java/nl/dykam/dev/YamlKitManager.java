package nl.dykam.dev;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class YamlKitManager implements KitManager {
    protected HashMap<String, YamlKit> kits = new HashMap<>();

    @Override
    public final Iterable<Kit> getApplicable(Permissible permissible) {
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
    public final boolean exists(String kitName) {
        return kits.containsKey(kitName);
    }

    @Override
    public final YamlKit get(String kitName) {
        return kits.get(kitName);
    }

    public abstract void reload();

    public abstract void save(String kitName, Player player);

    public final void remove(String kitName) {
        getConfig().set(kitName, null);
        kits.remove(kitName);
    }

    protected abstract ConfigurationSection getConfig();
}
