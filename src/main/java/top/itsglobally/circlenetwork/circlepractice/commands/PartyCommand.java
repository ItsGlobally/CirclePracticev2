package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.data.Party;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandInfo(name = "party", aliases = {"p", "pl", "pc"})
public class PartyCommand implements NontageCommand, GlobalInterface {
    @Override
    public void execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) return;

        if (strings.length < 1) {
            usage(p, "/party <subcommand> <args>");
        }

        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        String cmd = strings[0];

        switch (cmd) {
            case "create" -> {
                if (pp.isInParty()) {
                    fail(p, "You are already in a party!");
                    return;
                }

                create(p, pp);
            }
            case "disband" -> {
                if (!pp.isInParty()) {
                    fail(p, "You are not in a party!");
                    return;
                }
                pp.getParty().disband();
            }
            case "invite" -> {
                if (strings.length < 2) {
                    usage(p, "/party invite <player");
                    return;
                }
                Player target = Bukkit.getPlayerExact(strings[1]);

                if (target == null) {
                    fail(p, "That player is not online!");
                    return;
                }

                PracticePlayer targetPP = plugin.getPlayerManager().getPlayer(target);

                if (targetPP.isInParty()) {
                    fail(p, "That player is already in a party!");
                    return;
                }

                if (!pp.isInParty()) {
                    create(p, pp);
                }
                Party party = pp.getParty();
                party.add(targetPP);

            }
            case "chat" -> {
                chat(p, pp, strings);
            }
            case "leave" -> {
                if (!pp.isInParty()) {
                    fail(p, "You are not in a party!");
                    return;
                }
                Party party = pp.getParty();
                if (party.getLeader().equals(pp)) {
                    pp.getParty().disband();
                    return;
                }
                pp.getParty().remove(pp);
            }
            case "split" -> {
                if (strings.length < 2) {
                    usage(p, "/party split <kit>");
                    return;
                }

                if (!pp.isInParty()) {
                    fail(p, "You are not in a party!");
                    return;
                }

                String kitName = strings[1];
                Kit kit = plugin.getKitManager().getKit(kitName);
                if (kit == null) {
                    fail(p, "Kit does not exist!");
                    return;
                }

                Party party = pp.getParty();
                List<PracticePlayer> members = new ArrayList<>(party.getPlayers().values());

                if (members.size() < 2) {
                    fail(p, "Not enough players in the party to split!");
                    return;
                }

                List<PracticePlayer> redTeam = new ArrayList<>();
                List<PracticePlayer> blueTeam = new ArrayList<>();
                for (int i = 0; i < members.size(); i++) {
                    if (i % 2 == 0) redTeam.add(members.get(i));
                    else blueTeam.add(members.get(i));
                }

                plugin.getGameManager().processNewGame(redTeam, blueTeam, kit);
            }

        }
    }

    private void create(Player p, PracticePlayer pp) {
        Party newp = new Party(pp);
        pp.setParty(newp);
        success(p, "Created a party!");
    }

    public void chat(Player p, PracticePlayer pp, String[] strings) {
        if (!pp.isInParty()) {
            fail(p, "You are not in a party!");
            return;
        }

        String[] newStrings = Arrays.copyOfRange(strings, 1, strings.length);

        pp.getParty().chat(pp, String.join(" ", newStrings));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return NontageCommand.super.onTabComplete(sender, label, args);
    }
}
