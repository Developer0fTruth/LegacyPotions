package net.terrocidepvp.legacypotions.commands;

import net.terrocidepvp.legacypotions.PluginLauncher;
import net.terrocidepvp.legacypotions.utils.ColorCodeUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class CmdAbout {
    final static PluginDescriptionFile pdf = PluginLauncher.plugin.getDescription();
    final static String version = pdf.getVersion();
    final static String uid = "%%__USER__%%";
    final static String rid = "%%__RESOURCE__%%";
    final static String nonce = "%%__NONCE__%%";
    // Carry over command sender data from the command manager.
    public static boolean onAbout (CommandSender sender) {
        // Initialise the prefix.
        String Prefix = ColorCodeUtil.translateAlternateColorCodes('&', PluginLauncher.plugin.getConfig().getString("prefix"));
        
        // Simple console check, in order to ensure that colour doesn't try coming up in console.
        if (!(sender instanceof Player)) {
            // Uses the logger instead of sendMessage.
            PluginLauncher.plugin.getLogger().info("Running LegacyPotions v" + version + " by HunterGPlays.");
            PluginLauncher.plugin.getLogger().info("Registered to: https://www.spigotmc.org/members/" + uid + "/");
            PluginLauncher.plugin.getLogger().info("API lookup link (is purchased): http://www.spigotmc.org/api/resource.php?user_id=" + uid + "&resource_id=" + rid + "once=" + nonce);
        } else {
            // This uses colour when sending messages to the player.
            sender.sendMessage(Prefix + ChatColor.GRAY + "Running LegacyPotions v" + version + " by HunterGPlays.");
            sender.sendMessage(Prefix + ChatColor.DARK_RED + "Registered to: " + ChatColor.RED + "https://www.spigotmc.org/members/" + uid + "/");
            sender.sendMessage(Prefix + ChatColor.GRAY + "API lookup link (is purchased): " + ChatColor.DARK_GRAY + "http://www.spigotmc.org/api/resource.php?user_id=" + uid + "&resource_id=" + rid + "once=" + nonce);
        }
        return true;
    }
}