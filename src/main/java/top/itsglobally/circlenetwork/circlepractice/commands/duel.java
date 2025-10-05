package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.itsglobally.circlenetwork.circlepractice.utils.commandUsage;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "duel")
public class duel implements NontageCommand, ICommand {

    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;

        if (strings.length < 2) {
            commandUsage.sendCommandUsage(p, "duel");
            return;
        }
        String tgn = strings[0];
        Player tg = Bukkit.getPlayerExact(tgn);
        if (tg == null) {
            MessageUtil.sendMessage(p, "&cThat player is not online!");
            return;
        }
        String kit = strings[0];
        if (!plugin.getKitManager().kitAlreadyExist(kit)) {
            MessageUtil.sendMessage(p, "&cThat kit does not exist");
            return;
        }
        if (!plugin.getKitManager().getKit(kit).isForDuels()) {
            MessageUtil.sendMessage(p, "&cThat kit is not for duels!");
            return;
        }
        plugin.getGameManager().sendDuelRequest(p, tg, kit);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
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
