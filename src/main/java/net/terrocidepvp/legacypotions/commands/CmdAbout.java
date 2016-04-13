package net.terrocidepvp.legacypotions.commands;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import net.terrocidepvp.legacypotions.Main;
import net.terrocidepvp.legacypotions.utils.ColorCodeUtil;

public class CmdAbout {

    public static boolean onAbout(final CommandSender sender) {
        final PluginDescriptionFile pdf = Main.instance.getDescription();
        final String version = pdf.getVersion();
        final String uid = "%%__USER__%%";
        final String rid = "%%__RESOURCE__%%";
        final String nonce = "%%__NONCE__%%";
        final String prefix = ColorCodeUtil.translateAlternateColorCodes('&',
                Main.instance.getConfig().getString("prefix"));

        if (!(sender instanceof Player)) {
            final Logger logger = Bukkit.getLogger();
            logger.info("Running LegacyPotions v" + version + " by Terrobility.");
            logger.info("Registered to: https://www.spigotmc.org/members/" + uid + "/");
            logger.info("API lookup link (is purchased): http://www.spigotmc.org/api/resource.php?user_id=" + uid
                    + "&resource_id=" + rid + "once=" + nonce);
        } else {
            sender.sendMessage(prefix + ChatColor.GRAY + "Running LegacyPotions v" + version + " by Terrobility.");
            sender.sendMessage(prefix + ChatColor.DARK_RED + "Registered to: " + ChatColor.RED
                    + "https://www.spigotmc.org/members/" + uid + "/");
            sender.sendMessage(prefix + ChatColor.GRAY + "API lookup link (is purchased): " + ChatColor.DARK_GRAY
                    + "http://www.spigotmc.org/api/resource.php?user_id=" + uid + "&resource_id=" + rid + "once="
                    + nonce);
        }
        return true;
    }
}