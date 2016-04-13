package net.terrocidepvp.legacypotions.listeners;

import java.util.Collection;

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

import net.terrocidepvp.legacypotions.Main;
import net.terrocidepvp.legacypotions.handlers.StrengthHandler;

public class PotionEventListener implements Listener {

    final Plugin plugin;
    final boolean strengthFix = Main.instance.getConfig().getBoolean("legacymode.strength.enabled");
    final boolean healingFix = Main.instance.getConfig().getBoolean("legacymode.healing.enabled");
    final boolean regenerationFix = Main.instance.getConfig().getBoolean("legacymode.regeneration.enabled");
    final double healMultiplier = Main.instance.getConfig().getDouble("legacymode.healing.healmultiplier");
    final double extraHeartsPerLevel = Main.instance.getConfig().getInt("legacymode.regeneration.extraheartsperlevel");
    final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

    public PotionEventListener(final Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    /**
     * The code below is not mine. It's the strength potion listener.
     *
     * https://github.com/MinelinkNetwork/LegacyStrength
     *
     * @author Byteflux
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void calculateDamage(final EntityDamageByEntityEvent event) {
        // Check if the server version is 1.9 or above.
        if (!(Main.instance.versionAsDouble <= 1.8))
            return;

        // Do nothing if strength fix isn't set to true.
        if (!strengthFix)
            return;

        // Do nothing if the event is inherited (hacky way to ignore mcMMO AoE
        // attacks).
        if (event.getClass() != EntityDamageByEntityEvent.class)
            return;

        // Do nothing if the cause of damage isn't from an entity attack.
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK)
            return;

        // Do nothing if the damage is not directly from a player.
        final Entity entity = event.getDamager();
        if (!(entity instanceof Player))
            return;

        // Do nothing if the damaged entity is not a player.
        if (!(event.getEntity() instanceof Player))
            return;

        // Do nothing if the player doesn't have the Strength effect.
        final Player player = (Player) event.getDamager();
        if (!player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
            return;

        // Convert the old damage into a new damage with reduced Strength.
        event.setDamage(StrengthHandler.convertDamage(player, event.getDamage()));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onRegainHealth(final EntityRegainHealthEvent event) {
        if (healingFix || regenerationFix) {
            final LivingEntity entity = (LivingEntity) event.getEntity();

            int level = 0;

            final Collection<PotionEffect> effects = entity.getActivePotionEffects();

            for (final PotionEffect effect : effects) {
                final String effectName = effect.getType().getName();
                final int effectAmplifier = effect.getAmplifier();

                if (effectName == "REGENERATION" || effectName == "HEAL") {
                    level = effectAmplifier + 1;
                    break;
                }
            }

            final EntityRegainHealthEvent.RegainReason regainReason = event.getRegainReason();
            final double regainAmount = event.getAmount();
            if (regainReason == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN && regainAmount == 1.0 && level > 0) {
                if (regenerationFix) {
                    scheduler.runTaskLater(plugin, new Runnable() {

                        @Override
                        public void run() {
                            final boolean entityIsDead = entity.isDead();
                            if (entityIsDead)
                                return;

                            final double entityMaxHealth = entity.getMaxHealth();
                            final double entityHealth = entity.getHealth();

                            if (entityMaxHealth >= entityHealth + extraHeartsPerLevel) {
                                entity.setHealth(entityHealth + extraHeartsPerLevel);
                            }
                        }

                    }, 50L / (level * 2));
                }
            }

            else if (regainReason == EntityRegainHealthEvent.RegainReason.MAGIC && regainAmount > 1.0 && level >= 0
                    && healingFix) {
                event.setAmount(regainAmount * healMultiplier);
            }
        }
    }
}
