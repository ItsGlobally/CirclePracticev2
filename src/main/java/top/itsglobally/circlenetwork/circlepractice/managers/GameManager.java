package top.itsglobally.circlenetwork.circlepractice.managers;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import top.itsglobally.circlenetwork.circlepractice.achievement.Achievement;
import top.itsglobally.circlenetwork.circlepractice.data.*;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.itsglobally.circlenetwork.circlepractice.utils.RandomUtil;
import top.itsglobally.circlenetwork.circlepractice.utils.TeamColorUtil;

import java.util.*;

public class GameManager implements GlobalInterface {
    private final Map<Player, List<DuelRequest>> duelRequests;
    private final Map<UUID, Game> games;


    public GameManager() {
        games = new HashMap<>();
        duelRequests = new HashMap<>();
    }

    public void sendDuelRequest(Player p1, Player p2, String kit) {
        if (p1 == null || p2 == null || kit == null) return;

        if (p1.equals(p2)) {
            MessageUtil.sendMessage(p1, "&d&l✗ &fYou cannot duel yourself!");
            return;
        }

        if (!plugin.getKitManager().kitAlreadyExist(kit)) {
            MessageUtil.sendMessage(p1, "&d&l✗ &fThe kit does not exist!");
            return;
        }

        PracticePlayer pp1 = plugin.getPlayerManager().getPlayer(p1);
        PracticePlayer pp2 = plugin.getPlayerManager().getPlayer(p2);

        if (pp1 == null || pp2 == null) {
            MessageUtil.sendMessage(p1, "&d&l✗ &fPlayer data not found!");
            return;
        }
        if (!pp1.isInSpawn()) {
            MessageUtil.sendMessage(p1, "&d&l✗ &fYou're not in the spawn!");
            return;
        }
        if (!pp2.isInSpawn()) {
            MessageUtil.sendMessage(p1, "&d&l✗ &fThat player is not available!");
            return;
        }

        List<DuelRequest> existingRequests = duelRequests.getOrDefault(p2, new ArrayList<>());
        for (DuelRequest existing : existingRequests) {
            if (existing.getSender().equals(p1)) {
                MessageUtil.sendMessage(p1, "&d&l✗ &fYou already sent a duel request to &d" + p2.getName() + "&f!");
                return;
            }
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

                    MessageUtil.sendActionBar(p1, "&d&l✗ &fYour duel request to &d" + p2.getName() + " &fhas expired!");
                    MessageUtil.sendMessage(p2, "&d&l✗ &fThe duel request from &d" + p1.getName() + " &fhas expired!");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

        request.setTask(task);

        duelRequests.computeIfAbsent(p2, k -> new ArrayList<>()).add(request);

        MessageUtil.sendMessage(p2, "&d&l⚔ &d" + p1.getName() + " &fhas sent a duel request with kit &d" + kit + "&f. You have 60 seconds to accept!");
        MessageUtil.sendMessage(p1, "&d&l✓ &fDuel request sent to &d" + p2.getName() + "&f!");
        p1.playSound(p1.getLocation(), Sound.NOTE_BASS, 1f, 1f);
        p2.playSound(p1.getLocation(), Sound.NOTE_BASS, 1f, 1f);
    }


    public void acceptDuelRequest(Player p2, Player p1) {
        if (p1 == null || p2 == null) return;

        List<DuelRequest> list = duelRequests.get(p2);

        if (list == null || list.isEmpty()) {
            MessageUtil.sendMessage(p2, "&d&l✗ &fYou don't have any pending duel requests!");
            return;
        }

        DuelRequest request = list.stream()
                .filter(r -> r.getSender().equals(p1))
                .findFirst()
                .orElse(null);

        if (request == null) {
            MessageUtil.sendMessage(p2, "&d&l✗ &fNo duel request found from &d" + p1.getName() + "&f!");
            return;
        }

        request.cancelTask();
        list.remove(request);
        if (list.isEmpty()) duelRequests.remove(p2);

        String kit = request.getKit();

        MessageUtil.sendMessage(p1, "&d&l✓ &d" + p2.getName() + " &faccepted your duel request!");
        MessageUtil.sendMessage(p2, "&d&l✓ &fYou accepted the duel request from &d" + p1.getName() + "&f!");

        processNewGame(p1, p2, plugin.getKitManager().getKit(kit));
    }

    private List<GameArena> findAvailableArenas(Kit kit) {
        return plugin.getArenaManager().getGameArenas().stream()
                .filter(a -> {
                    boolean valid = !a.isInUse()
                            && a.getKits().stream().anyMatch(k -> k.equalsIgnoreCase(kit.getName()));
                    Bukkit.getLogger().info("[DEBUG] Arena=" + a.getName()
                            + " | inUse=" + a.isInUse()
                            + " | kits=" + a.getKits()
                            + " | kit=" + kit.getName()
                            + " | valid=" + valid);
                    return valid;
                })
                .toList();
    }


    public void processNewGame(Player p1, Player p2, Kit kit) {
        GameArena ga = null;

        List<GameArena> ngas = findAvailableArenas(kit);
        if (!ngas.isEmpty()) {
            ga = ngas.get(RandomUtil.nextInt(ngas.size()));
        }

        if (ga == null) {
            ga = plugin.getArenaManager().createGameArena(kit);
        }
        if (ga == null) {
            MessageUtil.sendMessage(p1, p2, "&d&l✗ &fError creating arena!");
            return;
        }

        startNewGame(plugin.getPlayerManager().getPlayer(p1),
                plugin.getPlayerManager().getPlayer(p2),
                kit, ga);
    }

    public void startNewGame(PracticePlayer pp11, PracticePlayer pp12, Kit kit, GameArena arena) {
        if (pp11 == null || pp12 == null || kit == null || arena == null) {
            Bukkit.getLogger().warning("Cannot start game: invalid parameters");
            return;
        }
        boolean p1isred = RandomUtil.getRandomBoolean();
        PracticePlayer pp1;
        PracticePlayer pp2;
        if (p1isred) {
            pp1 = pp11;
            pp2 = pp12;
        } else {
            pp1 = pp12;
            pp2 = pp11;
        }
        Game game = new Game(pp1, pp2, kit, arena);
        Player p1 = game.getPlayer1().getPlayer();
        Player p2 = game.getPlayer2().getPlayer();

        if (p1 == null || p2 == null) {
            Bukkit.getLogger().warning("Cannot start game: player is null");
            return;
        }

        p1.teleport(game.getArena().getPos1());
        p2.teleport(game.getArena().getPos2());

        ItemStack[][] p1Kit = pp1.getPlayerData().getKitContents(game.getKit().getName());
        ItemStack[][] p2Kit = pp2.getPlayerData().getKitContents(game.getKit().getName());

        p1.getInventory().setArmorContents(TeamColorUtil.colorTeamItems(p1Kit[1], p1isred));
        p1.getInventory().setContents(TeamColorUtil.colorTeamItems(p1Kit[0], p1isred));
        p2.getInventory().setArmorContents(TeamColorUtil.colorTeamItems(p2Kit[1], !p1isred));
        p2.getInventory().setContents(TeamColorUtil.colorTeamItems(p2Kit[0], !p1isred));
        p1.setFoodLevel(40);
        p1.setHealth(20);
        p2.setFoodLevel(40);
        p2.setHealth(20);
        pp1.setState(PlayerState.DUEL);
        pp2.setState(PlayerState.DUEL);
        pp1.setCurrentGame(game);
        pp2.setCurrentGame(game);
        p1.getPlayer().setFlying(false);
        p2.getPlayer().setFlying(false);
        p1.getPlayer().setAllowFlight(false);
        p2.getPlayer().setAllowFlight(false);
        game.setState(GameState.STARTING);
        game.getArena().setInUse(true);
        startCooldown(game);
        plugin.getPlayerDataManager().getData(p1).unlockAchievement(Achievement.JOIN);
        plugin.getPlayerDataManager().getData(p2).unlockAchievement(Achievement.JOIN);
        games.put(game.getId(), game);
    }

    public void startCooldown(Game game) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (game.getState() != GameState.STARTING) {
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
                    MessageUtil.sendMessage(p1, "&f&lDuel starting in &d" + countdown + "&f...");
                    MessageUtil.sendMessage(p2, "&f&lDuel starting in &d" + countdown + "&f...");
                    p1.playSound(p1.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                    p2.playSound(p2.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                    game.setCountdown(countdown - 1);
                } else {
                    MessageUtil.sendMessage(p1, "&d&l⚔ FIGHT!");
                    MessageUtil.sendMessage(p2, "&d&l⚔ FIGHT!");
                    p1.playSound(p1.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
                    p2.playSound(p2.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
                    game.setState(GameState.ONGOING);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void endGame(Game game, PracticePlayer winner) {
        game.setState(GameState.ENDING);
        Player p1 = Bukkit.getPlayer(game.getPlayer1().getUuid());
        Player p2 = Bukkit.getPlayer(game.getPlayer2().getUuid());

        game.getPlayer1().setState(PlayerState.SPAWN);
        game.getPlayer2().setState(PlayerState.SPAWN);
        game.getPlayer1().setCurrentGame(null);
        game.getPlayer2().setCurrentGame(null);
        game.getArena().setInUse(false);

        resetPlayer(p1);
        resetPlayer(p2);

        if (winner != null) {
            handleGameEnd(game, winner, p1, p2);
        }

        if (game.getArena().isRemake()) {
            plugin.getArenaManager().removeGameArena(game.getArena());
        }

        games.remove(game.getId());
    }

    private void resetPlayer(Player player) {
        if (player == null) return;
        plugin.getConfigManager().teleportToSpawn(player);
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));
        player.setFireTicks(0);
    }

    private void handleGameEnd(Game game, PracticePlayer winner, Player p1, Player p2) {
        PracticePlayer loser = game.getOpponent(winner);
        String message = "&f&m                    &r\n&d&lWinner: &f" + winner.getName() + " &7| &d&lLoser: &f" + loser.getName() + "\n&f&m                    &r";

        if (loser.getPlayer() != null) {
            MessageUtil.sendTitle(loser.getPlayer(), "&c&lDEFEAT!", "&fYou have been defeated by &d" + winner.getName());
        }
        if (winner.getPlayer() != null) {
            MessageUtil.sendTitle(winner.getPlayer(), "&d&lVICTORY!", "&fYou have defeated &d" + loser.getName());
        }

        if (p1 != null) MessageUtil.sendMessage(p1, message);
        if (p2 != null) MessageUtil.sendMessage(p2, message);

        if (winner.getPlayer() != null) {
            plugin.getPlayerDataManager().getData(winner.getPlayer()).addXps(20);
            MessageUtil.sendMessage(winner.getPlayer(), "&d&l⭐ &fYou won and earned &d20 XP&f!");
        }
        if (loser.getPlayer() != null) {
            plugin.getPlayerDataManager().getData(loser.getPlayer()).addXps(10);
            MessageUtil.sendMessage(loser.getPlayer(), "&d&l⭐ &fYou lost but earned &d10 XP&f!");
        }
    }
}
