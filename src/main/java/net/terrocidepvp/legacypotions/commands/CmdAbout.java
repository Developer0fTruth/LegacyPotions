package net.terrocidepvp.legacypotions.commands;

import net.terrocidepvp.legacypotions.PluginLauncher;
import net.terrocidepvp.legacypotions.utils.ColorCodeUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class CmdAbout {
    // Carry over command sender data from the command manager.
    public static boolean onAbout (CommandSender sender) {
        final PluginDescriptionFile pdf = PluginLauncher.plugin.getDescription();
        final String version = pdf.getVersion();
        final String uid = "%%__USER__%%";
        final String rid = "%%__RESOURCE__%%";
        final String nonce = "%%__NONCE__%%";
        // Initialise the prefix.
        final String prefix = ColorCodeUtil.translateAlternateColorCodes('&', PluginLauncher.plugin.getConfig().getString("prefix"));
        
        // Simple console check, in order to ensure that colour doesn't try coming up in console.
        if (!(sender instanceof Player)) {
            // Uses the logger instead of sendMessage.
            Bukkit.getLogger().info("Running LegacyPotions v" + version + " by Terrobility.");
            Bukkit.getLogger().info("Registered to: https://www.spigotmc.org/members/" + uid + "/");
            Bukkit.getLogger().info("API lookup link (is purchased): http://www.spigotmc.org/api/resource.php?user_id=" + uid + "&resource_id=" + rid + "once=" + nonce);
        } else {
            // This uses colour when sending messages to the player.
            sender.sendMessage(prefix + ChatColor.GRAY + "Running LegacyPotions v" + version + " by Terrobility.");
            sender.sendMessage(prefix + ChatColor.DARK_RED + "Registered to: " + ChatColor.RED + "https://www.spigotmc.org/members/" + uid + "/");
            sender.sendMessage(prefix + ChatColor.GRAY + "API lookup link (is purchased): " + ChatColor.DARK_GRAY + "http://www.spigotmc.org/api/resource.php?user_id=" + uid + "&resource_id=" + rid + "once=" + nonce);
        }
        return true;
    }
}