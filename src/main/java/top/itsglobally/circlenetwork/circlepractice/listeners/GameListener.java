package top.itsglobally.circlenetwork.circlepractice.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import top.itsglobally.circlenetwork.circlepractice.data.*;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.itsglobally.circlenetwork.circlepractice.utils.TeamColorUtil;
import top.nontage.nontagelib.annotations.AutoListener;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AutoListener
public class GameListener implements Listener, GlobalInterface {

    private final HashMap<UUID, Boolean> respawning = new HashMap<>();
    private final HashMap<UUID, Boolean> gotHitted = new HashMap<>();

    private boolean canAttack(PracticePlayer attacker, PracticePlayer victim) {
        if (attacker.getParty() != null && victim.getParty() != null) {
            return !attacker.getParty().equals(victim.getParty());
        }
        return true;
    }

    private List<PracticePlayer> getOpponents(Game game, PracticePlayer pp) {
        if (pp.getParty() != null) {
            if (game.getTeam1() != null && game.getTeam1().containsPlayer(pp)) {
                return game.getTeam2() != null ? game.getTeam2().getPlayers() : List.of();
            }
            if (game.getTeam2() != null && game.getTeam2().containsPlayer(pp)) {
                return game.getTeam1() != null ? game.getTeam1().getPlayers() : List.of();
            }
        }
        PracticePlayer opponent = game.getOpponent(pp);
        return opponent != null ? List.of(opponent) : List.of();
    }


    @EventHandler
    public void hunger(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (pp.isSpectating() || pp.isInSpawn() || pp.isInFFA()) {
            e.setCancelled(true);
            return;
        }
        if (pp.isInDuel() && !pp.getCurrentGame().getKit().isHunger()) e.setCancelled(true);
    }

    private void respawnPlayer(Player vic, PracticePlayer vicp, Game game, Player killer, int voidadddcount) {
        Location spawn = game.getPlayerSpawnPoint(vicp);
        killer.showPlayer(vic);
        vic.teleport(spawn);
        vic.setAllowFlight(false);
        vic.setFlying(false);

        boolean isRedTeam = (game.getPlayer1OrPlayer2(vicp) == 1);
        vic.getInventory().setArmorContents(
                TeamColorUtil.colorTeamItems(vicp.getPlayerData().getKitContents(game.getKit().getName())[1], isRedTeam)
        );
        vic.getInventory().setContents(
                TeamColorUtil.colorTeamItems(vicp.getPlayerData().getKitContents(game.getKit().getName())[0], isRedTeam)
        );

        game.setPlayerAttackable(vicp, false);
        gotHitted.put(vic.getUniqueId(), false);
        if (!game.getKit().isVoidTpBack()) MessageUtil.sendMessage(vic, "§dYou have respawned!");

        new BukkitRunnable() {
            @Override
            public void run() {
                game.setPlayerAttackable(vicp, true);
            }
        }.runTaskLater(plugin, plugin.getConfigManager().getMainConfig().getSpawnprot() * 20L);

        if (game.getKit().isCountHit()) {
            game.addPlayerhit(getOpponents(game, vicp).get(0), voidadddcount);
            MessageUtil.sendMessage(vic, "&7-" + voidadddcount + " hits (fell into the void).");
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (pp.isSpectating()) e.setCancelled(true);
    }

    @EventHandler
    public void damagebye(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player vic)) return;
        if (!(e.getDamager() instanceof Player damager)) return;

        PracticePlayer vicp = plugin.getPlayerManager().getPlayer(vic);
        PracticePlayer damagerPp = plugin.getPlayerManager().getPlayer(damager);

        if (damagerPp.isSpectating()) { e.setCancelled(true); return; }
        if (!vicp.isInDuel() || !damagerPp.isInDuel()) return;
        Game game = vicp.getCurrentGame();
        if (damagerPp.getCurrentGame() != game) return;

        if (respawning.getOrDefault(damagerPp.getUuid(), false)) { e.setCancelled(true); return; }

        if (!canAttack(damagerPp, vicp)) {
            e.setCancelled(true);
            MessageUtil.sendMessage(damager, "&d&l✗ You can't attack a teammate!");
            return;
        }

        gotHitted.put(vic.getUniqueId(), true);

        if (game.getKit().isNodamage()) e.setDamage(0.0);

        if (game.getKit().isCountHit()) {
            game.addPlayerhit(damagerPp, 1);
            if (game.getPlayerhit(damagerPp) >= game.getKit().getCountHitToDie()) {
                gotHitted.put(vic.getUniqueId(), false);
                gotHitted.put(damager.getUniqueId(), false);
                game.broadcast(game.getPrefixedTeamPlayerName(vicp)
                        + " &fwas slain by " + game.getPrefixedTeamPlayerName(damagerPp)
                        + "&f!");
                plugin.getGameManager().endGame(game, damagerPp);
            }
        }

