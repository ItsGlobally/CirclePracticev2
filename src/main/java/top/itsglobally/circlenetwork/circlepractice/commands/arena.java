package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.Arena;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "arena")
public class arena implements NontageCommand, ICommand {

    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;
        if (strings.length < 2) return;
        String a1 = strings[1];
        switch (strings[0]) {
            case "create": {
                if (plugin.getArenaManager().getArena(a1) != null) {
                    MessageUtil.sendMessage(p, "&cArena already exist!");
                    return;
                }
                Arena newA = new Arena(a1);
                newA.setWorldName(a1);
                plugin.getArenaManager().addArena(newA);
            }
            case "pos1": {
                if (plugin.getArenaManager().getArena(a1) == null) {
                    MessageUtil.sendMessage(p, "&cArena not exist!");
                    return;
                }
                Arena a = plugin.getArenaManager().getArena(a1);
                a.setPos1(p.getLocation());
            }
            case "pos2": {
                if (plugin.getArenaManager().getArena(a1) == null) {
                    MessageUtil.sendMessage(p, "&cArena not exist!");
                    return;
                }
                Arena a = plugin.getArenaManager().getArena(a1);
                a.setPos2(p.getLocation());
            }
            case "spec": {
                if (plugin.getArenaManager().getArena(a1) == null) {
                    MessageUtil.sendMessage(p, "&cArena not exist!");
                    return;
                }
                Arena a = plugin.getArenaManager().getArena(a1);
                a.setSpectatorSpawn(p.getLocation());
            }
            case "list": {
                StringBuilder sb = new StringBuilder();
                sb.append("----------------------\n&r");
                for (Arena a : plugin.getArenaManager().getArenas()) {
                    sb.append(a.getName()).append(" ").append(a.isComplete() ? "&aComplete" : "&cIncomplete").append("\n&r");
                }
                sb.append("----------------------\n&r");
                MessageUtil.sendMessage(p, sb.toString());

            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
