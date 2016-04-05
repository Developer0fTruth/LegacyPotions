package net.terrocidepvp.legacypotions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.terrocidepvp.legacypotions.commands.CommandManager;
import net.terrocidepvp.legacypotions.listeners.PotionEventListener;

public class Main extends JavaPlugin implements Listener {

    FileConfiguration config;
    private static YamlConfiguration conf;
    private File confFile = new File(getDataFolder(), "config.yml");
    public static Main instance;
    public static Main getInstance() {
        return Main.instance;
    }

    public static String version;
    public static double versionAsDouble;

    public void onEnable() {
        instance = this;
        getCommand("legacypotions").setExecutor(new CommandManager());
        getCommand("lp").setExecutor(new CommandManager());
        setupConfig();
        getLogger().info("Checking if the config is broken...");
        if (!getConfig().isSet("configversion")) {
            getLogger().severe("The config.yml file is broken!");
            getLogger().severe("The plugin failed to detect a 'configversion'.");
            getLogger().severe("The plugin will not load until you generate a new, working config OR if you fix the config.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("The config is not broken.");
        getLogger().info("Checking if the config is outdated...");
        // Add version info here when you change the config version to 4.
        final int configVersion = 3;
        if (getConfig().getInt("configversion") != configVersion) {
            getLogger().severe("Your config is outdated!");
            getLogger().severe("The plugin will not load unless you change the config version to " + configVersion + ".");
            getLogger().severe("This means that you will need to reset your config, as there may have been major changes to the plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            getLogger().info("The config was not detected as outdated.");
        }
        final String[] splitVersion = getServer().getBukkitVersion().trim().split("-");
        version = splitVersion[0];
        getLogger().info("Running Bukkit version " + version);
        final String[] versionSplitAgain = version.split("\\.");
        versionAsDouble = Double.parseDouble(versionSplitAgain[0] + "." + versionSplitAgain[1]);
        getLogger().info("I'll reference to your Bukkit version as " + Double.toString(versionAsDouble) + " since that's a valid number format.");
        getLogger().info("Loading potion listener...");
        new PotionEventListener((Plugin)this);
        getLogger().info("Enabled!");
    }

    public void onDisable()
    {
        getLogger().info("Disabled!");
    }

    private void setupConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Couldn't find a configuration file. Attempting to make one now.");
                saveDefaultConfig();
                reloadPlugin();
            } else {
                getLogger().info("Configuration file found.");
            }
        } catch (Exception e) {
            getLogger().severe("*** STACK TRACE START ***");
            e.printStackTrace();
            getLogger().severe("*** STACK TRACE END ***");
        }
    }

    public String reloadPlugin() {
        conf = new YamlConfiguration();
        try {
            conf.load(confFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "File not found!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Couldn't read file!";
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return "Invalid configuration!";
        }
        reloadConfig();
        return "Success!";
    }
}
