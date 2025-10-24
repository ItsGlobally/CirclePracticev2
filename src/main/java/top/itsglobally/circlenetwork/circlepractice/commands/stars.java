package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "stars")
public class stars implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;

        if (!p.hasPermission("circlepractice.admin")) {
            MessageUtil.sendMessage(p, "&d&l✗ &fNo permission!");
            return;
        }

        if (strings.length < 3) {
            MessageUtil.sendMessage(p, "&d&lUsage: &f/stars <addxp|addstar> <player> <amount>");
            return;
        }

        String subCommand = strings[0].toLowerCase();
        String targetName = strings[1];
        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null) {
            MessageUtil.sendMessage(p, "&d&l✗ &fThat player is not online!");
            return;
        }

        try {
            long amount = Long.parseLong(strings[2]);

            switch (subCommand) {
                case "addxp": {
                    plugin.getPlayerDataManager().getData(target).addXps(amount);
                    MessageUtil.sendMessage(target, "&d&l⭐ &fYou earned &d" + amount + " XP&f!");
                    MessageUtil.sendMessage(p, "&d&l✓ &fAdded &d" + amount + " XP &fto &d" + target.getName() + "&f!");
                    break;
                }
                case "addstar": {
                    plugin.getPlayerDataManager().getData(target).addStars(amount);
                    MessageUtil.sendMessage(target, "&d&l⭐ &fYou gained &d" + amount + " stars&f!");
                    MessageUtil.sendMessage(p, "&d&l✓ &fAdded &d" + amount + " stars &fto &d" + target.getName() + "&f!");
                    break;
                }
                default: {
                    MessageUtil.sendMessage(p, "&d&lUsage: &f/stars <addxp|addstar> <player> <amount>");
                }
            }
        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(p, "&d&l✗ &fInvalid amount! Please use a number.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return List.of("addxp", "addstar");
        }
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }
        if (args.length == 3) {
            return List.of("10", "50", "100", "500", "1000");
        }
        return List.of();
    }
}
