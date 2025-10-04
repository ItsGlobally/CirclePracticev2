package top.itsglobally.circlenetwork.circlepractice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.nontage.nontagelib.annotations.AutoListener;

@AutoListener
public class GameListener implements Listener, IListener {
    @EventHandler
    public void hunger(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        PracticePlayer pp = plugin.getPm().getPlayer(p);
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
}
