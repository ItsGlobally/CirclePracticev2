package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.Game;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "leave", aliases = {"l", "spawn", "lobby"})
public class LeaveCommand implements NontageCommand, GlobalInterface {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;

        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (pp.isSpectating()) {
            plugin.getGameManager().stopSpec(pp.getCurrentGame(), p);
            success(p, "Teleported to spawn!");
        }
        if (pp.isQueuing()) {
            plugin.getQueueManager().leaveQueue(p);
            success(p, "Left the queue!");
            return;
        }
        if (pp.isInSpawnOrEditingNotQueuing()) {
            plugin.getConfigManager().teleportToSpawn(p);
            success(p, "Teleported to spawn!");
            return;
        }
        if (pp.isInDuel()) {
            Game game = pp.getCurrentGame();
            game.broadcast(game.getPrefixedTeamPlayerName(pp)
                    + " &fdisconnected");
            plugin.getGameManager().endGame(game, game.getOpponent(pp));
            plugin.getConfigManager().teleportToSpawn(p);
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return List.of();
    }
}
