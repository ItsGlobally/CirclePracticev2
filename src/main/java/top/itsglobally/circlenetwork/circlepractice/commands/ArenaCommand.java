package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.Arena;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "arena")
public class ArenaCommand implements NontageCommand, GlobalInterface {

    @Override
    public void execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player p)) return;

        if (!p.hasPermission("circlepractice.admin")) {
            fail(p, "No permission!");
            return;
        }

        if (args.length < 1) {
            sendUsage(p);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> handleCreate(p, args);
            case "set" -> handleSet(p, args);
            case "pos1" -> handlePosition(p, args, true);
            case "pos2" -> handlePosition(p, args, false);
            case "spec" -> handleSpectatorSpawn(p, args);
            case "bnsb1" -> handleBedSpawn(p, args, true);
            case "bnsb2" -> handleBedSpawn(p, args, false);
            case "addkit" -> handleAddKit(p, args);
            case "info" -> handleInfo(p, args);
            case "list" -> handleList(p);
            case "saveall" -> handleSaveAll(p);
            case "reload" -> handleReload(p);
            case "removeallgamearenas" -> handleRemoveAllGameArenas(p);
            default -> sendUsage(p);
        }
    }

    private void handleCreate(Player p, String[] args) {
        if (args.length < 2) {
            usage(p, "&f/arena create <arenaname>");
            return;
        }
        String arenaName = args[1];
        if (plugin.getArenaManager().getArena(arenaName) != null) {
            fail(p, "Arena already exists!");
            return;
        }
        Arena newArena = new Arena(arenaName);
        newArena.setWorldName(arenaName);
        plugin.getArenaManager().addArena(newArena);
        success(p, "Created arena &d" + arenaName + "&f!");
    }

    private void handleSet(Player p, String[] args) {
        if (args.length < 4) {
            usage(p, "&f/arena set <arenaname> <field> <value>");
            return;
        }

        String arenaName = args[1];
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            fail(p, "Arena does not exist!");
            return;
        }

        String field = args[2].toLowerCase();
        String value = args[3];

        try {
            switch (field) {
                case "respawnable" -> arena.setRespawnableKit(Boolean.parseBoolean(value));
                case "remake" -> arena.setRemake(Boolean.parseBoolean(value));
                case "voidy" -> arena.setVoidY(Integer.parseInt(value));
                case "highlimity" -> arena.setHighLimitY(Integer.parseInt(value));
                case "worldname" -> arena.setWorldName(value);
                default -> {
                    fail(p, "Unknown field: &d" + field);
                    MessageUtil.sendMessage(p, "&fAvailable fields: &drespawnable, remake, voidy, highlimity, worldname");
                    return;
                }
            }
            plugin.getArenaManager().updateArena(arena);
            success(p, "Updated field &d" + field + "&f of arena &d" + arena.getName() + "&f to &d" + value + "&f!");
        } catch (NumberFormatException e) {
            fail(p, "Invalid value for field &d" + field + "&f!");
        }
    }

    private void handlePosition(Player p, String[] args, boolean isPos1) {
        if (args.length < 2) {
            usage(p, "&f/arena " + (isPos1 ? "pos1" : "pos2") + " <arenaname>");
            return;
        }
        Arena arena = plugin.getArenaManager().getArena(args[1]);
        if (arena == null) {
            fail(p, "Arena does not exist!");
            return;
        }
        if (isPos1) {
            arena.setPos1(p.getLocation());
        } else {
            arena.setPos2(p.getLocation());
        }
        plugin.getArenaManager().updateArena(arena);
        success(p, "Set player " + (isPos1 ? "1" : "2") + " spawn point!");
    }

    private void handleSpectatorSpawn(Player p, String[] args) {
        if (args.length < 2) {
            usage(p, "&f/arena spec <arenaname>");
            return;
        }
        Arena arena = plugin.getArenaManager().getArena(args[1]);
        if (arena == null) {
            fail(p, "Arena does not exist!");
            return;
        }
        arena.setSpectatorSpawn(p.getLocation());
        plugin.getArenaManager().updateArena(arena);
        success(p, "Set spectator spawn point!");
    }

    private void handleBedSpawn(Player p, String[] args, boolean isBed1) {
        if (args.length < 2) {
            usage(p, "&f/arena " + (isBed1 ? "bnsb1" : "bnsb2") + " <arenaname>");
            return;
        }
        Arena arena = plugin.getArenaManager().getArena(args[1]);
        if (arena == null) {
            fail(p, "Arena does not exist!");
            return;
        }
        if (!arena.isRespawnableKit()) {
            fail(p, "Arena does not have respawnable enabled!");
            return;
        }
        Location loc = p.getLocation();
        Location bedLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ(), loc.getPitch(), loc.getYaw());
        if (isBed1) {
            arena.setBnsb1(bedLoc);
        } else {
            arena.setBnsb2(bedLoc);
        }
        plugin.getArenaManager().updateArena(arena);
        success(p, "Set bed block for player " + (isBed1 ? "1" : "2") + "!");
    }

    private void handleAddKit(Player p, String[] args) {
        if (args.length < 3) {
            usage(p, "&f/arena addkit <arenaname> <kitname>");
            return;
        }
        Arena arena = plugin.getArenaManager().getArena(args[1]);
        if (arena == null) {
            fail(p, "Arena does not exist!");
            return;
        }
        String kitName = args[2];
        if (!plugin.getKitManager().kitAlreadyExist(kitName)) {
            fail(p, "Kit does not exist!");
            return;
        }
        if (arena.getKits().contains(kitName)) {
            fail(p, "Kit already added!");
            return;
        }
        arena.addKit(kitName);
        plugin.getArenaManager().updateArena(arena);
        success(p, "Added kit &d" + kitName + "&f!");
    }

    private void handleInfo(Player p, String[] args) {
        if (args.length < 2) {
            usage(p, "/arena info <arenaname>");
            return;
        }
        Arena arena = plugin.getArenaManager().getArena(args[1]);
        if (arena == null) {
            fail(p, "Arena does not exist!");
            return;
        }
        MessageUtil.sendMessage(p, "&d&lArena Info: &f" + arena.getName());
        MessageUtil.sendMessage(p, "&fWorld: &d" + arena.getWorldName());
        MessageUtil.sendMessage(p, "&fRespawnable: &d" + arena.isRespawnableKit());
        MessageUtil.sendMessage(p, "&fRemake: &d" + arena.isRemake());
        MessageUtil.sendMessage(p, "&fVoid Y: &d" + arena.getVoidY());
        MessageUtil.sendMessage(p, "&fHigh Limit Y: &d" + arena.getHighLimitY());
        MessageUtil.sendMessage(p, "&fKits: &d" + (arena.getKits().isEmpty() ? "none" : String.join(", ", arena.getKits())));
        MessageUtil.sendMessage(p, "&fComplete: &d" + arena.isComplete());
    }

    private void handleList(Player p) {
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
    }

    private void handleSaveAll(Player p) {
        plugin.getArenaManager().saveAllArenas();
        success(p, "Saved all arenas!");
    }

    private void handleReload(Player p) {
        plugin.getArenaManager().reload();
        success(p, "Reloaded all arenas!");
    }

    private void handleRemoveAllGameArenas(Player p) {
        int count = plugin.getArenaManager().getGameArenas().size();
        plugin.getArenaManager().clearGameArenas();
        success(p, "Removed &d" + count + "&f game arenas!");
    }

    private void sendUsage(Player p) {
        MessageUtil.sendMessage(p, "&d&lUsage: &f/arena <subcommand> <arenaname>");
        MessageUtil.sendMessage(p, "&fAvailable subcommands:");
        MessageUtil.sendMessage(p, "&7- &dcreate &f<arenaname>");
        MessageUtil.sendMessage(p, "&7- &dset &f<arenaname> <field> <value>");
        MessageUtil.sendMessage(p, "&7- &dpos1 &f<arenaname>");
        MessageUtil.sendMessage(p, "&7- &dpos2 &f<arenaname>");
        MessageUtil.sendMessage(p, "&7- &dspec &f<arenaname>");
        MessageUtil.sendMessage(p, "&7- &dbnsb1 &f<arenaname>");
        MessageUtil.sendMessage(p, "&7- &dbnsb2 &f<arenaname>");
        MessageUtil.sendMessage(p, "&7- &daddkit &f<arenaname> <kitname>");
        MessageUtil.sendMessage(p, "&7- &dinfo &f<arenaname>");
        MessageUtil.sendMessage(p, "&7- &dlist");
        MessageUtil.sendMessage(p, "&7- &dsaveall");
        MessageUtil.sendMessage(p, "&7- &dreload");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return List.of("create", "set", "pos1", "pos2", "spec", "bnsb1", "bnsb2", "addkit", "info", "list", "saveall", "reload", "removeallgamearenas");
        }
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (List.of("set", "pos1", "pos2", "spec", "bnsb1", "bnsb2", "addkit", "info").contains(subCommand)) {
                return plugin.getArenaManager().getArenas().stream()
                        .map(Arena::getName)
                        .toList();
            }
        }
        if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("set")) {
                return List.of("respawnable", "remake", "voidy", "highlimity", "worldname");
            }
            if (subCommand.equals("addkit")) {
                return plugin.getKitManager().getKits().stream()
                        .map(kit -> kit.getName())
                        .toList();
            }
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("set")) {
            String field = args[2].toLowerCase();
            if (field.equals("respawnable") || field.equals("remake")) {
                return List.of("true", "false");
            }
        }
        return List.of();
    }
}
