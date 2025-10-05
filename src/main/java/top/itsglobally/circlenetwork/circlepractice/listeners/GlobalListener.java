package top.itsglobally.circlenetwork.circlepractice.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;
import top.itsglobally.circlenetwork.circlepractice.achievement.Achievement;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.AutoListener;

@AutoListener
public class GlobalListener implements Listener, IListener {

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent e) {
        Player p = e.getPlayer();
        EntityDamageEvent event = p.getLastDamageCause();
        if (event != null && !event.isCancelled() && event instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
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
        plugin.getPlayerManager().getPlayer(e.getPlayer()).unlockAchievement(Achievement.JOIN);
        plugin.getPlayerManager().addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setFormat(MessageUtil.formatMessage(
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

}
