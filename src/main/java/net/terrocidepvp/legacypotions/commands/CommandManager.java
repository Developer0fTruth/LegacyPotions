package net.terrocidepvp.legacypotions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!cmd.getName().equalsIgnoreCase("legacypotions") && !cmd.getName().equalsIgnoreCase("lp"))
            return false;

        if (args.length >= 1) {
            final String firstLetterFromArg = args[0].substring(0, 1);

            if (firstLetterFromArg.equalsIgnoreCase("a")) {
                CmdAbout.onAbout(sender);
            }
        }
        return true;
    }
}
