package top.itsglobally.circlenetwork.circlepractice.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import top.itsglobally.circlenetwork.circlepractice.achievement.Achievement;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.itsglobally.circlenetwork.circlepractice.utils.ScoreboardUtils;
import top.itsglobally.circlenetwork.circlepractice.utils.starUtils;
import top.nontage.nontagelib.annotations.AutoListener;

@AutoListener
public class GlobalListener implements Listener, GlobalInterface {

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent e) {
        Player p = e.getPlayer();
        EntityDamageEvent event = p.getLastDamageCause();
        if (event != null && !event.isCancelled() && event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            Entity damager = entityDamageByEntityEvent.getDamager();
            if (damager instanceof Arrow) {
                if (((Arrow) damager).getShooter().equals(p)) {
                    Vector velocity = e.getVelocity();
                    double speed = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());
                    Vector dir = damager.getLocation().getDirection().normalize();
                    Vector vector = new Vector(dir.getX() * speed * -1.0D, velocity.getY(), dir.getZ() * speed);
                    e.setVelocity(vector);
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ScoreboardUtils.lastScoreboardLines.remove(e.getPlayer().getUniqueId());
        plugin.getPlayerManager().addPlayer(e.getPlayer());
        plugin.getPlayerDataManager().getData(e.getPlayer()).unlockAchievement(Achievement.JOIN);
        plugin.getConfigManager().teleportToSpawn(e.getPlayer());
        if (e.getPlayer().hasPermission("circlepractice.fly")) {
            e.getPlayer().setFlying(true);
            e.getPlayer().setAllowFlight(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setFormat(MessageUtil.formatMessage(
                starUtils.getColoredStars(plugin.getPlayerDataManager().getData(e.getPlayer()).getStars()) + "&r " +
                        plugin.getPlayerManager().getPrefixedName(e.getPlayer()) +
                        "&r » %2$s"
        ));
    }

    @EventHandler
    public void died(PlayerDeathEvent e) {
        Player p = e.getEntity();
        p.spigot().respawn();
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();

        if (item.getType() == Material.POTION) {
            event.getPlayer().getServer().getScheduler().runTaskLater(
                    plugin,
                    () -> {
                        event.getPlayer().getInventory().removeItem(new ItemStack(Material.GLASS_BOTTLE, 1));
                    },
                    1L
            );
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) e.setCancelled(true);
        if (e.getEntity() instanceof Player p) {
            PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
            if (pp.isInSpawn()) e.setCancelled(true);
        }
    }

    @EventHandler
    public void bbreak(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());
        if (pp.isInSpawn()) e.setCancelled(true);
    }

    @EventHandler
    public void place(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());
        if (pp.isInSpawn()) e.setCancelled(true);
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());
        if (pp.isInSpawn()) {
            if (e.getTo().getY() <= 0) {
                plugin.getConfigManager().teleportToSpawn(e.getPlayer());
            }
        }
    }

}
