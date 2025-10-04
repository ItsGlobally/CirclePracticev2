package top.itsglobally.circlenetwork.circlepractice.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
    public void move(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (pp.isInDuel()) {
            Game game = pp.getCurrentGame();
            if (game.getPlayer1OrPlayer2(pp) == 1) {
                Location l = game.getArena().getPos1();
                p.teleport(new Location(e.getFrom().getWorld(), l.getX(), l.getY(), l.getZ()));
            } else {
                Location l = game.getArena().getPos2();
                p.teleport(new Location(e.getFrom().getWorld(), l.getX(), l.getY(), l.getZ()));
            }
        }
    }
}
