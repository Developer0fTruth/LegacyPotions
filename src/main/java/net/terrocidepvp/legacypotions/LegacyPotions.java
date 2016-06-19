package net.terrocidepvp.legacypotions;

import net.terrocidepvp.legacypotions.listeners.PotionEventListener;
import net.terrocidepvp.legacypotions.utils.VersionUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class LegacyPotions extends JavaPlugin {
    public int[] serverVersion;
    public boolean strengthFix;
    public boolean healingFix;
    public boolean regenerationFix;
    public double healMultiplier;
    public double extraHeartsPerLevel;
    public int damagePerLevel;

    private static LegacyPotions instance;
    public static LegacyPotions getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
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
        int configVersion = 3;
        if (getConfig().getInt("configversion") != configVersion) {
            getLogger().severe("Your config is outdated!");
            getLogger().severe("The plugin will not load unless you change the config version to " + configVersion + ".");
            getLogger().severe("This means that you will need to reset your config, as there may have been major changes to the plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // Get values from config.
        // TODO Change over to a Config object when you have time.
        strengthFix = getConfig().getBoolean("legacymode.strength.enabled");
        healingFix = getConfig().getBoolean("legacymode.healing.enabled");
        regenerationFix = getConfig().getBoolean("legacymode.regeneration.enabled");
        healMultiplier = getConfig().getDouble("legacymode.healing.healmultiplier");
        extraHeartsPerLevel = getConfig().getInt("legacymode.regeneration.extraheartsperlevel");
        damagePerLevel = getConfig().getInt("legacymode.strength.damageperlevel");

        // Get the Minecraft server version.
        serverVersion = VersionUtil.getMCVersion(getServer().getVersion());
        getLogger().info("Running server version " + Integer.toString(serverVersion[0]) + "." + Integer.toString(serverVersion[1]));

        // Load listeners.
        new PotionEventListener(this);
    }
}
