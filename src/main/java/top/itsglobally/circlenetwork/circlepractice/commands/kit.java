package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.data.PlayerState;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

public class kit implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;
        if (strings.length < 3) return;
        String a1 = strings[1];
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        switch (strings[0].toLowerCase()) {
            case "create": {
                MessageUtil.sendMessage(p, "&cNo Permission!");
                if (plugin.getKitManager().kitAlreadyExist(a1)) {
                    return;
                }
                plugin.getKitManager().addKit(plugin.getKitManager().createKit(a1));

                break;
            }
            case "editglobally": {
                if (!p.hasPermission("circlepractice.admin")) {
                    MessageUtil.sendMessage(p, "&cNo Permission!");
                }
                if (pp.isInSpawnNotEditing()) {
                    if (plugin.getKitManager().kitAlreadyExist(a1)) {
                        pp.setQueuedKit(a1);
                        Kit kit = plugin.getKitManager().getKit(a1);
                        p.getInventory().clear();
                        p.getInventory().setArmorContents(null);
                        p.getInventory().setArmorContents(kit.getArmor());
                        p.getInventory().setContents(kit.getContents());
                    }
                }
                break;
            }
            case "save": {
                MessageUtil.sendMessage(p, "&cNo Permission!");
                if (pp.isEditing()) {
                    if (pp.getState() == PlayerState.EDITINGGLOBALLY) plugin.getKitManager().updateKit(a1, p.getInventory().getContents(), p.getInventory().getArmorContents());
                    else ;

                }
            }
            case "sethunger": {
                MessageUtil.sendMessage(p, "&cNo Permission!");

            }

        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        return NontageCommand.super.onTabComplete(sender, label, args, location);
    }
}
