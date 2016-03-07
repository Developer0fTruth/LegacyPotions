package net.terrocidepvp.legacypotions.commands;

import net.terrocidepvp.legacypotions.Main;
import net.terrocidepvp.legacypotions.utils.ColorCodeUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdReload {
    // Carry over command sender data from the command manager.
    public static boolean onReload (CommandSender sender) {
        // Initialise the prefix.
        final String prefix = ColorCodeUtil.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("prefix"));
        
        // Set up the reload response.
        final String reloadResponse = Main.plugin.reloadPlugin();
        
        // Simple console check, in order to ensure that colour doesn't try coming up in console.
        if (!(sender instanceof Player)) {
            // Uses the logger instead of sendMessage.
            Bukkit.getLogger().info("Attempting to reload config...");
            Bukkit.getLogger().info(reloadResponse);
        } else {
            // This uses colour when sending messages to the player.
            sender.sendMessage(prefix + ChatColor.GRAY + "Attempting to reload config...");
            sender.sendMessage(prefix + ChatColor.WHITE + reloadResponse);
        }
        return true;
    }
}
