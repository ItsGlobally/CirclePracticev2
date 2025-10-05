package top.itsglobally.circlenetwork.circlepractice.managers;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import top.itsglobally.circlenetwork.circlepractice.achievement.Achievement;
import top.itsglobally.circlenetwork.circlepractice.data.*;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;

import java.util.*;

public class GameManager extends Managers {
    private final Map<Player, List<DuelRequest>> duelRequests;
    private final Map<UUID, Game> games;


    public GameManager() {
        games = new HashMap<>();
        duelRequests = new HashMap<>();
    }

    public void sendDuelRequest(Player p1, Player p2, String kit) {
        if (!plugin.getKitManager().kitAlreadyExist(kit)) {
            MessageUtil.sendMessage(p1, "&cThe kit does not exist.");
            return;
        }

        PracticePlayer pp1 = plugin.getPlayerManager().getPlayer(p1);
        PracticePlayer pp2 = plugin.getPlayerManager().getPlayer(p2);

        if (!pp2.isInSpawn()) {
            MessageUtil.sendMessage(p1, "&cThat player is not available.");
            return;
        }

        DuelRequest request = new DuelRequest(p1, p2, kit);

        BukkitTask task = new BukkitRunnable() {
            int time = 60;

            @Override
            public void run() {
                if (time-- <= 0) {
                    List<DuelRequest> list = duelRequests.getOrDefault(p2, new ArrayList<>());
                    list.remove(request);
                    if (list.isEmpty()) {
                        duelRequests.remove(p2);
                    }

                    MessageUtil.sendActionBar(p1, "&cYour duel request to " + p2.getName() + " has expired.");
                    MessageUtil.sendMessage(p2, "&cThe duel request from " + p1.getName() + " has expired.");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

        request.setTask(task);

        duelRequests.computeIfAbsent(p2, k -> new ArrayList<>()).add(request);

        MessageUtil.sendMessage(p2, "&b" + p1.getName() + " &rhas sent a duel request with kit &e" + kit + "&r. You have 60 seconds to accept.");
        MessageUtil.sendMessage(p1, "&aDuel request sent to &b" + p2.getName() + "&a.");
        p1.playSound(p1.getLocation(), Sound.NOTE_BASS, 1f, 1f);
        p2.playSound(p1.getLocation(), Sound.NOTE_BASS, 1f, 1f);
    }


    public void acceptDuelRequest(Player p2, Player p1) {
        List<DuelRequest> list = duelRequests.get(p2);

        if (list == null || list.isEmpty()) {
            MessageUtil.sendMessage(p2, "&cYou don't have any pending duel requests.");
            return;
        }

        DuelRequest request = list.stream()
                .filter(r -> r.getSender().equals(p1))
                .findFirst()
                .orElse(null);

        if (request == null) {
            MessageUtil.sendMessage(p2, "&cNo duel request found from " + p1.getName());
            return;
        }

        request.cancelTask();
        list.remove(request);
        if (list.isEmpty()) duelRequests.remove(p2);

        String kit = request.getKit();

        MessageUtil.sendMessage(p1, "&a" + p2.getName() + " accepted your duel request!");
        MessageUtil.sendMessage(p2, "&aYou accepted the duel request from " + p1.getName() + "!");

        processNewGame(p1, p2, plugin.getKitManager().getKit(kit));
    }


    public void processNewGame(Player p1, Player p2, Kit kit) {
        GameArena ga = null;

        for (GameArena gas : plugin.getDataManager().getGameArenas()) {
            if (!gas.isInUse() && kit.equals(gas.getKit())) {
                ga = gas;
                break;
            }
        }

        if (ga == null) {
            List<Arena> allArenas = new ArrayList<>(plugin.getDataManager().getArenas());
            if (allArenas.isEmpty()) {
                MessageUtil.sendMessage(p1, p2, "&cNo configured arenas exist!");
                return;
            }
            ga = plugin.getArenaManager().createGameArena(kit);
        }
        if (ga == null) {
            MessageUtil.sendMessage(p1, p2, "&cNo arenas are available now!");
        }

        startNewGame(plugin.getPlayerManager().getPlayer(p1), plugin.getPlayerManager().getPlayer(p2), kit, ga);
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
        pp1.unlockAchievement(Achievement.FIRSTGAME);
        games.put(game.getId(), game);
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
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void endGame(Game game, PracticePlayer winner) {
        game.setState(GameStete.ENDING);
        Player p1 = Bukkit.getPlayer(game.getPlayer1().getUuid());
        Player p2 = Bukkit.getPlayer(game.getPlayer2().getUuid());

        game.getPlayer1().setState(PlayerState.SPAWN);
        game.getPlayer2().setState(PlayerState.SPAWN);
        game.getPlayer1().setCurrentGame(null);
        game.getPlayer2().setCurrentGame(null);

        game.getArena().setInUse(false);

        String message = "&f-------------------------\n&bWinner: &f" + winner.getName() + "&r | &cLoser: &f" + game.getOpponent(winner).getName() + "&r\n&f-------------------------";
        if (p1 != null) {
            plugin.getDataManager().teleportToSpawn(p1);

        }
        if (p2 != null) {
            plugin.getDataManager().teleportToSpawn(p2);
        }

        if (winner != null) {
            Player winnerPlayer = Bukkit.getPlayer(winner.getUuid());
            PracticePlayer winnerPp = plugin.getPlayerManager().getPlayer(winnerPlayer);
            PracticePlayer loser = game.getOpponent(winnerPp);
            if (loser.getPlayer() != null)MessageUtil.sendTitle(loser.getPlayer(), "&cDEFEAT!", "You have been defeated by " + winner.getName());
            if (winnerPp.getPlayer() != null)MessageUtil.sendTitle(winner.getPlayer(), "&aVICTORY!", "You have defeated " + loser.getName());

            if (p1 != null) MessageUtil.sendMessage(p1, message);
            if (p2 != null) MessageUtil.sendMessage(p2, message);
        }

        games.remove(game.getId());
    }
}
