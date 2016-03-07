package net.terrocidepvp.legacypotions.listeners;

import java.util.Collection;

import net.terrocidepvp.legacypotions.Main;
import net.terrocidepvp.legacypotions.handlers.StrengthHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public class PotionEventListener implements Listener {
    final private Plugin plugin;
    // Load some data from the configuration file.
    final boolean strengthFix = Main.plugin.getConfig().getBoolean("legacymode.strength.enabled");
    final boolean healingFix = Main.plugin.getConfig().getBoolean("legacymode.healing.enabled");
    final boolean regenerationFix = Main.plugin.getConfig().getBoolean("legacymode.regeneration.enabled");
    final double healMultiplier = Main.plugin.getConfig().getDouble("legacymode.healing.healmultiplier");
    final double extraHeartsPerLevel = Main.plugin.getConfig().getInt("legacymode.regeneration.extraheartsperlevel");

    // Set up the scheduler.
    final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

    // Constructor in order to enable this as a listener.
    public PotionEventListener(final Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents((Listener)this, this.plugin);
    }

    // Healing and regeneration potion listeners.
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onRegainHealth(final EntityRegainHealthEvent event) {
        // Check if either one of these bad boys are set to true.
        if (healingFix
                || regenerationFix) {
            // Store information on the entity that regained health in the event.
            final LivingEntity entity = (LivingEntity)event.getEntity();

            // Set the level to 0, this will be useful later on.
            int level = 0;

            // Set up a Collection for the potion effects on the player.
            final Collection<PotionEffect> effects = (Collection<PotionEffect>)entity.getActivePotionEffects();

            // Iterate through the potion effects in the Collection.
            for (final PotionEffect effect : effects) {
                final String effectName = effect.getType().getName();
                final int effectAmplifier = effect.getAmplifier();
                // Check for regeneration or healing potion.
                if (effectName == "REGENERATION"
                        || effectName == "HEAL") {
                    // Add 1 to the amplifier of the potion.
                    level = effectAmplifier + 1;
                    // Go back and continue with the code.
                    break;
                }
            }

            final EntityRegainHealthEvent.RegainReason regainReason = event.getRegainReason();
            final double regainAmount = event.getAmount();
            // Check if health gained was because of regeneration.
            if (regainReason == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN
                    && regainAmount == 1.0
                    && level > 0) {
                // Check if RegenerationFix is enabled in the config.
                if (regenerationFix) {
                    // Get the scheduler to run a sync task.
                    scheduler.runTaskLater(this.plugin, (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            // Check if the entity is dead.
                            final boolean entityIsDead = entity.isDead();
                            if (entityIsDead) return;
                            // Check if the max health is greater than or equal to the entity's health + 1.
                            final double entityMaxHealth = entity.getMaxHealth();
                            final double entityHealth = entity.getHealth();
                            if (entityMaxHealth >= entityHealth + extraHeartsPerLevel) {
                                // This would add an extra heart per level.
                                entity.setHealth(entityHealth + extraHeartsPerLevel);
                            }
                        }
                    // Remember how the regeneration potion duration was half? This sorts it out.
                    }, 50L / (level * 2));
                }
            }


            // If it's not those, it's got to be healing potions. Check if it was indeed a healing potion.
            else if (regainReason == EntityRegainHealthEvent.RegainReason.MAGIC
                    && regainAmount > 1.0
                    && level >= 0
                    && healingFix) {
                // Multiply the amount of hearts the healing potion gives you by 1.5, changing the base healing from 2 to 3.
                event.setAmount(regainAmount * healMultiplier);
            }
        }
    }

    /**
     * The code below is not mine. It's the strength potion listener.
     *
     * https://github.com/MinelinkNetwork/LegacyStrength
     * @author Byteflux
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void calculateDamage(EntityDamageByEntityEvent event) {
        // Check if the server version is 1.9 or above.
        if (Main.versionAsDouble >= 1.9) return;

        // Do nothing if strength fix isn't set to true.
        if (!strengthFix) return;

        // Do nothing if the event is inherited (hacky way to ignore mcMMO AoE attacks).
        if (event.getClass() != EntityDamageByEntityEvent.class) return;

        // Do nothing if the cause of damage isn't from an entity attack.
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        // Do nothing if the damage is not directly from a player.
        Entity entity = event.getDamager();
        if (!(entity instanceof Player)) return;

        // Do nothing if the damaged entity is not a player.
        if (!(event.getEntity() instanceof Player)) return;

        // Do nothing if the player doesn't have the Strength effect.
        Player player = (Player) event.getDamager();
        if (!player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) return;

        // Convert the old damage into a new damage with reduced Strength.
        event.setDamage(StrengthHandler.convertDamage(player, event.getDamage()));
    }
}
