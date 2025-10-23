package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "duel")
public class duel implements NontageCommand, ICommand {

    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;

        if (strings.length < 2) {
            MessageUtil.sendMessage(p, "&d&lUsage: &f/duel <player> <kit>");
            return;
        }

        String targetName = strings[0];
        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null) {
            MessageUtil.sendMessage(p, "&d&l✗ &fThat player is not online!");
            return;
        }

        String kitName = strings[1];
        if (!plugin.getKitManager().kitAlreadyExist(kitName)) {
            MessageUtil.sendMessage(p, "&d&l✗ &fThat kit does not exist!");
            return;
        }

        if (!plugin.getKitManager().getKit(kitName).isForDuels()) {
            MessageUtil.sendMessage(p, "&d&l✗ &fThat kit is not for duels!");
            return;
        }

        plugin.getGameManager().sendDuelRequest(p, target, kitName);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }
        if (args.length == 2) {
            return plugin.getKitManager().getKits().stream()
                    .map(Kit::getName)
                    .toList();
        }
        return List.of();
    }
}
