package top.itsglobally.circlenetwork.circlepractice.managers;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import top.itsglobally.circlenetwork.circlepractice.data.*;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;

public class GameManager extends Managers{

    public GameManager() {

    }
    public void processNewGame(Player p1, Player p2, Kit kit) {
        GameArena ga = null;
        for (GameArena gas : plugin.getCm().getGameArenas()) {
            if (!gas.isInUse()) {
                ga = gas;
                return;
            }
            MessageUtil.sendMessage(p1, p2, "&cNo arenas are available now!");
            return;
        }
        startNewGame(plugin.getPm().getPlayer(p1), plugin.getPm().getPlayer(p2), kit, ga);
    }
    public void startNewGame(PracticePlayer pp1, PracticePlayer pp2, Kit kit, GameArena arena) {
        Game game = new Game(pp1, pp2, kit, arena);
        Player p1 = game.getPlayer1().getPlayer();
        Player p2 = game.getPlayer2().getPlayer();
        p1.teleport(game.getArena().getPos1());
        p2.teleport(game.getArena().getPos2());
        game.setState(GameStete.STARTING);
        game.getArena().setInUse(true);
        startCooldown(game);
    }
    public void startCooldown(Game game) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (game.getState() != GameStete.STARTING) {
                    cancel();
                    return;
                }
                Player p1 = Bukkit.getPlayer(game.getPlayer1().getUuid());
                Player p2 = Bukkit.getPlayer(game.getPlayer2().getUuid());


                if (p1 == null || p2 == null) {
                    cancel();
                    return;
                }

                int countdown = game.getCountdown();
                if (countdown > 0) {
                    MessageUtil.sendMessage(p1, "&eDuel starting in &c" + countdown + "&e...");
                    MessageUtil.sendMessage(p2, "&eDuel starting in &c" + countdown + "&e...");
                    p1.playSound(p1.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                    p2.playSound(p2.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                    game.setCountdown(countdown - 1);
                } else {
                    MessageUtil.sendMessage(p1, "&aFight!");
                    MessageUtil.sendMessage(p2, "&aFight!");
                    p1.playSound(p1.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
                    p2.playSound(p2.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
                    game.setState(GameStete.ONGOING);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 0L);
    }
}
