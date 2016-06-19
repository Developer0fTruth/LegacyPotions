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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;

public class PotionEventListener implements Listener {
    private LegacyPotions plugin;
    private BukkitScheduler scheduler;

    public PotionEventListener(LegacyPotions plugin) {
        this.plugin = plugin;
        scheduler = plugin.getServer().getScheduler();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * The code below is not mine. It's the strength potion listener.
     *
     * https://github.com/MinelinkNetwork/LegacyStrength
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void calculateDamage(EntityDamageByEntityEvent event) {
        // Check if the server version is 1.9 or above.
        if (plugin.serverVersion[0] == 1
                && plugin.serverVersion[1] >= 9) return;

        // Do nothing if strength fix isn't set to true.
        if (!plugin.strengthFix) return;

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
        if (!player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
            return;

        // Convert the old damage into a new damage with reduced Strength.
        event.setDamage(StrengthHandler.convertDamage(player, event.getDamage()));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onRegainHealth(EntityRegainHealthEvent event) {
        if (plugin.healingFix || plugin.regenerationFix) {
            final LivingEntity entity = (LivingEntity) event.getEntity();
            int level = 0;
            Collection<PotionEffect> effects = entity.getActivePotionEffects();

            for (PotionEffect effect : effects) {
                PotionEffectType effectType = effect.getType();
                int effectAmplifier = effect.getAmplifier();

                if (effectType == PotionEffectType.REGENERATION
                        || effectType == PotionEffectType.HEAL) {
                    level = effectAmplifier + 1;
                    break;
                }
            }

            EntityRegainHealthEvent.RegainReason regainReason = event.getRegainReason();
            double regainAmount = event.getAmount();
            if (regainReason == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN
                    && regainAmount == 1.0
                    && level > 0) {
                if (plugin.regenerationFix) {
                    scheduler.runTaskLater(plugin, () -> {
                        boolean entityIsDead = entity.isDead();
                        if (entityIsDead)
                            return;

                        double entityMaxHealth = entity.getMaxHealth();
                        double entityHealth = entity.getHealth();

                        if (entityMaxHealth >= entityHealth + plugin.extraHeartsPerLevel) {
                            entity.setHealth(entityHealth + plugin.extraHeartsPerLevel);
                        }
                    }, 50L / (level * 2));
                }
            }

            else if (regainReason == EntityRegainHealthEvent.RegainReason.MAGIC
                    && regainAmount > 1.0
                    && level >= 0
                    && plugin.healingFix) {
                event.setAmount(regainAmount * plugin.healMultiplier);
            }
        }
    }
}
