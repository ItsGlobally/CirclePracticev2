package top.itsglobally.circlenetwork.circlepractice.handlers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import top.itsglobally.circlenetwork.circlepractice.data.Game;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.itsglobally.circlenetwork.circlepractice.utils.TeamColorUtil;

public class GameHandler implements GlobalInterface {
    private final Game game;

    public GameHandler(Game g) {
        this.game = g;
    }

    private Location findSpawnpoint(Location l) {

        if (l.getBlock().getType() != Material.AIR) {
            return l.clone().add(0, 1, 0);
        }
        l.clone().add(0, 2, 0).getBlock().setType(Material.AIR);
        l.clone().add(0, 3, 0).getBlock().setType(Material.AIR);
        return l;
    }

    public void respawnPlayer(Player vic, PracticePlayer vicp, Game game, Player killer, int voidadddcount) {
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
        game.gotHitted.put(vic.getUniqueId(), false);
        if (!game.getKit().isVoidTpBack()) MessageUtil.sendMessage(vic, "§dYou have respawned!");
        new BukkitRunnable() {
            @Override
            public void run() {
                game.setPlayerAttackable(vicp, true);
            }
        }.runTaskLater(plugin, plugin.getConfigManager().getMainConfig().getSpawnprot() * 20L);
        if (game.getKit().isCountHit()) {
            MessageUtil.sendMessage(vic, "&7-" + voidadddcount + " hits (fell into the void).");
            game.addPlayerhit(game.getOpponent(vicp), voidadddcount);
        }
    }

    public void onKill(PracticePlayer victimpp, PracticePlayer killerpp, KillReason kr) {
        Player victim = victimpp.getPlayer();
        Player killer = killerpp.getPlayer();
        if (game.getKit().isRespawnable() && game.getPlayerRespawnable(victimpp)) {
            victim.spigot().respawn();
            victim.setHealth(20.0);
            victim.setFoodLevel(20);
            killer.hidePlayer(victim);
            victim.teleport(killer.getLocation());
            victim.setAllowFlight(true);
            victim.setFlying(true);
            victim.getInventory().clear();
            victim.getInventory().setArmorContents(null);

            game.broadcast(game.getPrefixedTeamPlayerName(victimpp)
                    + " &fwas slain by " + game.getPrefixedTeamPlayerName(killerpp)
                    + "&f!");
            killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);

            int[] countdown = {game.getKit().getRespawnTime()};
            game.respawning.put(victim.getUniqueId(), true);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (victimpp.isInSpawn()) cancel();
                    if (countdown[0] <= 0) {
                        respawnPlayer(victim, victimpp, game, killer, 0);
                        game.respawning.put(victim.getUniqueId(), false);
                        cancel();
                        return;
                    }
                    MessageUtil.sendMessage(victim, "&fRespawning in &d" + countdown[0] + "s&f...");
                    MessageUtil.sendTitle(victim, "&c&lYOU DIED", "&fRespawning in &d" + countdown[0] + "s&f...");
                    countdown[0]--;
                }
            }.runTaskTimer(plugin, 0L, 20L);

        } else {
            victim.spigot().respawn();
            victim.setHealth(20.0);
            victim.setFoodLevel(20);
            killer.setHealth(20.0);
            killer.setFoodLevel(20);

            if (kr == KillReason.KILL) game.broadcast(game.getPrefixedTeamPlayerName(victimpp)
                    + " &fwas slain by &d" + game.getPrefixedTeamPlayerName(killerpp)
                    + "&f!");
            else {
                game.broadcast(game.gotHitted.getOrDefault(victim.getUniqueId(), false)
                        ? game.getPrefixedTeamPlayerName(victimpp) + " &fwas hit into the void by " + game.getPrefixedTeamPlayerName(killerpp) + "!"
                        : "&d" + game.getPrefixedTeamPlayerName(victimpp) + " &ffell into the void!");

            }

            killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
            victimpp.getPlayerData().getFinalKillParticle().play(victim.getLocation());
            victim.playSound(victim.getLocation(), Sound.HURT_FLESH, 1.0f, 1.0f);
            plugin.getGameManager().endGame(game, killerpp);
        }
    }

    public enum KillReason {
        KILL, VOID
    }

}
