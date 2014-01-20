package nl.dykam.dev;

import org.bukkit.permissions.Permissible;

public interface KitManager {
    Iterable<Kit> getApplicable(Permissible permissible);

    boolean isApplicable(Permissible permissible, String kitName);

    boolean exists(String kitName);

    Kit get(String kitName);
}
