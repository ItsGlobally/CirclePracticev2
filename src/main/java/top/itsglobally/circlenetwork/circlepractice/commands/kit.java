package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.data.PlayerState;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;
import java.util.Locale;

@CommandInfo(name = "kit")
public class kit implements NontageCommand, GlobalInterface {
    @Override
    public void execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player p)) return;

        if (args.length < 1) {
            sendUsage(p);
            return;
        }

        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);

        switch (args[0].toLowerCase(Locale.ROOT)) {

            case "create" -> {
                if (args.length < 2) {
                    usage(p, "/kit create <kitname>");
                    return;
                }
                if (!p.hasPermission("circlepractice.admin")) {
                    fail(p, "No permission!");
                    return;
                }
                String name = args[1];
                if (plugin.getKitManager().kitAlreadyExist(name)) {
                    fail(p, "Kit already exists!");
                    return;
                }
                plugin.getKitManager().addKit(new Kit(name));
                success(p, "Created kit &d" + name + "&f!");
            }
            case "editglobally" -> {
                if (args.length < 2) {
                    usage(p, "/kit editglobally <kitname>");
                    return;
                }
                if (!p.hasPermission("circlepractice.admin")) {
                    fail(p, "No permission!");
                    return;
                }
                String name = args[1];
                if (!plugin.getKitManager().kitAlreadyExist(name)) {
                    MessageUtil.sendMessage(p, "Kit not found!");
                    return;
                }

                Kit kit = plugin.getKitManager().getKit(name);
                if (pp.isInSpawnNotEditingOrQueuing()) {
                    pp.setState(PlayerState.EDITINGGLOBALLY);
                    pp.setQueuedKit(name);
                    pp.setInventory(p.getInventory().getContents());
                    pp.setArmor(p.getInventory().getArmorContents());

                    p.getInventory().clear();
                    p.getInventory().setArmorContents(kit.getArmor());
                    p.getInventory().setContents(kit.getContents());

                    MessageUtil.sendMessage(p, "&d&l✎ &fYou're now globally editing kit &d" + name + "&f. Use &d/kit save " + name + " &fto save!");
                }
            }

            case "edit" -> {
                if (args.length < 2) {
                    usage(p, "/kit edit <kitname>");
                    return;
                }
                String name = args[1];
                if (!plugin.getKitManager().kitAlreadyExist(name)) {
                    MessageUtil.sendMessage(p, "Kit not found!");
                    return;
                }
                Kit kit = plugin.getKitManager().getKit(name);
                if (pp.isInSpawnNotEditingOrQueuing()) {
                    pp.setState(PlayerState.EDITING);
                    pp.setQueuedKit(name);
                    pp.setInventory(p.getInventory().getContents());
                    pp.setArmor(p.getInventory().getArmorContents());
                    p.getInventory().clear();
                    p.getInventory().setArmorContents(kit.getArmor());
                    p.getInventory().setContents(kit.getContents());
                    MessageUtil.sendMessage(p, "&d&l✎ &fYou're now editing kit &d" + name + "&f. Use &d/kit save " + name + " &fto save!");
                }
            }

            case "apply" -> {
                if (args.length < 2) {
                    usage(p, "/kit apply <kitname>");
                    return;
                }
                String name = args[1];
                if (!plugin.getKitManager().kitAlreadyExist(name)) {
                    MessageUtil.sendMessage(p, "Kit not found!");
                    return;
                }
                if (pp.isInSpawnNotEditingOrQueuing()) {
                    pp.setInventory(p.getInventory().getContents());
                    pp.setArmor(p.getInventory().getArmorContents());
                    p.getInventory().clear();
                    p.getInventory().setArmorContents(plugin.getPlayerDataManager().getData(p).getKitContents(name)[1]);
                    p.getInventory().setContents(plugin.getPlayerDataManager().getData(p).getKitContents(name)[0]);
                    success(p, "You applied kit &d" + name + "&f.");
                }
            }

            case "save" -> {
                if (args.length < 2) {
                    usage(p, "/kit save <kitname>");
                    return;
                }
                if (pp.isEditing()) {
                    String name = args[1];
                    if (pp.getState() == PlayerState.EDITINGGLOBALLY) {
                        plugin.getKitManager().updateKit(name, p.getInventory().getContents(), p.getInventory().getArmorContents());
                    } else {
                        ItemStack[][] cs = {
                                p.getInventory().getContents(),
                                p.getInventory().getArmorContents()
                        };
                        plugin.getPlayerDataManager().getData(p).setKitContents(name, cs);
                    }
                    success(p, "Saved kit &d" + name + "&f!");
                    p.getInventory().clear();
                    p.getInventory().setContents(pp.getInventory());
                    p.getInventory().setArmorContents(pp.getArmor());
                    pp.setState(PlayerState.SPAWN);
                } else {
                    MessageUtil.sendMessage(p, "You're not editing a kit!");
                }
            }

            case "set" -> {
                if (args.length < 4) {
                    usage(p, "/kit set <kitname> <field> <value>");
                    return;
                }
                if (!p.hasPermission("circlepractice.admin")) {
                    fail(p, "No permission!");
                    return;
                }

                String kitName = args[1];
                Kit kit = plugin.getKitManager().getKit(kitName);
                if (kit == null) {
                    fail(p, "Kit not found!");
                    return;
                }

                String field = args[2].toLowerCase();
                String value = args[3];

                try {
                    switch (field) {
                        case "hunger" -> kit.setHunger(Boolean.parseBoolean(value));
                        case "enabled" -> kit.setEnabled(Boolean.parseBoolean(value));
                        case "forduels" -> kit.setForDuels(Boolean.parseBoolean(value));
                        case "canbuild" -> kit.setCanBuild(Boolean.parseBoolean(value));
                        case "respawnable" -> kit.setRespawnable(Boolean.parseBoolean(value));
                        case "respawntime" -> kit.setRespawnTime(Integer.parseInt(value));
                        case "broketonospawn" -> kit.setBrokeToNoSpawn(Material.valueOf(value.toUpperCase()));
                        case "freezeoncooldown" -> kit.setFreezeOnCooldown(Boolean.parseBoolean(value));
                        case "nodamage" -> kit.setNodamage(Boolean.parseBoolean(value));
                        case "counthit" -> kit.setCountHit(Boolean.parseBoolean(value));
                        case "counthittodie" -> kit.setCountHitToDie(Integer.parseInt(value));
                        case "allowbreakblocks" -> {
                            if (args.length < 5) {
                                usage(p, "/kit set <kitname> allowbreakblocks <add|remove> <material>");
                                return;
                            }
                            String action = args[3];
                            String matName = args[4];
                            Material mat = Material.valueOf(matName.toUpperCase());
                            if (action.equalsIgnoreCase("add")) kit.addAllowBreakBlocks(mat);
                            else if (action.equalsIgnoreCase("remove")) kit.getAllowBreakBlocks().remove(mat);
                            else {
                                fail(p, "Use add/remove!");
                                return;
                            }
                        }
                        default -> {
                            fail(p, "Unknown field: &d" + field);
                            return;
                        }
                    }
                } catch (Exception e) {
                    fail(p, "Invalid value for field &d" + field + "&f!");
                    return;
                }

                plugin.getKitManager().updateKit(kit);
                success(p, "Updated field &d" + field + "&f of kit &d" + kit.getName() + "&f!");
            }

            case "info" -> {
                if (args.length < 2) {
                    usage(p, "/kit info <kitname>");
                    return;
                }
                Kit kit = plugin.getKitManager().getKit(args[1]);
                if (kit == null) {
                    fail(p, "Kit not found!");
                    return;
                }

                MessageUtil.sendMessage(p, "&d&lKit Info: &f" + kit.getName());
                MessageUtil.sendMessage(p, "&fHunger: &d" + kit.isHunger());
                MessageUtil.sendMessage(p, "&fEnabled: &d" + kit.isEnabled());
                MessageUtil.sendMessage(p, "&fFor Duels: &d" + kit.isForDuels());
                MessageUtil.sendMessage(p, "&fCan Build: &d" + kit.isCanBuild());
                MessageUtil.sendMessage(p, "&fRespawnable: &d" + kit.isRespawnable());
                MessageUtil.sendMessage(p, "&fRespawn Time: &d" + kit.getRespawnTime());
                MessageUtil.sendMessage(p, "&fFreeze On Cooldown: &d" + kit.isFreezeOnCooldown());
                MessageUtil.sendMessage(p, "&fNo Damage: &d" + kit.isNodamage());
                MessageUtil.sendMessage(p, "&fCount Hit: &d" + kit.isCountHit());
                MessageUtil.sendMessage(p, "&fCount Hit To Die: &d" + kit.getCountHitToDie());
                MessageUtil.sendMessage(p, "&fBroke To NoSpawn: &d" + (kit.getBrokeToNoSpawn() == null ? "none" : kit.getBrokeToNoSpawn()));
                MessageUtil.sendMessage(p, "&fAllow Break Blocks: &d" + (kit.getAllowBreakBlocks().isEmpty() ? "none" : kit.getAllowBreakBlocks()));
            }

            case "saveall" -> {
                if (!p.hasPermission("circlepractice.admin")) {
                    fail(p, "No permission!");
                    return;
                }
                plugin.getKitManager().saveAllKits();
                success(p, "Saved all kits!");
            }

            default -> sendUsage(p);
        }
    }

    private void sendUsage(Player p) {
        MessageUtil.sendMessage(p, "&d&lUsage: &f/kit <subcommand> <kitname>");
        MessageUtil.sendMessage(p, "&fAvailable subcommands:");
        MessageUtil.sendMessage(p, "&7- &dcreate &f<kitname>");
        MessageUtil.sendMessage(p, "&7- &dedit &f<kitname>");
        MessageUtil.sendMessage(p, "&7- &deditglobally &f<kitname>");
        MessageUtil.sendMessage(p, "&7- &dsave &f<kitname>");
        MessageUtil.sendMessage(p, "&7- &dset &f<kitname> <field> <value>");
        MessageUtil.sendMessage(p, "&7- &dinfo &f<kitname>");
        MessageUtil.sendMessage(p, "&7- &dsaveall");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return List.of("create", "edit", "editglobally", "save", "set", "info", "saveall");
        }
        if (args.length == 2 && List.of("edit", "set", "info", "save").contains(args[0].toLowerCase())) {
            return plugin.getKitManager().getKits().stream().map(Kit::getName).toList();
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            return List.of("hunger", "enabled", "forduels", "canbuild", "respawnable", "respawntime", "broketonospawn", "freezeoncooldown", "nodamage", "allowbreakblocks", "counthit", "counthittodie");
        }
        return List.of();
    }
}
