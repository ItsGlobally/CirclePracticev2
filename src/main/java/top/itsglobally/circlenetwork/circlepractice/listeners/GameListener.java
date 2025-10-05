package top.itsglobally.circlenetwork.circlepractice.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.itsglobally.circlenetwork.circlepractice.data.Game;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.nontage.nontagelib.annotations.AutoListener;

@AutoListener
public class GameListener implements Listener, IListener {
    @EventHandler
    public void hunger(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (pp.isInDuel()) {
            if (!pp.getCurrentGame().getKit().isHunger()) {
                e.setCancelled(true);
                return;
            }
        }
        if (pp.isInSpawn() || pp.isInFFA()) {
            e.setCancelled(true);

        }
    }
    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player vic)) return;
        PracticePlayer vicp = plugin.getPlayerManager().getPlayer(vic);
        if (vicp.isInDuel()) {
            Game game = vicp.getCurrentGame();
            if (vic.getHealth() < e.getFinalDamage()) {
                e.setCancelled(true);
                vic.setHealth(20.0);
                vic.setFoodLevel(20);
                Player winner = game.getOpponent(vicp).getPlayer();
                winner.setHealth(20.0);
                winner.setFoodLevel(20);
                plugin.getGameManager().endGame(game, game.getOpponent(vicp));
            }
        }
    }
    @EventHandler
    public void died(PlayerDeathEvent e) {
        Player p = e.getEntity();
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);

        if (pp.isInDuel()) {
            Game game = pp.getCurrentGame();
            e.setDeathMessage(null);
            e.getDrops().clear();
            e.setDroppedExp(0);
            p.setHealth(20.0);
            p.setFoodLevel(20);
            Player winner = game.getOpponent(pp).getPlayer();
            winner.setHealth(20.0);
            winner.setFoodLevel(20);
            plugin.getGameManager().endGame(game, game.getOpponent(pp));
        }
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (pp.isInDuel()) {
            plugin.getGameManager().endGame(pp.getCurrentGame(), pp.getCurrentGame().getOpponent(pp));
        }

        plugin.getPlayerManager().removePlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void bbreak(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());
        if (pp.isInDuel()) {
            Game game = pp.getCurrentGame();
            if (!game.getKit().isCanBuild()) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void place(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());
        if (pp.isInDuel()) {
            Game game = pp.getCurrentGame();
            if (!game.getKit().isCanBuild()) {
                e.setCancelled(true);
            }
        }
    }
}
