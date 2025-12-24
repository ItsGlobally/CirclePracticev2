package top.itsglobally.circlenetwork.circlepractice.managers;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import top.itsglobally.circlenetwork.circlepractice.achievement.Achievement;
import top.itsglobally.circlenetwork.circlepractice.data.*;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.itsglobally.circlenetwork.circlepractice.utils.NMSUtils;
import top.itsglobally.circlenetwork.circlepractice.utils.RandomUtil;
import top.itsglobally.circlenetwork.circlepractice.utils.TeamColorUtil;

import java.util.*;

public class GameManager implements GlobalInterface {

    private static final long END_DELAY_TICKS = 60L; // 3 seconds

    private final Map<Player, List<DuelRequest>> duelRequests = new HashMap<>();
    private final Map<UUID, Game> games = new HashMap<>();

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

        if (pp1 == null || pp2 == null || !pp1.isInSpawn() || !pp2.isInSpawn()) {
            MessageUtil.sendMessage(p1, "&d&l✗ &fThat player is not available!");
            return;
        }

        List<DuelRequest> list = duelRequests.computeIfAbsent(p2, k -> new ArrayList<>());
        if (list.stream().anyMatch(r -> r.getSender().equals(p1))) {
            MessageUtil.sendMessage(p1, "&d&l✗ &fYou already sent a duel request!");
            return;
        }

        DuelRequest request = new DuelRequest(p1, p2, kit);