        // 改進死亡邏輯（Party 支援）
        if (vic.getHealth() <= e.getFinalDamage()) {
            e.setCancelled(true);
            vic.setHealth(20.0);
            vic.setFoodLevel(20);

            List<PracticePlayer> opponents = getOpponents(game, vicp);

            if (game.getKit().isRespawnable() && game.getPlayerRespawnable(vicp)) {
                gotHitted.put(vic.getUniqueId(), false);
                damager.hidePlayer(vic);
                vic.getInventory().clear();
                vic.getInventory().setArmorContents(null);
                vic.teleport(damager.getLocation());
                vic.setAllowFlight(true);
                vic.setFlying(true);

                game.broadcast(game.getPrefixedTeamPlayerName(vicp)
                        + " &fwas slain by " + game.getPrefixedTeamPlayerName(damagerPp) + "&f!");

                int[] countdown = {game.getKit().getRespawnTime()};
                respawning.put(vic.getUniqueId(), true);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (vicp.isInSpawn()) cancel();
                        if (countdown[0] <= 0) {
                            respawnPlayer(vic, vicp, game, damager, 0);
                            respawning.put(vic.getUniqueId(), false);
                            cancel();
                        } else {
                            MessageUtil.sendMessage(vic, "&fRespawning in &d" + countdown[0] + "s&f...");
                            MessageUtil.sendTitle(vic, "&c&lYOU DIED", "&fRespawning in &d" + countdown[0] + "s&f...");
                            countdown[0]--;
                        }
                    }
                }.runTaskTimer(plugin, 0L, 20L);
            } else {
                vic.setHealth(20.0);
                vic.setFoodLevel(20);
                damager.setHealth(20.0);
                damager.setFoodLevel(20);
                game.broadcast(game.getPrefixedTeamPlayerName(vicp)
                        + " &fwas slain by " + game.getPrefixedTeamPlayerName(damagerPp)
                        + "&f!");
                gotHitted.put(vic.getUniqueId(), false);
                gotHitted.put(damager.getUniqueId(), false);
                vicp.getPlayerData().getFinalKillParticle().play(vic.getLocation());
                plugin.getGameManager().endGame(game, damagerPp);
            }
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (!pp.isInDuel()) return;

        Game game = pp.getCurrentGame();

        // Freeze on starting
        if (game.getState() == GameState.STARTING && game.getKit().isFreezeOnCooldown()) {
            Location from = e.getFrom();
            Location to = e.getTo();
            if (!from.getWorld().equals(to.getWorld())
                    || from.distanceSquared(to) > 0.01) {
                Location fixed = from.clone();
                fixed.setYaw(to.getYaw());
                fixed.setPitch(to.getPitch());
                p.teleport(fixed);
            }
            return;
        }

        // Void handling
        if (p.getLocation().getY() <= game.getArena().getOrgArena().getVoidY()) {
            if (respawning.getOrDefault(pp.getUuid(), false)) {
                p.teleport(game.getPlayerSpawnPoint(pp));
                return;
            }

            Player killer = getOpponents(game, pp).get(0).getPlayer();
            if (game.getKit().isVoidTpBack()) {
                respawnPlayer(p, pp, game, killer, game.getKit().getVoidaddcount());
                return;
            }

            if (game.getKit().isRespawnable() && game.getPlayerRespawnable(pp)) {
                game.broadcast(gotHitted.getOrDefault(p.getUniqueId(), false)
                        ? game.getPrefixedTeamPlayerName(pp) + " &fwas hit into the void!"
                        : "&d" + game.getPrefixedTeamPlayerName(pp) + " &ffell into the void!");
                p.setHealth(20.0);
                p.setFoodLevel(20);
                killer.hidePlayer(p);
                p.teleport(killer.getLocation());
                p.setAllowFlight(true);
                p.setFlying(true);
                p.getInventory().clear();
                p.getInventory().setArmorContents(null);

                int[] countdown = {game.getKit().getRespawnTime()};
                respawning.put(p.getUniqueId(), true);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (pp.isInSpawn()) cancel();
                        if (countdown[0] <= 0) {
                            respawnPlayer(p, pp, game, killer, game.getKit().getVoidaddcount());
                            respawning.put(p.getUniqueId(), false);
                            cancel();
                        } else {
                            MessageUtil.sendMessage(p, "&fRespawning in &d" + countdown[0] + "s&f...");
                            MessageUtil.sendTitle(p, "&c&lYOU DIED", "&fRespawning in &d" + countdown[0] + "s&f...");
                            countdown[0]--;
                        }
                    }
                }.runTaskTimer(plugin, 0L, 20L);
            } else {
                p.setHealth(20.0);
                p.setFoodLevel(20);
                killer.setHealth(20.0);
                killer.setFoodLevel(20);
                game.broadcast(pp.getPlayer().getName() + " &ffell into the void!");
                pp.getPlayerData().getFinalKillParticle().play(p.getLocation());
                plugin.getGameManager().endGame(game, getOpponents(game, pp).get(0));
            }
        }
    }

    @EventHandler
    public void died(PlayerDeathEvent e) {
        Player vic = e.getEntity();
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(vic);
        if (!pp.isInDuel()) return;
        Game game = pp.getCurrentGame();

        e.setDeathMessage(null);
        e.getDrops().clear();
        e.setDroppedExp(0);
        gotHitted.put(vic.getUniqueId(), false);

        Player killer = getOpponents(game, pp).get(0).getPlayer();

        if (game.getKit().isRespawnable() && game.getPlayerRespawnable(pp)) {
            vic.spigot().respawn();
            vic.setHealth(20.0);
            vic.setFoodLevel(20);
            killer.hidePlayer(vic);
            vic.teleport(killer.getLocation());
            vic.setAllowFlight(true);
            vic.setFlying(true);
            vic.getInventory().clear();
            vic.getInventory().setArmorContents(null);

            game.broadcast(game.getPrefixedTeamPlayerName(pp)
                    + " &fwas slain by " + game.getPrefixedTeamPlayerName(plugin.getPlayerManager().getPlayer(killer))
                    + "&f!");
            killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);

            int[] countdown = {game.getKit().getRespawnTime()};
            respawning.put(vic.getUniqueId(), true);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (pp.isInSpawn()) cancel();
                    if (countdown[0] <= 0) {
                        respawnPlayer(vic, pp, game, killer, 0);
                        respawning.put(vic.getUniqueId(), false);
                        cancel();
                    } else {
                        MessageUtil.sendMessage(vic, "&fRespawning in &d" + countdown[0] + "s&f...");
                        MessageUtil.sendTitle(vic, "&c&lYOU DIED", "&fRespawning in &d" + countdown[0] + "s&f...");
                        countdown[0]--;
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);
        } else {
            vic.spigot().respawn();
            vic.setHealth(20.0);
            vic.setFoodLevel(20);
            killer.setHealth(20.0);
            killer.setFoodLevel(20);
            game.broadcast(pp.getPlayer().getName() + " &fwas slain!");
            pp.getPlayerData().getFinalKillParticle().play(vic.getLocation());
            plugin.getGameManager().endGame(game, plugin.getPlayerManager().getPlayer(killer));
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (pp.isInDuel()) {
            pp.getCurrentGame().broadcast(pp.getCurrentGame().getPrefixedTeamPlayerName(pp)
                    + " &fdisconnected");
            plugin.getGameManager().endGame(pp.getCurrentGame(), getOpponents(pp.getCurrentGame(), pp).get(0));
        }
        plugin.getPlayerManager().removePlayer(p.getUniqueId());
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        if (respawning.getOrDefault(e.getPlayer().getUniqueId(), false)) e.setCancelled(true);
    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent e) {
        if (respawning.getOrDefault(e.getPlayer().getUniqueId(), false)) e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (!pp.isInSpawn()) return;

        Inventory inv = e.getClickedInventory();
        if (inv != null && inv.getType() == InventoryType.CHEST) e.setCancelled(true);
    }

    @EventHandler
    public void bbreak(BlockBreakEvent e) {
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());
        if (!pp.isInDuel()) return;
        Game game = pp.getCurrentGame();
        if (respawning.getOrDefault(pp.getUuid(), false)) { e.setCancelled(true); return; }
        if (!game.getKit().isCanBuild()) { e.setCancelled(true); return; }
        if (game.getState() == GameState.STARTING && game.getKit().isFreezeOnCooldown()) { e.setCancelled(true); return; }
    }

    @EventHandler
    public void place(BlockPlaceEvent e) {
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());
        if (!pp.isInDuel()) return;
        Game game = pp.getCurrentGame();
        if (respawning.getOrDefault(pp.getUuid(), false)) { e.setCancelled(true); return; }
        if (!game.getKit().isCanBuild()) { e.setCancelled(true); return; }
        if (game.getState() == GameState.STARTING && game.getKit().isFreezeOnCooldown()) { e.setCancelled(true); return; }
    }
}