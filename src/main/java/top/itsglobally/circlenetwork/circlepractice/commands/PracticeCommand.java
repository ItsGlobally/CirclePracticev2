package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "practice")
public class PracticeCommand implements NontageCommand, GlobalInterface {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;
        if (strings.length < 1) {
            MessageUtil.sendMessage(p,
                    """
                            &f&m                    &r
                            &d&lCircle Practice
                            &f&lDeveloper: &dItsGlobally
                            &fThe project is almost done by AI, thanks to them!
                            &f&m                    &r""");
            return;
        }
        switch (strings[0].toLowerCase()) {
            case "reload": {
                if (!p.hasPermission("circlepractice.admin")) {
                    fail(p, "No permission!");
                    return;
                }
                plugin.getArenaManager().reload();
                plugin.getKitManager().reload();
                plugin.getPlayerDataManager().reload();
                success(p, "Reloaded all configurations!");
                break;
            }
            case "saveall": {
                if (!p.hasPermission("circlepractice.admin")) {
                    fail(p, "No permission!");
                    return;
                }
                plugin.getArenaManager().saveAllArenas();
                plugin.getKitManager().saveAllKits();
                plugin.getPlayerDataManager().saveAll();
                success(p, "Saved all data!");
                break;
            }
            default: {
                usage(p, "/practice [reload|saveall]");
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
