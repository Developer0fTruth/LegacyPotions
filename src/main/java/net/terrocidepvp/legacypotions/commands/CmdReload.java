package net.terrocidepvp.legacypotions.commands;

import net.terrocidepvp.legacypotions.PluginLauncher;
import net.terrocidepvp.legacypotions.utils.ColorCodeUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdReload {
    // Carry over command sender data from the command manager.
    public static boolean onReload (CommandSender sender) {
        // Initialise the prefix.
        String Prefix = ColorCodeUtil.translateAlternateColorCodes('&', PluginLauncher.plugin.getConfig().getString("prefix"));
        
        // Set up the reload response.
        String ReloadResponse = PluginLauncher.plugin.reloadPlugin();
        
        // Simple console check, in order to ensure that colour doesn't try coming up in console.
        if (!(sender instanceof Player)) {
            // Uses the logger instead of sendMessage.
            PluginLauncher.plugin.getLogger().info("Attempting to reload config...");
            PluginLauncher.plugin.getLogger().info(ReloadResponse);
        } else {
            // This uses colour when sending messages to the player.
            sender.sendMessage(Prefix + ChatColor.GRAY + "Attempting to reload config...");
            sender.sendMessage(Prefix + ChatColor.WHITE + ReloadResponse);
        }
        return true;
    }
}
