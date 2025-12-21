package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "queue")
public class QueueCommand implements NontageCommand, GlobalInterface {

    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;

        if (strings.length < 1) {
            usage(p, "/queue <kit>");
            return;
        }
        String kitName = strings[0];
        if (!plugin.getKitManager().kitAlreadyExist(kitName)) {
            fail(p, "That kit does not exist!");
            return;
        }

        if (!plugin.getKitManager().getKit(kitName).isForDuels()) {
            fail(p, "That kit is not for duels!");
            return;
        }

        plugin.getQueueManager().joinQueue(p, kitName);
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
