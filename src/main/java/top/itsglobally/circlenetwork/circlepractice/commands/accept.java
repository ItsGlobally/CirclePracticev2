package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

public class accept implements NontageCommand, ICommand {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args, Location location) {
        return NontageCommand.super.onTabComplete(sender, label, args, location);
    }
}
