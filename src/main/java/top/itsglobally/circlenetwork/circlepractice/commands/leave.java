package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.Game;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "leave", aliases = {"l", "spawn", "lobby"})
public class leave implements NontageCommand, GlobalInterface {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;

        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (pp.isQueuing()) {
            plugin.getQueueManager().leaveQueue(p);
        }
        if (pp.isInSpawnNotQueuing()) {
            plugin.getConfigManager().teleportToSpawn(p);
            success(p, "Teleported to spawn!");
        }
        if (pp.isInDuel()) {
            Game game = pp.getCurrentGame();
            game.broadcast(game.getPrefixedTeamPlayerName(pp)
                    + " &fdisconnected");
            plugin.getGameManager().endGame(game, game.getOpponent(pp));

        }

    }
    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return List.of();
    }
}
