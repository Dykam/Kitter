package nl.dykam.dev;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.List;

public abstract class Kit {
    private ItemStack[] compressed;
    private ItemStack[] contents;
    private ItemStack[] armor;
    protected String name;

    public Kit(String name) {
        this.name = name;
        compressed = armor = contents = new ItemStack[0];
    }

    protected ItemStack[] getCompressed() {
        if(compressed == null)
            compress();
        return compressed;
    }

    protected ItemStack[] getContents() {
        return contents;
    }

    protected ItemStack[] getArmor() {
        return armor;
    }

    protected void set(ItemStack[] contents, ItemStack[] armor) {
        this.contents = contents;
        this.armor = armor;
        compressed = null;
    }

    public final String getName() {
        return name;
    }

    public abstract boolean isApplicable(Permissible permissible);

    /**
     * Replaces the inventory with this kit.
     * @param player The holder of the inventory to apply it to
     */
    public final void apply(Player player) {
        apply(player, true);
    }

    /**
     * Adds this kit to the inventory, or replaces it.
     * @param player The holder of the inventory to apply it to
     * @param clear Whether to clear the given inventory
     */
    public final void apply(Player player, boolean clear) {
        apply(player.getInventory(), clear);
        player.updateInventory();
    }

    /**
     * Replaces the inventory with this kit.
     * @param inventory The inventory to apply it to
     */
    public final void apply(PlayerInventory inventory) {
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
            inventory.addItem(getCompressed());
        }
    }

    public final void read(HumanEntity human) {
        read(human.getInventory());
    }

    public void read(PlayerInventory inventory) {
        contents = inventory.getContents();
        armor = inventory.getArmorContents();
        compressed = null;
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
}
