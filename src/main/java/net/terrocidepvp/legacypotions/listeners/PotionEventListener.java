package net.terrocidepvp.legacypotions.listeners;

import net.terrocidepvp.legacypotions.LegacyPotions;
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

import java.util.Collection;

public class PotionEventListener implements Listener {

    private final Plugin plugin;
    private final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

    public PotionEventListener(final Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * The code below is not mine. It's the strength potion listener.
     *
     * https://github.com/MinelinkNetwork/LegacyStrength
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void calculateDamage(final EntityDamageByEntityEvent event) {
        // Check if the server version is 1.9 or above.
        if (LegacyPotions.getInstance().serverVersion <= 1.9)
            return;

        // Do nothing if strength fix isn't set to true.
        if (!LegacyPotions.getInstance().strengthFix)
            return;

        // Do nothing if the event is inherited (hacky way to ignore mcMMO AoE attacks).
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
        if (LegacyPotions.getInstance().healingFix || LegacyPotions.getInstance().regenerationFix) {
            final LivingEntity entity = (LivingEntity) event.getEntity();

            int level = 0;

            final Collection<PotionEffect> effects = entity.getActivePotionEffects();

            for (final PotionEffect effect : effects) {
                final PotionEffectType effectType = effect.getType();
                final int effectAmplifier = effect.getAmplifier();

                if (effectType == PotionEffectType.REGENERATION
                        || effectType == PotionEffectType.HEAL) {
                    level = effectAmplifier + 1;
                    break;
                }
            }

            final EntityRegainHealthEvent.RegainReason regainReason = event.getRegainReason();
            final double regainAmount = event.getAmount();
            if (regainReason == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN
                    && regainAmount == 1.0
                    && level > 0) {
                if (LegacyPotions.getInstance().regenerationFix) {
                    scheduler.runTaskLater(plugin, new Runnable() {

                        @Override
                        public void run() {
                            final boolean entityIsDead = entity.isDead();
                            if (entityIsDead)
                                return;

                            final double entityMaxHealth = entity.getMaxHealth();
                            final double entityHealth = entity.getHealth();

                            if (entityMaxHealth >= entityHealth + LegacyPotions.getInstance().extraHeartsPerLevel) {
                                entity.setHealth(entityHealth + LegacyPotions.getInstance().extraHeartsPerLevel);
                            }
                        }

                    }, 50L / (level * 2));
                }
            }

            else if (regainReason == EntityRegainHealthEvent.RegainReason.MAGIC
                    && regainAmount > 1.0
                    && level >= 0
                    && LegacyPotions.getInstance().healingFix) {
                event.setAmount(regainAmount * LegacyPotions.getInstance().healMultiplier);
            }
        }
    }
}
