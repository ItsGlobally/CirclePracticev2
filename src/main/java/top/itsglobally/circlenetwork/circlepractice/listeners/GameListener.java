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
import top.itsglobally.circlenetwork.circlepractice.data.Game;
import top.itsglobally.circlenetwork.circlepractice.data.GameState;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.itsglobally.circlenetwork.circlepractice.handlers.GameHandler;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.AutoListener;

import java.util.Collection;

@AutoListener
public class GameListener implements Listener, GlobalInterface {

    @EventHandler
    public void hunger(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (pp.isSpectating()) {
            e.setCancelled(true);
            return;
        }
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

        if (damagerPp.isSpectating()) {
            e.setCancelled(true);
            return;
        }

        if (!vicp.isInDuel() || !damagerPp.isInDuel()) return;
        Game game = vicp.getCurrentGame();
        if (damagerPp.getCurrentGame() != game) return;

        if (game.respawning.getOrDefault(damagerPp.getUuid(), false)) {
            e.setCancelled(true);
            return;
        }
        if (!game.isPlayerAttackable(damagerPp)) {
            game.setPlayerAttackable(damagerPp, true);
            MessageUtil.sendMessage(damager, "&dSpawn protection removed because you attacked.");
        }

        if (!game.isPlayerAttackable(vicp)) {
            MessageUtil.sendMessage(damager, "&d&l✗ You can't attack this player yet!");
            e.setCancelled(true);
            return;
        }


        if (game.getKit().isNodamage()) e.setDamage(0.0);

        if (game.getKit().isCountHit()) {
            game.addPlayerhit(damagerPp, 1);
            if (game.getPlayerhit(damagerPp) >= game.getKit().getCountHitToDie()) {
                game.getHandler().onKill(vicp, damagerPp, GameHandler.KillReason.KILL);
            }
            return;
        }

        game.getLastHit().put(vic.getUniqueId(), damagerPp);

        if (vic.getHealth() <= e.getFinalDamage()) {
            e.setCancelled(true);
            game.getHandler().onKill(vicp, damagerPp, GameHandler.KillReason.KILL);
        }
    }


    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());
        if (pp.isInDuel()) {
            Game game = pp.getCurrentGame();
            if (game.respawning.getOrDefault(e.getPlayer().getUniqueId(), false)) {
                e.setCancelled(true);
                return;
            }
        }
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (plugin.getPlayerManager().getPlayer(e.getPlayer()).isInSpawn()) e.setCancelled(true);
    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent e) {
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());
        if (pp.isInDuel()) {
            Game game = pp.getCurrentGame();
            if (game.respawning.getOrDefault(e.getPlayer().getUniqueId(), false)) {
                e.setCancelled(true);
                return;
            }
        }
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (plugin.getPlayerManager().getPlayer(e.getPlayer()).isInSpawn()) e.setCancelled(true);
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

            if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                vic.teleport(from.clone().setDirection(from.getDirection()));
            }
            return;
        }


        if (vic.getLocation().getY() <= game.getArena().getOrgArena().getVoidY()) {
            PracticePlayer killerPp = game.getLasthit().get(vicp.getUuid());
            if (killerPp == null) {
                game.getHandler().onKill(vicp, null, GameHandler.KillReason.VOID);
                return;
            }
            Player killer = killerPp.getPlayer();

            if (game.respawning.getOrDefault(vic.getUniqueId(), false)) {
                vic.teleport(game.getPlayerSpawnPoint(vicp));
                return;
            }

            if (game.getKit().isVoidTpBack()) {
                game.getHandler().respawnPlayer(vic, vicp, game, killer, game.getKit().getVoidaddcount());
                return;
            }
            game.getHandler().onKill(vicp, killerPp, GameHandler.KillReason.VOID);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (pp.isInDuel()) {
            pp.getCurrentGame().broadcast(pp.getCurrentGame().getPrefixedTeamPlayerName(pp)
                    + " &fdisconnected");
            pp.getCurrentGame().removePlayer(pp);

        }

        plugin.getPlayerManager().removePlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void bbreak(BlockBreakEvent e) {
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());
        if (pp == null) return;

        Bukkit.getLogger().info("DEBUG " + e.getPlayer().getName() + " broke " + e.getBlock().getType());
        if (!pp.isInDuel()) {
            if (pp.isInSpawn() && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
            }
            return;
        }

        Game game = pp.getCurrentGame();
        if (game.respawning.getOrDefault(pp.getUuid(), false)) {
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

        Material bedType = game.getKit().getBrokeToNoSpawn();

        if (bedType != null && e.getBlock().getType() == bedType) {

            Location loc = e.getBlock().getLocation();
            boolean isEnemyBed = game.getIsEnemyBed(pp, loc);
            boolean isOwnBed = game.getIsOwnBed(pp, loc);

            if (isEnemyBed) {

                Collection<PracticePlayer> enemyTeam = game.getEnemyTeam(pp);

                for (PracticePlayer enemy : enemyTeam) {
                    game.setRespawnable(enemy, false);

                    MessageUtil.sendTitle(
                            enemy.getPlayer(),
                            "&c&lBED DESTROYED",
                            "&fYou won't be able to respawn again!"
                    );

                    enemy.getPlayer().playSound(
                            enemy.getPlayer().getLocation(),
                            Sound.WITHER_DEATH,
                            1.0f,
                            1.0f
                    );
                }

                game.broadcast(
                        "&d&lBED DESTROYED &f» " +
                                (game.isRed(pp) ? "&9Blue" : "&cRed") +
                                " &fteam's bed has been destroyed by &d" +
                                e.getPlayer().getName() + "&f!"
                );

                e.getPlayer().playSound(
                        e.getPlayer().getLocation(),
                        Sound.ENDERDRAGON_GROWL,
                        1.0f,
                        1.0f
                );

                pp.getPlayerData().getFinalKillParticle().play(e.getPlayer().getLocation());
                e.setCancelled(false);
                return;
            }

            if (isOwnBed) {
                e.setCancelled(true);
                MessageUtil.sendMessage(e.getPlayer(), "&d&l✗ &fYou can't break your own bed!");
                return;
            }

            e.setCancelled(true);
            return;
        }
        if (!game.getKit().getAllowBreakBlocks().contains(e.getBlock().getType())) {
            e.setCancelled(true);
        }
    }



    @EventHandler
    public void place(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        PracticePlayer pp = plugin.getPlayerManager().getPlayer(e.getPlayer());
        if (!pp.isInDuel()) return;

        Game game = pp.getCurrentGame();
        if (game.respawning.getOrDefault(pp.getUuid(), false)) {
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
