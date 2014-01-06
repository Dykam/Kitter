package nl.dykam.dev;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.List;

public class Kit {
    private String name;

    private ItemStack[] compressed;
    private ItemStack[] contents, armor;
    public Kit(String name, ConfigurationSection config) {
        this.name = name;
        load(config);
    }

    public Kit(String name) {
        this.name = name;
        compressed = armor = contents = new ItemStack[0];
    }

    public String getName() {
        return name;
    }

    public boolean isApplicable(Permissible permissible) {
        return permissible.hasPermission("kitter.kit.*") || permissible.hasPermission("kitter.kit." + name);
    }

    /**
     * Replaces the inventory with this kit.
     * @param player The holder of the inventory to apply it to
     */
    public void apply(Player player) {
        apply(player, true);
    }

    /**
     * Adds this kit to the inventory, or replaces it.
     * @param player The holder of the inventory to apply it to
     * @param clear Whether to clear the given inventory
     */
    public void apply(Player player, boolean clear) {
        apply(player.getInventory(), clear);
        player.updateInventory();
    }

    /**
     * Replaces the inventory with this kit.
     * @param inventory The inventory to apply it to
     */
    public void apply(PlayerInventory inventory) {
        apply(inventory, true);
    }

    /**
     * Adds this kit to the inventory, or replaces it.
     * @param inventory The inventory to apply it to
     * @param clear     Whether to clear the given inventory
     */
    public void apply(PlayerInventory inventory, boolean clear) {
        if(clear) {
            inventory.setContents(contents);
            inventory.setArmorContents(armor);
        } else {
            inventory.addItem(compressed);
        }
    }

    public void read(HumanEntity human) {
        read(human.getInventory());
    }

    public void read(PlayerInventory inventory) {
        contents = inventory.getContents();
        armor = inventory.getArmorContents();
        compress();
    }

    private void compress() {
        compressed = compress(contents, armor);
    }

    private static ItemStack[] compress(ItemStack[]... contents) {
        List<ItemStack> nonNullContents = new ArrayList<>();
        for(ItemStack[] subContents : contents) {
            for(ItemStack content : subContents) {
                if(content != null)
                    nonNullContents.add(content);
            }
        }
        return nonNullContents.toArray(new ItemStack[nonNullContents.size()]);
    }

    public void load(ConfigurationSection config) {
        config = config.getConfigurationSection(name);
        if(config != null) {
            contents = getItemStacksFromConfig(config, "contents");
            armor = getItemStacksFromConfig(config, "armor");
            compress();
        }
    }

    public void save(ConfigurationSection config) {
        config.set(name + ".contents", contents);
        config.set(name + ".armor", armor);
    }

    @SuppressWarnings("unchecked")
    private ItemStack[] getItemStacksFromConfig(ConfigurationSection config, String name) {
        List<ItemStack> list = (List<ItemStack>)config.get(name);
        return list.toArray(new ItemStack[list.size()]);
    }
}