        BukkitTask task = new BukkitRunnable() {
            int time = plugin.getConfigManager().getMainConfig().duelRequestExpire;

            @Override
            public void run() {
                if (--time <= 0) {
                    list.remove(request);
                    if (list.isEmpty()) duelRequests.remove(p2);
                    MessageUtil.sendMessage(p1, "&d&l✗ &fDuel request expired!");
                    MessageUtil.sendMessage(p2, "&d&l✗ &fDuel request expired!");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);

        request.setTask(task);
        list.add(request);

        MessageUtil.sendMessage(p2, "&d&l⚔ &d" + p1.getName() + " &fwants to duel!");
        MessageUtil.sendMessage(p1, "&d&l✓ &fDuel request sent!");
        p1.playSound(p1.getLocation(), Sound.CLICK, 1f, 1f);
        p2.playSound(p2.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
    }

    public void acceptDuelRequest(Player p2, Player p1) {
        List<DuelRequest> list = duelRequests.get(p2);
        if (list == null) return;

        DuelRequest request = list.stream()
                .filter(r -> r.getSender().equals(p1))
                .findFirst().orElse(null);

        if (request == null) return;

        request.cancelTask();
        list.remove(request);
        if (list.isEmpty()) duelRequests.remove(p2);

        processNewGame(p1, p2, plugin.getKitManager().getKit(request.getKit()));
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
        startNewGame(
                plugin.getPlayerManager().getPlayer(p1),
                plugin.getPlayerManager().getPlayer(p2),
                kit, ga
        );
    }

    public void startNewGame(PracticePlayer a, PracticePlayer b, Kit kit, GameArena arena) {
        boolean swap = RandomUtil.getRandomBoolean();
        PracticePlayer p1 = swap ? a : b;
        PracticePlayer p2 = swap ? b : a;

        Game game = new Game(p1, p2, kit, arena);
        games.put(game.getId(), game);

        Player bp1 = p1.getPlayer();
        Player bp2 = p2.getPlayer();

        bp1.teleport(arena.getPos1());
        bp2.teleport(arena.getPos2());

        bp1.getActivePotionEffects().forEach(p -> bp1.removePotionEffect(p.getType()));
        bp2.getActivePotionEffects().forEach(p -> bp2.removePotionEffect(p.getType()));
        bp1.setFireTicks(0);
        bp2.setFireTicks(0);

        bp1.setAllowFlight(false);
        bp2.setAllowFlight(false);

        ItemStack[][] kit1 = p1.getPlayerData().getKitContents(kit.getName());
        ItemStack[][] kit2 = p2.getPlayerData().getKitContents(kit.getName());

        bp1.getInventory().setContents(TeamColorUtil.colorTeamItems(kit1[0], true));
        bp1.getInventory().setArmorContents(TeamColorUtil.colorTeamItems(kit1[1], true));
        bp2.getInventory().setContents(TeamColorUtil.colorTeamItems(kit2[0], false));
        bp2.getInventory().setArmorContents(TeamColorUtil.colorTeamItems(kit2[1], false));


        p1.setState(PlayerState.DUEL);
        p2.setState(PlayerState.DUEL);
        p1.setCurrentGame(game);
        p2.setCurrentGame(game);

        game.setState(GameState.STARTING);
        arena.setInUse(true);
        startCooldown(game);
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

        PracticePlayer p1 = game.getPlayer1();
        PracticePlayer p2 = game.getPlayer2();

        p1.setState(PlayerState.SPAWN);
        p2.setState(PlayerState.SPAWN);
        p1.setCurrentGame(null);
        p2.setCurrentGame(null);

        handleGameEnd(game, winner, p1.getPlayer(), p2.getPlayer());

        new BukkitRunnable() {
            @Override
            public void run() {
                finishGameCleanup(game);
            }
        }.runTaskLater(plugin, END_DELAY_TICKS);
    }

    private void finishGameCleanup(Game game) {
        Player p1 = game.getPlayer1().getPlayer();
        Player p2 = game.getPlayer2().getPlayer();

        resetPlayer(p1);
        resetPlayer(p2);

        for (UUID u : game.getSpectators()) {
            Player spec = Bukkit.getPlayer(u);
            resetPlayer(spec);
            if (p1 != null) p1.showPlayer(spec);
            if (p2 != null) p2.showPlayer(spec);
        }

        game.getArena().setInUse(false);
        if (game.getArena().isRemake()) {
            plugin.getArenaManager().removeGameArena(game.getArena());
        }

        games.remove(game.getId());
    }

    private void resetPlayer(Player player) {
        if (player == null) return;
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(player);
        if (pp.isInSpawn() && !player.getLocation().getWorld().equals(plugin.getConfigManager().getMainConfig().getSpawn().getWorld())) {
            plugin.getConfigManager().teleportToSpawn(player);
            player.getActivePotionEffects().forEach(p -> player.removePotionEffect(p.getType()));
            player.setFireTicks(0);
        }
    }

    private void handleGameEnd(Game game, PracticePlayer winner, Player p1, Player p2) {
        PracticePlayer loser = game.getOpponent(winner);

        MessageUtil.sendTitle(winner.getPlayer(), "&d&lVICTORY!", "&fYou won!");
        MessageUtil.sendTitle(loser.getPlayer(), "&c&lDEFEAT!", "&fYou lost!");

        plugin.getPlayerDataManager().getData(winner.getPlayer()).addXps(20);
        plugin.getPlayerDataManager().getData(loser.getPlayer()).addXps(10);

        List<Player> viewers = new ArrayList<>();
        viewers.add(winner.getPlayer());

        for (UUID u : game.getSpectators()) {
            Player spec = Bukkit.getPlayer(u);
            if (spec != null) viewers.add(spec);
        }

        Player player = game.getOpponent(winner).getPlayer();

        winner.getPlayerData().unlockAchievement(Achievement.WINFIRSTGAME);

        NMSUtils.playFakeDeath(player, viewers);
    }

    public void joinSpec(Game game, Player p) {
        if (game == null || p == null) return;
        p.teleport(game.getPlayer1().getPlayer());
        game.getPlayer1().getPlayer().hidePlayer(p);
        game.getPlayer2().getPlayer().hidePlayer(p);
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        pp.setState(PlayerState.SPECTATING);
        pp.setCurrentGame(game);
        game.addSpectator(p.getUniqueId());
        game.broadcast(p.getName() + " &dstarted spectating.");
    }

    public void stopSpec(Game game, Player p) {
        if (game == null || p == null) return;
        resetPlayer(p);
        game.getPlayer1().getPlayer().showPlayer(p);
        game.getPlayer2().getPlayer().showPlayer(p);
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        pp.setState(PlayerState.SPAWN);
        pp.setCurrentGame(null);
        game.removeSpectator(p.getUniqueId());
        game.broadcast(p.getName() + " &dstopped spectating.");
    }
}
