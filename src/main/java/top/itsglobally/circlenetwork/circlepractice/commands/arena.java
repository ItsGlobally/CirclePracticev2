package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.Arena;
import top.itsglobally.circlenetwork.circlepractice.data.GameArena;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "arena")
public class arena implements NontageCommand, ICommand {

    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;

        if (!p.hasPermission("circlepractice.admin")) {
            MessageUtil.sendMessage(p, "&d&l✗ &fNo permission!");
            return;
        }

        if (strings.length < 1) {
            MessageUtil.sendMessage(p, "&d&lUsage: &f/arena <create|pos1|pos2|spec|respawnable|bnsb1|bnsb2|addkit|setVoidY|list|reload>");
            return;
        }

        String subCommand = strings[0].toLowerCase();
        switch (subCommand) {
            case "reload": {
                plugin.getArenaManager().reload();
                MessageUtil.sendMessage(p, "&d&l✓ &fReloaded all arenas!");
                break;
            }
            case "create": {
                if (strings.length < 2) return;
                String arenaName = strings[1];
                if (plugin.getArenaManager().getArena(arenaName) != null) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fArena already exists!");
                    return;
                }
                Arena newArena = new Arena(arenaName);
                newArena.setWorldName(arenaName);
                plugin.getArenaManager().addArena(newArena);
                MessageUtil.sendMessage(p, "&d&l✓ &fCreated arena &d" + arenaName + "&f!");
                break;
            }
            case "pos1": {
                if (strings.length < 2) return;
                String arenaName = strings[1];
                Arena arena = plugin.getArenaManager().getArena(arenaName);
                if (arena == null) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fArena does not exist!");
                    return;
                }
                arena.setPos1(p.getLocation());
                plugin.getArenaManager().updateArena(arena);
                MessageUtil.sendMessage(p, "&d&l✓ &fSet player 1 spawn point!");
                break;
            }
            case "pos2": {
                if (strings.length < 2) return;
                String arenaName = strings[1];
                Arena arena = plugin.getArenaManager().getArena(arenaName);
                if (arena == null) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fArena does not exist!");
                    return;
                }
                arena.setPos2(p.getLocation());
                plugin.getArenaManager().updateArena(arena);
                MessageUtil.sendMessage(p, "&d&l✓ &fSet player 2 spawn point!");
                break;
            }
            case "spec": {
                if (strings.length < 2) return;
                String arenaName = strings[1];
                Arena arena = plugin.getArenaManager().getArena(arenaName);
                if (arena == null) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fArena does not exist!");
                    return;
                }
                arena.setSpectatorSpawn(p.getLocation());
                plugin.getArenaManager().updateArena(arena);
                MessageUtil.sendMessage(p, "&d&l✓ &fSet spectator spawn point!");
                break;
            }
            case "respawnable": {
                if (strings.length < 3) return;
                String arenaName = strings[1];
                Arena arena = plugin.getArenaManager().getArena(arenaName);
                if (arena == null) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fArena does not exist!");
                    return;
                }
                boolean status = Boolean.parseBoolean(strings[2]);
                arena.setRespawnableKit(status);
                plugin.getArenaManager().updateArena(arena);
                MessageUtil.sendMessage(p, "&d&l✓ &fSet respawnable to &d" + status + "&f!");
                break;
            }
            case "remake": {
                if (strings.length < 3) return;
                String arenaName = strings[1];
                Arena arena = plugin.getArenaManager().getArena(arenaName);
                if (arena == null) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fArena does not exist!");
                    return;
                }
                boolean status = Boolean.parseBoolean(strings[2]);
                arena.setRemake(status);
                plugin.getArenaManager().updateArena(arena);
                MessageUtil.sendMessage(p, "&d&l✓ &fSet remake to &d" + status + "&f!");
                break;
            }
            case "bnsb1": {
                if (strings.length < 2) return;
                String arenaName = strings[1];
                Arena arena = plugin.getArenaManager().getArena(arenaName);
                if (arena == null) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fArena does not exist!");
                    return;
                }
                if (!arena.isRespawnableKit()) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fArena does not have respawnable enabled!");
                    return;
                }
                Location loc = p.getLocation();
                arena.setBnsb1(new Location(loc.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ(), loc.getPitch(), loc.getYaw()));
                plugin.getArenaManager().updateArena(arena);
                MessageUtil.sendMessage(p, "&d&l✓ &fSet bed block for player 1!");
                break;
            }
            case "bnsb2": {
                if (strings.length < 2) return;
                String arenaName = strings[1];
                Arena arena = plugin.getArenaManager().getArena(arenaName);
                if (arena == null) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fArena does not exist!");
                    return;
                }
                if (!arena.isRespawnableKit()) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fArena does not have respawnable enabled!");
                    return;
                }
                Location loc = p.getLocation();
                arena.setBnsb2(new Location(loc.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ(), loc.getPitch(), loc.getYaw()));
                plugin.getArenaManager().updateArena(arena);
                MessageUtil.sendMessage(p, "&d&l✓ &fSet bed block for player 2!");
                break;
            }
            case "addkit": {
                if (strings.length < 3) return;
                String arenaName = strings[1];
                String kitName = strings[2];
                Arena arena = plugin.getArenaManager().getArena(arenaName);
                if (arena == null) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fArena does not exist!");
                    return;
                }
                if (!plugin.getKitManager().kitAlreadyExist(kitName)) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fKit does not exist!");
                    return;
                }
                if (arena.getKits().contains(kitName)) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fKit already added!");
                    return;
                }
                arena.addKit(kitName);
                plugin.getArenaManager().updateArena(arena);
                MessageUtil.sendMessage(p, "&d&l✓ &fAdded kit &d" + kitName + "&f!");
                break;
            }
            case "setvoidy": {
                if (strings.length < 3) return;
                String arenaName = strings[1];
                Arena arena = plugin.getArenaManager().getArena(arenaName);
                if (arena == null) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fArena does not exist!");
                    return;
                }
                try {
                    int voidY = Integer.parseInt(strings[2]);
                    arena.setVoidY(voidY);
                    plugin.getArenaManager().updateArena(arena);
                    MessageUtil.sendMessage(p, "&d&l✓ &fSet void Y level to &d" + voidY + "&f!");
                } catch (NumberFormatException e) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fInvalid number!");
                }
                break;
            }
            case "removeallgamearenas": {
                int count = plugin.getArenaManager().getGameArenas().size();
                for (GameArena ga : plugin.getArenaManager().getGameArenas()) {
                    plugin.getArenaManager().removeGameArena(ga);
                }
                MessageUtil.sendMessage(p, "&d&l✓ &fRemoved &d" + count + " &fgame arenas!");
                break;
            }
            case "list": {
                StringBuilder sb = new StringBuilder();
                sb.append("&f&m                    &r\n");
                sb.append("&d&lArena List\n");
                sb.append("&f&m                    &r\n");
                for (Arena a : plugin.getArenaManager().getArenas()) {
                    String status = a.isComplete() ? "&d✓ Complete" : "&f✗ Incomplete";
                    sb.append("&f● &d").append(a.getName()).append(" ").append(status).append("\n");
                }
                sb.append("&f&m                    &r");
                MessageUtil.sendMessage(p, sb.toString());
                break;
            }
            default: {
                MessageUtil.sendMessage(p, "&d&lUsage: &f/arena <create|pos1|pos2|spec|respawnable|bnsb1|bnsb2|addkit|setVoidY|list|reload>");
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return List.of("create", "pos1", "pos2", "spec", "respawnable", "remake", "bnsb1", "bnsb2", "addkit", "setvoidy", "removeallgamearenas", "list", "reload");
        }
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("pos1") || subCommand.equals("pos2") ||
                subCommand.equals("spec") || subCommand.equals("respawnable") ||
                subCommand.equals("remake") || subCommand.equals("bnsb1") ||
                subCommand.equals("bnsb2") || subCommand.equals("addkit") ||
                subCommand.equals("setvoidy") || subCommand.equals("create")) {
                return plugin.getArenaManager().getArenas().stream()
                        .map(arena -> arena.getName())
                        .toList();
            }
        }
        if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("respawnable") || subCommand.equals("remake")) {
                return List.of("true", "false");
            }
            if (subCommand.equals("addkit")) {
                return plugin.getKitManager().getKits().stream()
                        .map(kit -> kit.getName())
                        .toList();
            }
        }
        return List.of();
    }
}
