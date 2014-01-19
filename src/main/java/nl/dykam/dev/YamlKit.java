package nl.dykam.dev;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

import java.util.List;

class YamlKit extends Kit {
    public YamlKit(String name, ConfigurationSection config) {
        super(name);
        load(config);
    }

    public YamlKit(String name) {
        super(name);
    }

    public void load(ConfigurationSection config) {
        config = config.getConfigurationSection(name);
        if(config != null) {
            ItemStack[] contents = getItemStacksFromConfig(config, "contents");
            ItemStack[] armor = getItemStacksFromConfig(config, "armor");
            set(contents, armor);
        }
    }

    public void save(ConfigurationSection config) {
        config.set(name + ".contents", getContents());
        config.set(name + ".armor", getArmor());
    }

    @SuppressWarnings("unchecked")
    private ItemStack[] getItemStacksFromConfig(ConfigurationSection config, String name) {
        List<ItemStack> list = (List<ItemStack>)config.get(name);
        return list.toArray(new ItemStack[list.size()]);
    }

    public boolean isApplicable(Permissible permissible) {
        return permissible.hasPermission("kitter.kit.*") || permissible.hasPermission("kitter.kit." + name);
    }
}
