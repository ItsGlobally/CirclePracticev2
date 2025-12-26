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

    private static final long END_DELAY_TICKS = 60L; // 3 秒

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
                .filter(a -> !a.isInUse() && a.getKits().stream().anyMatch(k -> k.equalsIgnoreCase(kit.getName())))
                .toList();
    }

    public void processNewGame(Player p1, Player p2, Kit kit) {
        GameArena ga = null;
        List<GameArena> ngas = findAvailableArenas(kit);
        if (!ngas.isEmpty()) ga = ngas.get(RandomUtil.nextInt(ngas.size()));
        if (ga == null) ga = plugin.getArenaManager().createGameArena(kit);
        if (ga == null) {
            MessageUtil.sendMessage(p1, p2, "&d&l✗ &fError creating arena!");
            return;
        }

        startNewGame(
                List.of(plugin.getPlayerManager().getPlayer(p1)),
                List.of(plugin.getPlayerManager().getPlayer(p2)),
                kit, ga
        );
    }

    public void startNewGame(List<PracticePlayer> redTeam,
                             List<PracticePlayer> blueTeam,
                             Kit kit,
                             GameArena arena) {

        HashMap<UUID, PracticePlayer> red = new HashMap<>();
        HashMap<UUID, PracticePlayer> blue = new HashMap<>();

        for (PracticePlayer pp : redTeam) red.put(pp.getUuid(), pp);
        for (PracticePlayer pp : blueTeam) blue.put(pp.getUuid(), pp);

        Game game = new Game(red, blue, kit, arena);
        games.put(game.getId(), game);

        for (PracticePlayer pp : game.getRedPlayers()) {
            Player p = pp.getPlayer();
            p.teleport(arena.getPos1());
            setupPlayer(pp, game, true);
        }

        for (PracticePlayer pp : game.getBluePlayers()) {
            Player p = pp.getPlayer();
            p.teleport(arena.getPos2());
            setupPlayer(pp, game, false);
        }

        arena.setInUse(true);
        game.setState(GameState.STARTING);
        startCooldown(game);
    }

    private void setupPlayer(PracticePlayer pp, Game game, boolean red) {
        Player p = pp.getPlayer();
        p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
        p.setFireTicks(0);
        p.setAllowFlight(false);

        ItemStack[][] kit = pp.getPlayerData().getKitContents(game.getKit().getName());
        p.getInventory().setContents(TeamColorUtil.colorTeamItems(kit[0], red));
        p.getInventory().setArmorContents(TeamColorUtil.colorTeamItems(kit[1], red));

        pp.setState(PlayerState.DUEL);
        pp.setCurrentGame(game);
    }

    public void startCooldown(Game game) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getState() != GameState.STARTING) {
                    cancel();
                    return;
                }

                int countdown = game.getCountdown();

                for (PracticePlayer pp : game.getAllPlayers()) {
                    Player p = pp.getPlayer();
                    if (p == null) continue;

                    if (countdown > 0) {
                        MessageUtil.sendMessage(p, "&f&lDuel starting in &d" + countdown + "&f...");
                        p.playSound(p.getLocation(), Sound.CLICK, 1f, 1f);
                    } else {
                        MessageUtil.sendMessage(p, "&d&l⚔ FIGHT!");
                        p.playSound(p.getLocation(), Sound.FIREWORK_BLAST, 1f, 1f);
                    }
                }

                if (countdown <= 0) {
                    game.setState(GameState.ONGOING);
                    cancel();
                } else {
                    game.setCountdown(countdown - 1);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void endGame(Game game, Collection<PracticePlayer> winningTeam) {
        game.setState(GameState.ENDING);

        for (PracticePlayer pp : game.getAllPlayers()) {
            pp.setState(PlayerState.SPAWN);
            pp.setCurrentGame(null);
        }

        handleGameEnd(game, winningTeam);

        new BukkitRunnable() {
            @Override
            public void run() {
                finishGameCleanup(game);
            }
        }.runTaskLater(plugin, END_DELAY_TICKS);
    }

    private void finishGameCleanup(Game game) {

        for (PracticePlayer pp : game.getAllPlayers()) {
            Player p = pp.getPlayer();
            if (p == null) continue;
            resetPlayer(p);

            for (UUID u : game.getSpectators()) {
                Player spec = Bukkit.getPlayer(u);
                if (spec != null) p.showPlayer(spec);
            }
        }

        for (UUID u : game.getSpectators()) {
            Player spec = Bukkit.getPlayer(u);
            if (spec == null) continue;
            resetPlayer(spec);
            for (PracticePlayer pp : game.getAllPlayers()) {
                Player p = pp.getPlayer();
                if (p != null) {
                    p.showPlayer(spec);
                    spec.showPlayer(p);
                }
            }
        }

        game.getArena().setInUse(false);
        if (game.getArena().isRemake()) plugin.getArenaManager().removeGameArena(game.getArena());

        games.remove(game.getId());
    }

    private void resetPlayer(Player player) {
        if (player == null) return;
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(player);
        if (pp.isInSpawn() && !player.getLocation().getWorld().equals(plugin.getConfigManager().getMainConfig().getSpawn().getWorld())) {
            plugin.getConfigManager().teleportToSpawn(player);
            player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));
            player.setFireTicks(0);
        }
    }

    private void handleGameEnd(Game game, Collection<PracticePlayer> winningTeam) {
        Set<PracticePlayer> losers = new HashSet<>(game.getAllPlayers());
        losers.removeAll(winningTeam);

        StringBuilder finalMsg = new StringBuilder();
        finalMsg.append("&d&m                           \n");
        finalMsg.append("&aWinner: &d");
        for (PracticePlayer pp : winningTeam) {
            finalMsg.append(pp.getPlayer().getName()).append(", ");
        }
        if (!winningTeam.isEmpty()) finalMsg.setLength(finalMsg.length() - 2);

        finalMsg.append("\n&cLoser: ");
        for (PracticePlayer pp : losers) {
            finalMsg.append(pp.getPlayer().getName()).append(", ");
        }
        if (!losers.isEmpty()) finalMsg.setLength(finalMsg.length() - 2);
        finalMsg.append("\n&d&m                           ");

        for (PracticePlayer pp : winningTeam) {
            MessageUtil.sendTitle(pp.getPlayer(), "&d&lVICTORY!", "&fYour team won!");
            MessageUtil.sendMessage(pp.getPlayer(), finalMsg.toString());
            pp.getPlayerData().addXps(20);
            pp.getPlayerData().unlockAchievement(Achievement.WINFIRSTGAME);
        }

        for (PracticePlayer pp : losers) {
            MessageUtil.sendTitle(pp.getPlayer(), "&c&lDEFEAT!", "&fYour team lost!");
            MessageUtil.sendMessage(pp.getPlayer(), finalMsg.toString());
            pp.getPlayerData().addXps(10);
        }

        List<Player> viewers = new ArrayList<>();
        for (PracticePlayer pp : winningTeam) viewers.add(pp.getPlayer());
        for (PracticePlayer pp : losers) viewers.add(pp.getPlayer());
        for (UUID u : game.getSpectators()) {
            Player spec = Bukkit.getPlayer(u);
            if (spec != null) viewers.add(spec);
        }

        for (PracticePlayer pp : losers) {
            NMSUtils.playFakeDeath(pp.getPlayer(), viewers);
        }
    }


    public void joinSpec(Game game, Player p) {
        if (game == null || p == null) return;

        Optional<PracticePlayer> anyPlayer = game.getRedPlayers().stream().findFirst();
        anyPlayer.ifPresent(pp -> p.teleport(pp.getPlayer()));

        for (PracticePlayer pp : game.getAllPlayers()) pp.getPlayer().hidePlayer(p);

        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        pp.setState(PlayerState.SPECTATING);
        pp.setCurrentGame(game);
        game.addSpectator(p.getUniqueId());

        game.broadcast(p.getName() + " &dstarted spectating.");
    }

    public void stopSpec(Game game, Player p) {
        if (game == null || p == null) return;

        resetPlayer(p);
        for (PracticePlayer pp : game.getAllPlayers()) {
            pp.getPlayer().showPlayer(p);
            p.showPlayer(pp.getPlayer());
        }

        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        pp.setState(PlayerState.SPAWN);
        pp.setCurrentGame(null);
        game.removeSpectator(p.getUniqueId());

        game.broadcast(p.getName() + " &dstopped spectating.");
    }

}
