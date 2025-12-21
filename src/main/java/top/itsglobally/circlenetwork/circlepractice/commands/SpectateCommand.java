package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.Game;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "spectate", aliases = {"spec"})
public class SpectateCommand implements NontageCommand, GlobalInterface {

    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;
        if (strings.length < 1) {
            usage(p, "/spectate <player>");
            return;
        }
        Player target = Bukkit.getPlayer(strings[0]);
        if (target == null) {
            fail(p, "This player is not online.");
            return;
        }
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        PracticePlayer targetpp = plugin.getPlayerManager().getPlayer(p);
        if (!pp.isInSpawnNotEditingOrQueuing()) {
            fail(p, "You're not in the spawn.");
            return;
        }
        if (targetpp.isSpectating()) {
            fail(p, "This player is spectating other games.");
            return;
        }
        if (!targetpp.isInDuel()) {
            fail(p, "This player is not in a game.");
            return;
        }
        Game game = targetpp.getCurrentGame();
        plugin.getGameManager().joinSpec(game, p);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }
        return List.of();
    }
}
