package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.data.PlayerState;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name="kit")
public class kit implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;
        if (strings.length < 2) return;
        String a1 = strings[1];
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        switch (strings[0].toLowerCase()) {
            case "create": {
                if (!p.hasPermission("circlepractice.admin")) {
                    MessageUtil.sendMessage(p, "&cNo Permission!");
                    return;
                }
                if (plugin.getKitManager().kitAlreadyExist(a1)) {
                    return;
                }
                plugin.getKitManager().addKit(new Kit(a1));
                MessageUtil.sendMessage(p, "&aCreated kit " + a1 + "!");
                break;
            }
            case "editglobally": {
                if (!p.hasPermission("circlepractice.admin")) {
                    MessageUtil.sendMessage(p, "&cNo Permission!");
                    return;
                }
                if (pp.isInSpawnNotEditing()) {
                    if (plugin.getKitManager().kitAlreadyExist(a1)) {
                        pp.setState(PlayerState.EDITINGGLOBALLY);
                        pp.setQueuedKit(a1);
                        Kit kit = plugin.getKitManager().getKit(a1);
                        pp.setInventory(p.getInventory().getContents());
                        pp.setArmor(p.getInventory().getArmorContents());
                        p.getInventory().clear();
                        p.getInventory().setArmorContents(null);
                        p.getInventory().setArmorContents(kit.getArmor());
                        p.getInventory().setContents(kit.getContents());
                        MessageUtil.sendMessage(p, "&aYou're now editing kit " + a1 + " . Do \"/kit save " + a1 + "\" to save!");
                        return;
                    }
                }
                break;
            }
            case "save": {
                if (pp.isEditing()) {
                    if (pp.getState() == PlayerState.EDITINGGLOBALLY) plugin.getKitManager().updateKit(a1, p.getInventory().getContents(), p.getInventory().getArmorContents());
                    MessageUtil.sendMessage(p, "&aSaved kit " + a1 + "!");
                    p.getInventory().clear();
                    p.getInventory().setArmorContents(null);
                    p.getInventory().setContents(pp.getInventory());
                    p.getInventory().setArmorContents(pp.getArmor());
                    pp.setState(PlayerState.SPAWN);
                    return;
                }
                MessageUtil.sendMessage(p, "&cYou're not editing a kit!");
                break;
            }
            case "togglehunger": {
                if (!p.hasPermission("circlepractice.admin")) {
                    MessageUtil.sendMessage(p, "&cNo Permission!");
                    return;
                }
                Kit kit = plugin.getKitManager().getKit(a1);
                boolean status = kit.isHunger();
                kit.setHunger(!status);
                plugin.getKitManager().updateKit(kit);
                MessageUtil.sendMessage(p, "&Set " + kit.getName() + "'s hunger status to " + kit.isHunger() + "!");
                break;
            }
            case "toggleforduels": {
                if (!p.hasPermission("circlepractice.admin")) {
                    MessageUtil.sendMessage(p, "&cNo Permission!");
                    return;
                }
                Kit kit = plugin.getKitManager().getKit(a1);
                boolean status = kit.isForDuels();
                kit.setForDuels(!status);
                plugin.getKitManager().updateKit(kit);
                MessageUtil.sendMessage(p, "&Set " + kit.getName() + "'s for duel status to " + kit.isForDuels() + "!");
                break;
            }
            case "toggle": {
                if (!p.hasPermission("circlepractice.admin")) {
                    MessageUtil.sendMessage(p, "&cNo Permission!");
                    return;
                }
                Kit kit = plugin.getKitManager().getKit(a1);
                boolean status = kit.isEnabled();
                kit.setEnabled(!status);
                plugin.getKitManager().updateKit(kit);
                MessageUtil.sendMessage(p, "&Set " + kit.getName() + "'s hunger status to " + kit.isEnabled() + "!");
                break;
            }
            case "togglecanbuild": {
                if (!p.hasPermission("circlepractice.admin")) {
                    MessageUtil.sendMessage(p, "&cNo Permission!");
                    return;
                }
                Kit kit = plugin.getKitManager().getKit(a1);
                boolean status = kit.isCanBuild();
                kit.setCanBuild(!status);
                plugin.getKitManager().updateKit(kit);
                MessageUtil.sendMessage(p, "&Set " + kit.getName() + "'s canbuild status to " + kit.isCanBuild() + "!");
                break;
            }
            case "saveall": {
                Bukkit.broadcastMessage("e");
                plugin.getKitManager().saveAllKits();
                break;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
