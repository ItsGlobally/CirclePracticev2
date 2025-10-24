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
            MessageUtil.sendMessage(p,
                    "&f&m                    &r\n" +
                    "&d&lCircle Practice\n" +
                    "&f&lDeveloper: &dItsGlobally\n" +
                    "&fThe project is almost done by AI, thanks to them!\n" +
                    "&f&m                    &r");
            return;
        }
        switch (strings[0].toLowerCase()) {
            case "reload": {
                if (!p.hasPermission("circlepractice.admin")) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fNo permission!");
                    return;
                }
                plugin.getArenaManager().reload();
                plugin.getKitManager().reload();
                plugin.getPlayerDataManager().reload();
                MessageUtil.sendMessage(p, "&d&l✓ &fReloaded all configurations!");
                break;
            }
            case "saveall": {
                if (!p.hasPermission("circlepractice.admin")) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fNo permission!");
                    return;
                }
                plugin.getArenaManager().saveAllArenas();
                plugin.getKitManager().saveAllKits();
                plugin.getPlayerDataManager().saveAll();
                MessageUtil.sendMessage(p, "&d&l✓ &fSaved all data!");
                break;
            }
            default: {
                MessageUtil.sendMessage(p, "&d&lUsage: &f/practice [reload|saveall]");
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return List.of("reload", "saveall");
        }
        return List.of();
    }
}
