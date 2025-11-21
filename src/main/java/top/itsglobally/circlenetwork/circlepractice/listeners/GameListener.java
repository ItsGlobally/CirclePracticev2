package top.itsglobally.circlenetwork.circlepractice.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import top.itsglobally.circlenetwork.circlepractice.data.Game;
import top.itsglobally.circlenetwork.circlepractice.data.GameState;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.itsglobally.circlenetwork.circlepractice.utils.TeamColorUtil;
import top.nontage.nontagelib.annotations.AutoListener;

import java.util.HashMap;
import java.util.UUID;

@AutoListener
public class GameListener implements Listener, GlobalInterface {

    private final HashMap<UUID, Boolean> respawning = new HashMap<>();
    private final HashMap<UUID, Boolean> gotHitted = new HashMap<>();

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
    private void respawnPlayer(Player vic, PracticePlayer vicp, Game game, Player killer) {
        Location spawn = findSpawnpoint(game.getPlayerSpawnPoint(vicp));

        killer.showPlayer(vic);
        vic.teleport(spawn);
        vic.setAllowFlight(false);
        vic.setFlying(false);

        boolean isRedTeam = game.getPlayer1OrPlayer2(vicp) == 1;
        vic.getInventory().setArmorContents(
                TeamColorUtil.colorTeamItems(vicp.getPlayerData()
                        .getKitContents(game.getKit().getName())[1], isRedTeam)
        );
        vic.getInventory().setContents(
                TeamColorUtil.colorTeamItems(vicp.getPlayerData()
                        .getKitContents(game.getKit().getName())[0], isRedTeam)
        );
        game.setPlayerAttackable(vicp, false);
        gotHitted.put(vic.getUniqueId(), false);
        MessageUtil.sendMessage(vic, "§dYou have respawned!");
        new BukkitRunnable() {
            @Override
            public void run() {
                game.setPlayerAttackable(vicp, true);
            }
        }.runTaskLater(plugin, plugin.getConfigManager().getMainConfig().getSpawnprot() * 20L);
    }
    private Location findSpawnpoint(Location l) {

        if (l.getBlock().getType() != Material.AIR) {
            return l.clone().add(0, 1, 0);
        }
        l.clone().add(0, 2, 0).getBlock().setType(Material.AIR);
        l.clone().add(0, 3, 0).getBlock().setType(Material.AIR);
        return l;
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (!(e instanceof EntityDamageByEntityEvent event)) return;
        if (!(e.getEntity() instanceof Player vic)) return;
        if (!(event.getDamager() instanceof Player damager)) return;

        PracticePlayer vicp = plugin.getPlayerManager().getPlayer(vic);
        PracticePlayer damagerPp = plugin.getPlayerManager().getPlayer(damager);

        if (!vicp.isInDuel() || !damagerPp.isInDuel()) return;

        Game game = vicp.getCurrentGame();
        if (damagerPp.getCurrentGame() != game) return;


        if (respawning.getOrDefault(damagerPp.getUuid(), false)) {
            e.setCancelled(true);
            return;
        }

        if (!game.isPlayerAttackable(damagerPp)) {
            game.setPlayerAttackable(damagerPp, true);
            MessageUtil.sendMessage(damager, "&aSpawn protection removed because you attacked.");
        }

        if (!game.isPlayerAttackable(vicp)) {
            MessageUtil.sendMessage(damager, "&d&l✗ You can't attack this player yet!");
            e.setCancelled(true);
            return;
        }



        gotHitted.put(vic.getUniqueId(), true);
        if (!game.isPlayerAttackable(damagerPp)) game.setPlayerAttackable(damagerPp, true);
        if (game.getKit().isNodamage()) {
            e.setDamage(0.0);
        }

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
            return;
        }

