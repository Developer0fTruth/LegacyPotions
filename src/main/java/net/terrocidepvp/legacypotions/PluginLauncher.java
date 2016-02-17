package net.terrocidepvp.legacypotions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.terrocidepvp.legacypotions.commands.CommandManager;
import net.terrocidepvp.legacypotions.listeners.PotionEventListener;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

// RSL's simple plugin loader. It may not be the most efficient, but you can always give me examples.
// It's not exactly easy to find good sample code I can base the main class off.

// It's simple enough to read, so I won't waste my time annotating all of the code below.

public class PluginLauncher extends JavaPlugin implements Listener {
    // Set up the configuration file variables.
    FileConfiguration config;
    private static YamlConfiguration conf;
    private File confFile = new File(getDataFolder(), "config.yml");
    // Allow other bits of code to use "plugin".
    public static PluginLauncher plugin; //create the variable
    // Allow other classes to access the Main class.
    private static PluginLauncher instance;
    public static PluginLauncher getInstance() {
        return PluginLauncher.instance;
    }
    public void onEnable() {
        plugin = this; //assign plugin to this class
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