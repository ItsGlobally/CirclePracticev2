package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "practice")
public class practice implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;
        if (strings.length < 1) {
            String sb = "------------------------" +
                    "&dCircle Practice!" +
                    "&dDeveloper: ItsGlobally" +
                    "&dThe project is almost done by ai, thanks to them!" +
                    "------------------------";
            MessageUtil.sendMessage(p, sb);
        }
        switch (strings[0].toLowerCase()) {
            case "reload": {
                if (!p.hasPermission("circlepractice.admin")) {
                    MessageUtil.sendMessage(p, "&cNo permission!");
                    return;
                }
                plugin.getArenaManager().reload();
                plugin.getKitManager().reload();
                MessageUtil.sendMessage(p, "&aReloaded!");
            }
            default: {
                MessageUtil.sendMessage(p, "&c/practice [reload]");
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
