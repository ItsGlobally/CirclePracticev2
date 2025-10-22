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
        switch (strings[0]) {
            case "reload": {
                plugin.getArenaManager().reload();
                break;
            }
            case "create": {
                if (strings.length < 2) return;
                String a1 = strings[1];
                if (plugin.getArenaManager().getArena(a1) != null) {
                    MessageUtil.sendMessage(p, "&cArena already exist!");
                    return;
                }
                Arena newA = new Arena(a1);
                newA.setWorldName(a1);
                plugin.getArenaManager().addArena(newA);
                break;
            }
            case "pos1": {
                if (strings.length < 2) return;
                String a1 = strings[1];
                if (plugin.getArenaManager().getArena(a1) == null) {
                    MessageUtil.sendMessage(p, "&cArena not exist!");
                    return;
                }
                Arena a = plugin.getArenaManager().getArena(a1);
                a.setPos1(p.getLocation());
                plugin.getArenaManager().updateArena(a);
                MessageUtil.sendMessage(p, "&aSet player 1 spawnpoint!");
                break;
            }
            case "pos2": {
                if (strings.length < 2) return;
                String a1 = strings[1];
                if (plugin.getArenaManager().getArena(a1) == null) {
                    MessageUtil.sendMessage(p, "&cArena not exist!");
                    return;
                }
                Arena a = plugin.getArenaManager().getArena(a1);
                a.setPos2(p.getLocation());
                plugin.getArenaManager().updateArena(a);
                MessageUtil.sendMessage(p, "&aSet player 2 spawnpoint!");
                break;
            }
            case "spec": {
                if (strings.length < 2) return;
                String a1 = strings[1];
                if (plugin.getArenaManager().getArena(a1) == null) {
                    MessageUtil.sendMessage(p, "&cArena not exist!");
                    return;
                }
                Arena a = plugin.getArenaManager().getArena(a1);
                a.setSpectatorSpawn(p.getLocation());
                plugin.getArenaManager().updateArena(a);
                MessageUtil.sendMessage(p, "&aSet spec pos!");
                break;
            }
            case "respawnable": {
                if (strings.length < 3) return;
                String a1 = strings[1];
                String a2 = strings[2];
                if (plugin.getArenaManager().getArena(a1) == null) {
                    MessageUtil.sendMessage(p, "&cArena not exist!");
                    return;
                }
                Arena a = plugin.getArenaManager().getArena(a1);
                boolean status = Boolean.parseBoolean(a2);
                a.setRespawnableKit(status);
                plugin.getArenaManager().updateArena(a);
                MessageUtil.sendMessage(p, "&aSet respawnable to " + status + "!");
                break;

            }
            case "bnsb1": {
                if (strings.length < 2) return;
                String a1 = strings[1];
                if (plugin.getArenaManager().getArena(a1) == null) {
                    MessageUtil.sendMessage(p, "&cArena not exist!");
                    return;
                }
                Arena a = plugin.getArenaManager().getArena(a1);
                if (!a.isRespawnableKit()) {
                    MessageUtil.sendMessage(p, "&cArena not enabled respawnable!");
                    return;
                }
                Location l = p.getLocation();
                a.setBnsb1(new Location(l.getWorld(), l.getX(), l.getY() - 1, l.getZ(), l.getPitch(), l.getYaw()));
                plugin.getArenaManager().updateArena(a);
                MessageUtil.sendMessage(p, "&aSet block(bed) for player 1!");
                break;
            }
            case "bnsb2": {
                if (strings.length < 2) return;
                String a1 = strings[1];
                if (plugin.getArenaManager().getArena(a1) == null) {
                    MessageUtil.sendMessage(p, "&cArena not exist!");
                    return;
                }
                Arena a = plugin.getArenaManager().getArena(a1);
                if (!a.isRespawnableKit()) {
                    MessageUtil.sendMessage(p, "&cArena not enabled respawnable!");
                    return;
                }
                Location l = p.getLocation();
                a.setBnsb2(new Location(l.getWorld(), l.getX(), l.getY() - 1, l.getZ(), l.getPitch(), l.getYaw()));
                plugin.getArenaManager().updateArena(a);
                MessageUtil.sendMessage(p, "&aSet block(bed) for player 2!");
                break;
            }
            case "addkit": {
                if (strings.length < 3) return;
                String a1 = strings[1];
                String a2 = strings[2];
                if (plugin.getArenaManager().getArena(a1) == null) {
                    MessageUtil.sendMessage(p, "&cArena not exist!");
                    return;
                }
                if (!plugin.getKitManager().kitAlreadyExist(a2)) {
                    MessageUtil.sendMessage(p, "&cKit not exist!");
                    return;
                }
                Arena a = plugin.getArenaManager().getArena(a1);
                if (a.getKits().contains(a2)) {
                    MessageUtil.sendMessage(p, "&cKit already added!");
                    return;
                }
                a.addKit(a2);
                plugin.getArenaManager().updateArena(a);
                MessageUtil.sendMessage(p, "&aAdded kit " + a2 + "!");
                break;
            }
            case "setVoidY": {
                if (strings.length < 3) return;
                String a1 = strings[1];
                String a2 = strings[2];
                if (plugin.getArenaManager().getArena(a1) == null) {
                    MessageUtil.sendMessage(p, "&cArena not exist!");
                    return;
                }
                Arena a = plugin.getArenaManager().getArena(a1);
                a.setVoidY(Integer.parseInt(a2));
                MessageUtil.sendMessage(p, "&aSet void y level!");
            }
            case "list": {
                StringBuilder sb = new StringBuilder();
                sb.append("----------------------\n&r");
                for (Arena a : plugin.getArenaManager().getArenas()) {
                    sb.append(a.getName()).append(" ").append(a.isComplete() ? "&aComplete" : "&cIncomplete").append("\n&r");
                }
                sb.append("----------------------\n&r");
                MessageUtil.sendMessage(p, sb.toString());
                break;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
