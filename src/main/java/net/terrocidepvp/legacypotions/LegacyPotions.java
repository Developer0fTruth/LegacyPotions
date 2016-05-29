package net.terrocidepvp.legacypotions;

import net.terrocidepvp.legacypotions.listeners.PotionEventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LegacyPotions extends JavaPlugin {

    private static LegacyPotions instance;
    public static LegacyPotions getInstance() {
        return instance;
    }

    public double serverVersion;

    public final boolean strengthFix = getConfig().getBoolean("legacymode.strength.enabled");
    public final boolean healingFix = getConfig().getBoolean("legacymode.healing.enabled");
    public final boolean regenerationFix = getConfig().getBoolean("legacymode.regeneration.enabled");
    public final double healMultiplier = getConfig().getDouble("legacymode.healing.healmultiplier");
    public final double extraHeartsPerLevel = getConfig().getInt("legacymode.regeneration.extraheartsperlevel");

    @Override
    public void onEnable() {
        instance = this;

        // Make sure config exists if not already existing.
        saveDefaultConfig();
        reloadConfig();

        // Broken config check.
        if (!getConfig().isSet("configversion")) {
            getLogger().severe("The config.yml file is broken!");
            getLogger().severe("The plugin failed to detect a 'configversion'.");
            getLogger().severe(
                    "The plugin will not load until you generate a new, working config OR if you fix the config.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Outdated config check.
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

        // Get the Minecraft server version.
        serverVersion = getMCVersion();
        getLogger().info("Running server version " + serverVersion);

        // Load listeners.
        new PotionEventListener(this);
    }

    private double getMCVersion() {
        // Get version from Bukkit.
        String version = new String(Bukkit.getVersion());
        final int pos = version.indexOf("(MC: ");
        // Clean it up to get the numbers.
        version = version.substring(pos + 5).replace(")", "");
        // Parse as a double.
        final String[] splitVersion = version.split("\\.");
        return Double.parseDouble(splitVersion[0] + "." + splitVersion[1]);
    }
}
