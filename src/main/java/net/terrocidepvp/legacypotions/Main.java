package net.terrocidepvp.legacypotions;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.terrocidepvp.legacypotions.commands.CommandManager;
import net.terrocidepvp.legacypotions.listeners.PotionEventListener;

public class Main extends JavaPlugin implements Listener {

    public static Main instance;
    public String version;
    public double versionAsDouble;

    public String getMCVersion() {
        String version = new String(Bukkit.getVersion());
        final int pos = version.indexOf("(MC: ");
        version = version.substring(pos + 5).replace(")", "");
        return version;
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled!");
    }

    @Override
    public void onEnable() {
        instance = this;

        getCommand("legacypotions").setExecutor(new CommandManager());
        getCommand("lp").setExecutor(new CommandManager());

        saveDefaultConfig();
        reloadConfig();
        getLogger().info("Checking if the config is broken...");
        if (!getConfig().isSet("configversion")) {
            getLogger().severe("The config.yml file is broken!");
            getLogger().severe("The plugin failed to detect a 'configversion'.");
            getLogger().severe(
                    "The plugin will not load until you generate a new, working config OR if you fix the config.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("The config is not broken.");
        getLogger().info("Checking if the config is outdated...");
        final int configVersion = 3;
        if (getConfig().getInt("configversion") != configVersion) {
            getLogger().severe("Your config is outdated!");
            getLogger()
                    .severe("The plugin will not load unless you change the config version to " + configVersion + ".");
            getLogger().severe(
                    "This means that you will need to reset your config, as there may have been major changes to the plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            getLogger().info("The config was not detected as outdated.");
        }

        version = getMCVersion();
        final String[] splitVersion = version.split("\\.");
        versionAsDouble = Double.parseDouble(splitVersion[0] + "." + splitVersion[1]);
        getLogger().info("Running Bukkit version " + version);

        getLogger().info("Loading potion listener...");
        new PotionEventListener(this);

        getLogger().info("Enabled!");
    }
}