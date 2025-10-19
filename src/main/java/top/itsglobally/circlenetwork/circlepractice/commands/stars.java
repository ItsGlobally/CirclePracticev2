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
        if (strings.length < 3) return;
        switch (strings[0]) {
            case "addxp": {
                Player vic = Bukkit.getPlayerExact(strings[1]);
                if (vic == null) {
                    MessageUtil.sendMessage(p, "&cThat player is not online!");
                    return;
                }

                plugin.getPlayerDataManager().getData(p).addXps(Long.parseLong(strings[2]));
                MessageUtil.sendMessage(vic, "&dYou earned " + strings[2] + "xps for no reason.");
            }
            case "addstar": {
                Player vic = Bukkit.getPlayerExact(strings[1]);
                if (vic == null) {
                    MessageUtil.sendMessage(p, "&cThat player is not online!");
                    return;
                }

                plugin.getPlayerDataManager().getData(p).addStars(Long.parseLong(strings[2]));
                MessageUtil.sendMessage(vic, "&dYou upgraded to " + strings[2] + "stars for no reason.");
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
