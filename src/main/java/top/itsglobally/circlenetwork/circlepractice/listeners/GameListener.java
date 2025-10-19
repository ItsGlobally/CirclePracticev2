package top.itsglobally.circlenetwork.circlepractice.listeners;

import com.avaje.ebeaninternal.server.core.Message;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import top.itsglobally.circlenetwork.circlepractice.data.Game;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.itsglobally.circlenetwork.circlepractice.data.Temp;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.AutoListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AutoListener
public class GameListener implements Listener, IListener {

    private HashMap<UUID, Boolean> respawning = new HashMap<>();

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
            if (respawning.getOrDefault(game.getOpponent(vicp).getUuid(), false)) {
                e.setCancelled(true);
                return;
            }
            if (vic.getHealth() < e.getFinalDamage()) {
                if (game.getKit().isRespawnable()) {
                    if (game.getPlayerRespawnable(vicp)) {
                        e.setCancelled(true);
                        vic.setHealth(20.0);
                        vic.setFoodLevel(20);
                        PracticePlayer klrpp = game.getOpponent(vicp);
                        klrpp.getPlayer().hidePlayer(vic);

                        int[] deathCountdown = {game.getKit().getRespawnTime()};
                        vic.teleport(klrpp.getPlayer().getLocation());
                        vic.setAllowFlight(true);
                        vic.setFlying(true);
                        vic.getInventory().clear();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (deathCountdown[0] <= 0) {
                                    klrpp.getPlayer().showPlayer(vic);
                                    vic.teleport(game.getPlayerSpawnPoint(vicp));
                                    vic.setAllowFlight(false);
                                    vic.setFlying(false);
                                    vic.getInventory().setArmorContents(game.getKit().getArmor());
                                    vic.getInventory().setContents(game.getKit().getContents());
                                    MessageUtil.sendMessage(vic, "&aYou have respawned!");
                                    cancel();
                                    return;
                                }
                                if (vicp.isInSpawn()) cancel();
                                MessageUtil.sendMessage(vic, "&eRespawning in " + deathCountdown[0] + "s...");
                                deathCountdown[0]--;
                            }
                        }.runTaskTimer(plugin, 0L, 20L);
                    } else {
                        e.setCancelled(true);
                        vic.setHealth(20.0);
                        vic.setFoodLevel(20);
                        Player winner = game.getOpponent(vicp).getPlayer();
                        winner.setHealth(20.0);
                        winner.setFoodLevel(20);
                        plugin.getGameManager().endGame(game, game.getOpponent(vicp));
                    }
                } else {
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
    }
    @EventHandler
    public void move(PlayerMoveEvent e) {
        Player vic = e.getPlayer();
        PracticePlayer vicp = plugin.getPlayerManager().getPlayer(vic);

        if (!vicp.isInDuel()) return;
        Game game = vicp.getCurrentGame();

        Player killer = game.getOpponent(vicp).getPlayer();
        if (e.getPlayer().getLocation().getY() <= 235) {
            if (respawning.getOrDefault(vic.getUniqueId(), false)) {
                vic.teleport(game.getPlayerSpawnPoint(vicp));
                return;
            }
        }
        if (e.getPlayer().getLocation().getBlockY() <= 230) {
            if (game.getKit().isRespawnable() && game.getPlayerRespawnable(vicp)) {
                vic.setHealth(20.0);
                vic.setFoodLevel(20);
                killer.hidePlayer(vic);
                vic.teleport(killer.getPlayer().getLocation());
                vic.setAllowFlight(true);
                vic.setFlying(true);
                vic.getInventory().clear();

                int[] deathCountdown = {game.getKit().getRespawnTime()};
                respawning.put(vic.getUniqueId(), true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (deathCountdown[0] <= 0) {
                            killer.showPlayer(vic);
                            vic.teleport(game.getPlayerSpawnPoint(vicp));
                            vic.setAllowFlight(false);
                            vic.setFlying(false);
                            vic.getInventory().setArmorContents(game.getKit().getArmor());
                            vic.getInventory().setContents(game.getKit().getContents());
                            respawning.put(vic.getUniqueId(), false);
                            MessageUtil.sendMessage(vic, "§aYou have respawned!");
                            cancel();
                            return;
                        }
                        if (vicp.isInSpawn()) cancel();
                        MessageUtil.sendMessage(vic, "§eRespawning in " + deathCountdown[0] + "s...");
                        deathCountdown[0]--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);

            } else {
                vic.setHealth(20.0);
                vic.setFoodLevel(20);
                killer.setHealth(20.0);
                killer.setFoodLevel(20);
                plugin.getGameManager().endGame(game, game.getOpponent(vicp));
            }
        }
    }
    @EventHandler
    public void died(PlayerDeathEvent e) {
        Player vic = e.getEntity();
        PracticePlayer vicp = plugin.getPlayerManager().getPlayer(vic);

        if (!vicp.isInDuel()) return;
        Game game = vicp.getCurrentGame();

        e.setDeathMessage(null);
        e.getDrops().clear();
        e.setDroppedExp(0);

        Player killer = game.getOpponent(vicp).getPlayer();

        if (game.getKit().isRespawnable() && game.getPlayerRespawnable(vicp)) {
            vic.spigot().respawn();
            vic.setHealth(20.0);
            vic.setFoodLevel(20);
            killer.hidePlayer(vic);
            vic.teleport(killer.getPlayer().getLocation());
            vic.setAllowFlight(true);
            vic.setFlying(true);
            vic.getInventory().clear();
            int[] deathCountdown = {game.getKit().getRespawnTime()};

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (deathCountdown[0] <= 0) {
                        killer.showPlayer(vic);
                        vic.teleport(game.getPlayerSpawnPoint(vicp));
                        vic.setAllowFlight(false);
                        vic.setFlying(false);
                        vic.getInventory().setArmorContents(game.getKit().getArmor());
                        vic.getInventory().setContents(game.getKit().getContents());
                        MessageUtil.sendMessage(vic, "§aYou have respawned!");
                        cancel();
                        return;
                    }
                    if (vicp.isInSpawn()) cancel();
                    MessageUtil.sendMessage(vic, "§eRespawning in " + deathCountdown[0] + "s...");
                    deathCountdown[0]--;
                }
            }.runTaskTimer(plugin, 0L, 20L);

        } else {
            vic.spigot().respawn();
            vic.setHealth(20.0);
            vic.setFoodLevel(20);
            killer.setHealth(20.0);
            killer.setFoodLevel(20);
            plugin.getGameManager().endGame(game, game.getOpponent(vicp));
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
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());

        if (pp.isInDuel()) {
            Game game = pp.getCurrentGame();
            if (respawning.getOrDefault(pp.getUuid(), false)) {
                e.setCancelled(true);
                return;
            }
            if (!game.getKit().isCanBuild()) {
                e.setCancelled(true);
                return;
            }

            if (game.getKit().getBrokeToNoSpawn() != null && e.getBlock().getType() == game.getKit().getBrokeToNoSpawn()) {
                boolean isEnemyBed = game.getIsEnemyBed(pp, e.getBlock().getLocation());
                boolean isOwnBed = game.getIsOwnBed(pp, e.getBlock().getLocation());

                if (game.getKit().getBrokeToNoSpawn() != null &&
                        e.getBlock().getType() == game.getKit().getBrokeToNoSpawn()) {

                    if (isEnemyBed) {
                        game.setRespawnable(game.getOpponent(pp), false);
                        MessageUtil.sendTitle(game.getOpponent(pp).getPlayer(), "&cBED DESTROYED", "You won't be able to respawn again!");
                        MessageUtil.sendMessage(e.getPlayer(), game.getOpponent(pp).getPlayer(),
                                "BED DESTROY > &7" + game.getOpponent(pp).getPlayer().getName() +
                                        "&r's bed has been destroyed by " + e.getPlayer().getName() + "!");
                        e.setCancelled(false);
                    } else if (isOwnBed) {
                        e.setCancelled(true);
                        MessageUtil.sendMessage(e.getPlayer(), "&cYou can't break your own bed!");
                    } else {
                        e.setCancelled(true);
                    }
                    return;
                }
                return;
            }

            if (!game.getKit().getAllowBreakBlocks().contains(e.getBlock().getType())) {
                e.setCancelled(true);
            }

        } else if (pp.isInSpawn() && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void place(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());
        if (!pp.isInDuel()) return;

        Game game = pp.getCurrentGame();
        if (respawning.getOrDefault(pp.getUuid(), false)) {
            e.setCancelled(true);
            return;
        }

        if (!game.getKit().isCanBuild()) {
            e.setCancelled(true);
            return;
        }

        if (!game.getKit().getAllowBreakBlocks().contains(e.getBlock().getType())) {
            e.setCancelled(true);
        }
    }



}
