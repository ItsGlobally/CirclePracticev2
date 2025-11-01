package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.data.PlayerState;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;
import java.util.Locale;

@CommandInfo(name = "kit")
public class kit implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player p)) return;

        // 無參數：顯示所有用法
        if (args.length < 1) {
            sendUsage(p);
            return;
        }

        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);

        switch (args[0].toLowerCase(Locale.ROOT)) {

            case "create" -> {
                if (args.length < 2) {
                    MessageUtil.sendMessage(p, "&d&lUsage: &f/kit create <kitname>");
                    return;
                }
                if (!p.hasPermission("circlepractice.admin")) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fNo permission!");
                    return;
                }
                String name = args[1];
                if (plugin.getKitManager().kitAlreadyExist(name)) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fKit already exists!");
                    return;
                }
                plugin.getKitManager().addKit(new Kit(name));
                MessageUtil.sendMessage(p, "&d&l✓ &fCreated kit &d" + name + "&f!");
            }
            case "editglobally" -> {
                if (args.length < 2) {
                    MessageUtil.sendMessage(p, "&d&lUsage: &f/kit editglobally <kitname>");
                    return;
                }
                if (!p.hasPermission("circlepractice.admin")) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fNo permission!");
                    return;
                }
                String name = args[1];
                if (!plugin.getKitManager().kitAlreadyExist(name)) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fKit not found!");
                    return;
                }

                Kit kit = plugin.getKitManager().getKit(name);
                if (pp.isInSpawnNotEditing()) {
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
                    MessageUtil.sendMessage(p, "&d&lUsage: &f/kit edit <kitname>");
                    return;
                }
                String name = args[1];
                if (!plugin.getKitManager().kitAlreadyExist(name)) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fKit not found!");
                    return;
                }
                Kit kit = plugin.getKitManager().getKit(name);
                if (pp.isInSpawnNotEditing()) {
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
                    MessageUtil.sendMessage(p, "&d&lUsage: &f/kit apply <kitname>");
                    return;
                }
                String name = args[1];
                if (!plugin.getKitManager().kitAlreadyExist(name)) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fKit not found!");
                    return;
                }
                if (pp.isInSpawnNotEditing()) {
                    pp.setInventory(p.getInventory().getContents());
                    pp.setArmor(p.getInventory().getArmorContents());
                    p.getInventory().clear();
                    p.getInventory().setArmorContents(plugin.getPlayerDataManager().getData(p).getKitContents(name)[1]);
                    p.getInventory().setContents(plugin.getPlayerDataManager().getData(p).getKitContents(name)[0]);
                    MessageUtil.sendMessage(p, "&d&l✓ &fYou applied kit &d" + name + "&f.");
                }
            }

            case "save" -> {
                if (args.length < 2) {
                    MessageUtil.sendMessage(p, "&d&lUsage: &f/kit save <kitname>");
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
                    MessageUtil.sendMessage(p, "&d&l✓ &fSaved kit &d" + name + "&f!");
                    p.getInventory().clear();
                    p.getInventory().setContents(pp.getInventory());
                    p.getInventory().setArmorContents(pp.getArmor());
                    pp.setState(PlayerState.SPAWN);
                } else {
                    MessageUtil.sendMessage(p, "&d&l✗ &fYou're not editing a kit!");
                }
            }

            case "set" -> {
                if (args.length < 4) {
                    MessageUtil.sendMessage(p, "&d&lUsage: &f/kit set <kitname> <field> <value>");
                    return;
                }
                if (!p.hasPermission("circlepractice.admin")) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fNo permission!");
                    return;
                }

                String kitName = args[1];
                Kit kit = plugin.getKitManager().getKit(kitName);
                if (kit == null) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fKit not found!");
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
                        case "allowbreakblocks" -> {
                            if (args.length < 5) {
                                MessageUtil.sendMessage(p, "&d&lUsage: &f/kit set <kitname> allowbreakblocks <add|remove> <material>");
                                return;
                            }
                            String action = args[3];
                            String matName = args[4];
                            Material mat = Material.valueOf(matName.toUpperCase());
                            if (action.equalsIgnoreCase("add")) kit.addAllowBreakBlocks(mat);
                            else if (action.equalsIgnoreCase("remove")) kit.getAllowBreakBlocks().remove(mat);
                            else {
                                MessageUtil.sendMessage(p, "&d&l✗ &fUse add/remove!");
                                return;
                            }
                        }
                        default -> {
                            MessageUtil.sendMessage(p, "&d&l✗ &fUnknown field: &d" + field);
                            return;
                        }
                    }
                } catch (Exception e) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fInvalid value for field &d" + field + "&f!");
                    return;
                }

                plugin.getKitManager().updateKit(kit);
                MessageUtil.sendMessage(p, "&d&l✓ &fUpdated field &d" + field + "&f of kit &d" + kit.getName() + "&f!");
            }

            case "info" -> {
                if (args.length < 2) {
                    MessageUtil.sendMessage(p, "&d&lUsage: &f/kit info <kitname>");
                    return;
                }
                Kit kit = plugin.getKitManager().getKit(args[1]);
                if (kit == null) {
                    MessageUtil.sendMessage(p, "&d&l✗ &fKit not found!");
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
                MessageUtil.sendMessage(p, "&fBroke To NoSpawn: &d" + (kit.getBrokeToNoSpawn() == null ? "none" : kit.getBrokeToNoSpawn()));
                MessageUtil.sendMessage(p, "&fAllow Break Blocks: &d" + (kit.getAllowBreakBlocks().isEmpty() ? "none" : kit.getAllowBreakBlocks()));
            }

            case "saveall" -> {
                MessageUtil.sendMessage(p, "&d&lUsage: &f/kit saveall");
                plugin.getKitManager().saveAllKits();
                MessageUtil.sendMessage(p, "&d&l✓ &fSaved all kits!");
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
            return List.of("hunger", "enabled", "forduels", "canbuild", "respawnable", "respawntime", "broketonospawn", "freezeoncooldown", "allowbreakblocks");
        }
        return List.of();
    }
}
