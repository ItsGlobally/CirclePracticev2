package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.practical.BedBreakParticle;
import top.itsglobally.circlenetwork.circlepractice.practical.FinalKillParticle;
import top.itsglobally.circlenetwork.circlepractice.practical.Particles;
import top.itsglobally.circlenetwork.circlepractice.utils.Menus;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.List;

@CommandInfo(name = "particle")
public class ParticleCommand implements NontageCommand, GlobalInterface {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;

        if (strings.length < 1) {
            usage(p, "/particle <finalkill|bedbreak> <list|particleName>");
            return;
        }
        switch (strings[0]) {
            case "finalkill" -> {
                if (strings.length < 2) {
                    p.openInventory(Menus.particleMenu(p, Particles.FinalKill));
                    return;
                }
                if (strings[1].equals("list")) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("&dAll Final Kill Particles");
                    for (FinalKillParticle fkp : FinalKillParticle.values()) {
                        builder.append("\n").append("&e&l").append(fkp.name());
                    }
                    MessageUtil.sendMessage(p, builder.toString());
                    return;
                }
                try {
                    FinalKillParticle target = FinalKillParticle.valueOf(strings[1]);
                    if (!p.hasPermission(target.getPermission())) {
                        fail(p, "You do not own this particle!");
                        return;
                    }
                    plugin.getPlayerDataManager().getData(p).setFinalKillParticle(target);
                    success(p, "Changed your final kill particle effect to " + target.name());
                } catch (IllegalArgumentException e) {
                    fail(p, "Particle not found.");
                    e.printStackTrace();
                }
            }
            case "bedbreak" -> {
                if (strings.length < 2) {
                    p.openInventory(Menus.particleMenu(p, Particles.BedBreak));
                    return;
                }
                if (strings[1].equals("list")) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("&dAll Bed Break Particles");
                    for (FinalKillParticle fkp : FinalKillParticle.values()) {
                        builder.append("\n").append("&e&l").append(fkp.name());
                    }
                    MessageUtil.sendMessage(p, builder.toString());
                    return;
                }
                try {
                    BedBreakParticle target = BedBreakParticle.valueOf(strings[1]);
                    if (!p.hasPermission(target.getPermission())) {
                        fail(p, "You do not own this particle!");
                        return;
                    }
                    plugin.getPlayerDataManager().getData(p).setBedBreakParticle(target);
                    success(p, "Changed your bed break particle effect to " + target.name());
                } catch (IllegalArgumentException e) {
                    fail(p, "Particle not found.");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