        if (vic.getHealth() < e.getFinalDamage()) {
            e.setCancelled(true);
            vic.setHealth(20.0);
            vic.setFoodLevel(20);

            if (game.getKit().isRespawnable() && game.getPlayerRespawnable(vicp)) {
                gotHitted.put(vic.getUniqueId(), false);
                damager.hidePlayer(vic);
                vic.getInventory().clear();
                vic.getInventory().setArmorContents(null);
                vic.teleport(damager.getLocation());
                vic.setAllowFlight(true);
                vic.setFlying(true);

                game.broadcast(game.getPrefixedTeamPlayerName(vicp)
                        + " &fwas slain by " + game.getPrefixedTeamPlayerName(damagerPp)
                        + "&f! ");
                damager.playSound(damager.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);

                int[] countdown = {game.getKit().getRespawnTime()};
                respawning.put(vic.getUniqueId(), true);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (vicp.isInSpawn()) cancel();
                        if (countdown[0] <= 0) {
                            respawnPlayer(vic, vicp, game, damager);
                            respawning.put(vic.getUniqueId(), false);
                            cancel();
                            return;
                        }
                        MessageUtil.sendMessage(vic, "&fRespawning in &d" + countdown[0] + "s&f...");
                        MessageUtil.sendTitle(vic, "&c&lYOU DIED", "&fRespawning in &d" + countdown[0] + "s&f...");
                        countdown[0]--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);

            } else {
                damager.setHealth(20.0);
                damager.setFoodLevel(20);
                game.broadcast(game.getPrefixedTeamPlayerName(vicp)
                        + " &fwas slain by " + game.getPrefixedTeamPlayerName(damagerPp)
                        + "&f!");
                damager.playSound(damager.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                gotHitted.put(vic.getUniqueId(), false);
                gotHitted.put(damager.getUniqueId(), false);
                plugin.getGameManager().endGame(game, damagerPp);
            }
        }
    }
    @EventHandler
    public void pickup(PlayerPickupItemEvent e) {
        if (respawning.getOrDefault(e.getPlayer().getUniqueId(), false)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void move(PlayerMoveEvent e) {
        Player vic = e.getPlayer();
        PracticePlayer vicp = plugin.getPlayerManager().getPlayer(vic);
        if (!vicp.isInDuel()) return;

        Game game = vicp.getCurrentGame();

        if (game.getState() == GameState.STARTING && game.getKit().isFreezeOnCooldown()) {
            Location from = e.getFrom();
            Location to = e.getTo();
            Location fixed = from.clone();
            fixed.setYaw(to.getYaw());
            fixed.setPitch(to.getPitch());
            vic.teleport(fixed);
            return;
        }

        PracticePlayer killerPp = game.getOpponent(vicp);
        Player killer = killerPp.getPlayer();
        if (e.getPlayer().getLocation().getY() <= game.getArena().getOrgArena().getVoidY()) {
            if (respawning.getOrDefault(vic.getUniqueId(), false)) {
                vic.teleport(game.getPlayerSpawnPoint(vicp));
                return;
            }

            if (game.getKit().isRespawnable() && game.getPlayerRespawnable(vicp)) {
                vic.setHealth(20.0);
                vic.setFoodLevel(20);
                killer.hidePlayer(vic);
                vic.teleport(killer.getLocation());
                vic.setAllowFlight(true);
                vic.setFlying(true);
                vic.getInventory().clear();
                vic.getInventory().setArmorContents(null);

                game.broadcast(gotHitted.getOrDefault(vic.getUniqueId(), false) ? game.getPrefixedTeamPlayerName(vicp)
                        + " &fwas hit into the void by " + game.getPrefixedTeamPlayerName(game.getOpponent(vicp)) + "!" : "&d" + game.getPrefixedTeamPlayerName(vicp)
                        + " &ffell into the void!");
                if (gotHitted.getOrDefault(vic.getUniqueId(), false)) game.getOpponent(vicp).getPlayer().playSound(game.getOpponent(vicp).getPlayer().getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);

                int[] countdown = {game.getKit().getRespawnTime()};
                respawning.put(vic.getUniqueId(), true);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (vicp.isInSpawn()) cancel();
                        if (countdown[0] <= 0) {
                            respawnPlayer(vic, vicp, game, killer);
                            respawning.put(vic.getUniqueId(), false);
                            cancel();
                            return;
                        }
                        MessageUtil.sendMessage(vic, "&fRespawning in &d" + countdown[0] + "s&f...");
                        MessageUtil.sendTitle(vic, "&c&lYOU DIED", "&fRespawning in &d" + countdown[0] + "s&f...");
                        countdown[0]--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);

            } else {
                vic.setHealth(20.0);
                vic.setFoodLevel(20);
                killer.setHealth(20.0);
                killer.setFoodLevel(20);
                game.broadcast(gotHitted.getOrDefault(vic.getUniqueId(), false) ? game.getPrefixedTeamPlayerName(vicp)
                        + " &fwas hit into the void by " + game.getPrefixedTeamPlayerName(game.getOpponent(vicp)) + "!" : "&d" + game.getPrefixedTeamPlayerName(vicp)
                        + " &ffell into the void!");

                if (gotHitted.getOrDefault(vic.getUniqueId(), false)) game.getOpponent(vicp).getPlayer().playSound(game.getOpponent(vicp).getPlayer().getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                plugin.getGameManager().endGame(game, killerPp);
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
        gotHitted.put(vic.getUniqueId(), false);
        PracticePlayer killerPp = game.getOpponent(vicp);
        Player killer = killerPp.getPlayer();

        if (game.getKit().isRespawnable() && game.getPlayerRespawnable(vicp)) {
            vic.spigot().respawn();
            vic.setHealth(20.0);
            vic.setFoodLevel(20);
            killer.hidePlayer(vic);
            vic.teleport(killer.getLocation());
            vic.setAllowFlight(true);
            vic.setFlying(true);
            vic.getInventory().clear();
            vic.getInventory().setArmorContents(null);

            game.broadcast(game.getPrefixedTeamPlayerName(vicp)
                    + " &fwas slain by " + game.getPrefixedTeamPlayerName(killerPp)
                    + "&f!");
            killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);

            int[] countdown = {game.getKit().getRespawnTime()};
            respawning.put(vic.getUniqueId(), true);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (vicp.isInSpawn()) cancel();
                    if (countdown[0] <= 0) {
                        respawnPlayer(vic, vicp, game, killer);
                        respawning.put(vic.getUniqueId(), false);
                        cancel();
                        return;
                    }
                    MessageUtil.sendMessage(vic, "&fRespawning in &d" + countdown[0] + "s&f...");
                    MessageUtil.sendTitle(vic, "&c&lYOU DIED", "&fRespawning in &d" + countdown[0] + "s&f...");
                    countdown[0]--;
                }
            }.runTaskTimer(plugin, 0L, 20L);

        } else {
            vic.spigot().respawn();
            vic.setHealth(20.0);
            vic.setFoodLevel(20);
            killer.setHealth(20.0);
            killer.setFoodLevel(20);
            game.broadcast(game.getPrefixedTeamPlayerName(vicp)
                    + " &fwas slain by &d" + game.getPrefixedTeamPlayerName(killerPp)
                    + "&f!");
            killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);

            plugin.getGameManager().endGame(game, killerPp);
        }
    }


    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (pp.isInDuel()) {
            pp.getCurrentGame().broadcast(pp.getCurrentGame().getPrefixedTeamPlayerName(pp)
                    + " &fdisconnected");
            plugin.getGameManager().endGame(pp.getCurrentGame(), pp.getCurrentGame().getOpponent(pp));

        }

        plugin.getPlayerManager().removePlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void bbreak(BlockBreakEvent e) {
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());
        Bukkit.getLogger().info("DEBUG " + e.getPlayer().getName() + " broke " + e.getBlock().getType());
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
                        MessageUtil.sendTitle(game.getOpponent(pp).getPlayer(), "&c&lBED DESTROYED", "&fYou won't be able to respawn again!");
                        game.broadcast("&d&lBED DESTROYED &f» &d" + game.getOpponent(pp).getPlayer().getName() +
                                        "&f's bed has been destroyed by &d" + e.getPlayer().getName() + "&f!");
                        game.getOpponent(pp).getPlayer().playSound(game.getPlayer2().getPlayer().getLocation(), Sound.WITHER_DEATH, 1.0f, 1.0f);
                        e.getPlayer().playSound(game.getPlayer2().getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, 1.0f, 1.0f);
                        e.setCancelled(false);
                    } else if (isOwnBed) {
                        e.setCancelled(true);
                        MessageUtil.sendMessage(e.getPlayer(), "&d&l✗ &fYou can't break your own bed!");
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

            if (game.getState() == GameState.STARTING && game.getKit().isFreezeOnCooldown()) {
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
        if (game.getState() == GameState.STARTING && game.getKit().isFreezeOnCooldown()) {
            e.setCancelled(true);
            return;
        }
        if (e.getBlockPlaced().getY() >= game.getArena().getOrgArena().getHighLimitY()) {
            MessageUtil.sendMessage(e.getPlayer(), "&d&l✗ &fYou can't place block at build limit!");
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (pp.isInSpawn()) {
            Inventory clickedInv = e.getClickedInventory();

            if (clickedInv == null) return;
            if (clickedInv.getType() == InventoryType.CHEST) {
                e.setCancelled(true);
            }
        }
    }
}
