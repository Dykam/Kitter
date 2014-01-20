package nl.dykam.dev;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.bukkit.permissions.Permissible;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class NestedKitManager implements KitManager {
    List<KitManager> managers;

    public NestedKitManager(KitManager... managers) {
        this.managers = new ArrayList<>(Arrays.asList(managers));
    }

    @Override
    public Iterable<Kit> getApplicable(Permissible permissible) {
        List<Kit> kits = new ArrayList<>();
        for (KitManager manager : managers) {
            for (Kit kit : manager.getApplicable(permissible)) {
                kits.add(kit);
            }
        }
        return kits;
    }

    @Override
    public boolean isApplicable(final Permissible permissible, final String kitName) {
        return Iterables.any(managers, new Predicate<KitManager>() {
            @Override
            public boolean apply(@Nullable KitManager kitManager) {
                assert kitManager != null;
                return kitManager.isApplicable(permissible, kitName);
            }
        });
    }

    @Override
    public boolean exists(final String kitName) {
        return Iterables.any(managers, new Predicate<KitManager>() {
            @Override
            public boolean apply(@Nullable KitManager kitManager) {
                assert kitManager != null;
                return kitManager.exists(kitName);
            }
        });
    }

    @Override
    public Kit get(final String kitName) {
        for (KitManager manager : managers) {
            Kit kit = manager.get(kitName);
            if(kit != null)
                return kit;
        }
        return null;
    }

    public void add(KitManager manager) {
        managers.add(manager);
    }

    public void remove(KitManager manager) {
        managers.remove(manager);
    }
}
