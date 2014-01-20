package nl.dykam.dev;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

class KitterPlugin extends JavaPlugin {
    YamlKitManager yamlKits;
    NestedKitManager kits;
    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        yamlKits = new YamlKitManager(this, "yamlKits");
        kits = new NestedKitManager();
        kits.add(yamlKits);
        saveConfig();
        Bukkit.getPluginManager().registerEvents(new ServiceListener(kits), this);
        for (RegisteredServiceProvider<KitManager> provider : Bukkit.getServicesManager().getRegistrations(KitManager.class)) {
            kits.add(provider.getProvider());
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) { // List
            if(sender.hasPermission("kitter.command.list")) {
                List<String> names = new ArrayList<>();
                for(Kit kit : kits.getApplicable(sender)) {
                    names.add(kit.getName());
                }
                if(names.isEmpty()) {
                    sendMessage(sender, "There are no kits for you.");
                } else {
                    sendMessage(sender, "Your kits: " + StringUtils.join(names, ", "));
                }
                return true;
            }
        } else if(args.length == 1) {
            if(args[0].equals("reload")) {
                if (!sender.hasPermission("kitter.command.reload")) {
                    showUsage(sender);
                    return true;
                }
                kits.reload();
                sendMessage(sender, "Reloaded the kits.");
                return true;
            }
        } if(args.length == 2) {
            if (!kits.isApplicable(sender, args[0])) {
                sendMessage(sender, "You don't have permissions for this kit!");
                return true;
            }
            switch (args[1]) {
                case "apply":
                    if (!sender.hasPermission("kitter.command.apply")) {
                        showUsage(sender);
                        return true;
                    }
                    if (!(sender instanceof Player)) {
                        sendMessage(sender, "You can only apply a kit to a player!");
                        return true;
                    }
                    Kit kit = kits.get(args[0]);
                    if(kit == null) {
                        sendMessage(sender, "Kit " + ChatColor.UNDERLINE + args[0] + ChatColor.RESET + " does not exist!");
                        return true;
                    }
                    kit.apply((Player) sender);
                    sendMessage(sender, "Kit " + ChatColor.UNDERLINE + kit.getName() + ChatColor.RESET + " applied");
                    return true;
                case "save":
                    if (!sender.hasPermission("kitter.command.save")) {
                        sendMessage(sender, "You don't have permissions to save kits!");
                        return true;
                    }
                    if (!(sender instanceof Player)) {
                        sendMessage(sender, "You can only save a kit as a player!");
                        return true;
                    }
                    yamlKits.save(args[0], (Player) sender);
                    sendMessage(sender, "Kit " + ChatColor.UNDERLINE + args[0] + ChatColor.RESET + " saved");
                    return true;
                case "remove":
                    if (!sender.hasPermission("kitter.command.remove")) {
                        sendMessage(sender, "You don't have permissions to remove kits!");
                        return true;
                    }
                    yamlKits.remove(args[0]);
                    sendMessage(sender, "Kit " + ChatColor.UNDERLINE + args[0] + ChatColor.RESET + " removed");
                    return true;
            }
        } else if(args.length == 3) {
            if(args[1].equals("apply")) {
                if (!sender.hasPermission("kitter.command.apply.other")) {
                    showUsage(sender);
                    return true;
                }
                Player player = Bukkit.getPlayerExact(args[2]);
                if(player == null) {
                    sendMessage(sender, args[2] + " does not exist!");
                    return true;
                }
                Kit kit = kits.get(args[0]);
                if(kit == null) {
                    sendMessage(sender, "Kit " + ChatColor.UNDERLINE + args[0] + ChatColor.RESET + " does not exist!");
                    return true;
                }
                if(!kit.isApplicable(sender)) {
                    sendMessage(sender, "You don't have permissions for this kit!");
                    return true;
                }
                kit.apply(player);
                sendMessage(sender, "Kit " + ChatColor.UNDERLINE + kit.getName() + ChatColor.RESET + " applied");
                return true;
            }
        }
        showUsage(sender);
        return true;
    }

    private void showUsage(CommandSender sender) {
        List<String> message = new ArrayList<>();
        if(sender.hasPermission("kitter.command.list"))
            message.add("/kit§7 -- Shows your kits");
        if(sender.hasPermission("kitter.command.apply"))
            message.add("/kit <kit> apply§7 -- Applies <kit>");
        if(sender.hasPermission("kitter.command.apply.other"))
            message.add("/kit <kit> apply <player>§7 -- Applies <kit> to <player>");
        if(sender.hasPermission("kitter.command.save"))
            message.add("/kit <kit> save§7 -- Saves your inventory as <kit>");
        if(sender.hasPermission("kitter.command.remove"))
            message.add("/kit <kit> remove§7 -- Removes <kit> from configuration");
        if(sender.hasPermission("kitter.command.reload"))
            message.add("/kit reload§7 -- Reloads the kits");
        if(message.size() == 0) {
            sendMessage(sender, "You don't have permission so see your kits");
        } else {
            sendMessage(sender, message.toArray(new String[message.size()]));
        }
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GOLD + "[Kitter] " + ChatColor.RESET + message);
    }

    private void sendMessage(CommandSender sender, String... messages) {
        for(String message : messages) {
            sendMessage(sender, message);
        }
    }
}
