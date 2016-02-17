package net.terrocidepvp.legacypotions.commands;

import net.terrocidepvp.legacypotions.PluginLauncher;
import net.terrocidepvp.legacypotions.utils.ColorCodeUtil;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor {
    // What to do when a player types a command.
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Initialise basic messages, including prefix and no permission message.
        final String prefix = ColorCodeUtil.translateAlternateColorCodes('&', PluginLauncher.plugin.getConfig().getString("prefix"));
        final String noPermission = ColorCodeUtil.translateAlternateColorCodes('&', PluginLauncher.plugin.getConfig().getString("nopermission"));
        
        // Checks for the right commands in case something happens.
        if (!cmd.getName().equalsIgnoreCase("legacypotions") && !cmd.getName().equalsIgnoreCase("lp")) {
            return false;
        }
        
        // Check for an args length that is greater than or equal to 1.
        if (args.length >= 1) {
            // Ensure the first letter is extracted.
            String firstLetterFromArg = args[0].substring(0, 1);
            // Make sure the argument starts with the letter "r".
            if (firstLetterFromArg.equalsIgnoreCase("r")) {
                // Check for a permission node.
                if (sender.hasPermission("legacypotions.reload")) {
                    // Call CmdReload.
                    CmdReload.onReload(sender);
                } else {
                    // Output the no permission message otherwise.
                    sender.sendMessage(prefix + noPermission);
                }
            }
            // Make sure the argument starts with the letter "a".
            if (firstLetterFromArg.equalsIgnoreCase("a")) {
                // Call CmdAbout.
                CmdAbout.onAbout(sender);
            }
        }
        return true;
    }
}
