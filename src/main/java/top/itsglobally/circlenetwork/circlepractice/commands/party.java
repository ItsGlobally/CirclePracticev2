package top.itsglobally.circlenetwork.circlepractice.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.*;
import top.nontage.nontagelib.annotations.CommandInfo;
import top.nontage.nontagelib.command.NontageCommand;

import java.util.*;

@CommandInfo(name = "party", aliases = {"p", "pl"})
public class party implements GlobalInterface, NontageCommand {

    private final Map<UUID, Set<UUID>> pendingInvites = new HashMap<>();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        if (pp == null) return;

        if (args.length == 0) {
            usage(p, "/party <create|invite|kick|leave|info|accept>");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                createParty(pp);
                break;
            case "invite":
                if (args.length < 2) {
                    usage(p, "/party invite <player>");
                    return;
                }
                invitePlayer(pp, args[1]);
                break;
            case "kick":
                if (args.length < 2) {
                    usage(p, "/party kick <player>");
                    return;
                }
                kickPlayer(pp, args[1]);
                break;
            case "leave":
                leaveParty(pp);
                break;
            case "info":
                showPartyInfo(pp);
                break;
            case "accept":
                if (args.length < 2) {
                    fail(p, "Usage: /party accept <leader>");
                    return;
                }
                acceptInvite(pp, args[1]);
                break;
            default:
                fail(p, "Unknown subcommand.");
        }
    }

    private void createParty(PracticePlayer leader) {
        if (leader.isInParty()) {
            fail(leader.getPlayer(), "You are already in a party!");
            return;
        }
        Party party = new Party(leader);
        leader.setParty(party);
        success(leader.getPlayer(), "Party created! You are the leader.");
    }

    private void invitePlayer(PracticePlayer leader, String targetName) {
        Party party = leader.getParty();
        if (party == null || !party.getLeader().equals(leader)) {
            fail(leader.getPlayer(), "You are not the leader of a party!");
            return;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            fail(leader.getPlayer(), "Player not found or offline.");
            return;
        }

        PracticePlayer targetPP = plugin.getPlayerManager().getPlayer(target);
        if (targetPP == null) return;

        if (targetPP.isInParty()) {
            fail(leader.getPlayer(), "Player is already in a party!");
            return;
        }

        pendingInvites.computeIfAbsent(targetPP.getUuid(), k -> new HashSet<>()).add(leader.getUuid());

        success(leader.getPlayer(), "Invite sent to " + target.getName());
        success(target, leader.getPlayer().getName() + " invited you to a party. Type /party accept " + leader.getPlayer().getName() + " to join.");
    }

    private void acceptInvite(PracticePlayer pp, String leaderName) {
        if (pp.isInParty()) {
            fail(pp.getPlayer(), "You are already in a party! Leave it first to join another.");
            return;
        }

        Player leaderPlayer = Bukkit.getPlayer(leaderName);
        if (leaderPlayer == null || !leaderPlayer.isOnline()) {
            fail(pp.getPlayer(), "Leader not found or offline.");
            return;
        }

        PracticePlayer leaderPP = plugin.getPlayerManager().getPlayer(leaderPlayer);
        if (leaderPP == null || !leaderPP.isInParty()) {
            fail(pp.getPlayer(), "The party no longer exists.");
            return;
        }

        Set<UUID> invites = pendingInvites.getOrDefault(pp.getUuid(), Collections.emptySet());
        if (!invites.contains(leaderPP.getUuid())) {
            fail(pp.getPlayer(), "No pending invite from " + leaderName);
            return;
        }

        // 接受邀請
        pp.joinParty(leaderPP.getParty());
        pp.getParty().broadcast(pp.getPlayer().getName() + " joined the party!");

        // 移除所有邀請
        pendingInvites.remove(pp.getUuid());
    }

    private void kickPlayer(PracticePlayer leader, String targetName) {
        Party party = leader.getParty();
        if (party == null || !party.getLeader().equals(leader)) {
            fail(leader.getPlayer(), "You are not the leader of a party!");
            return;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            fail(leader.getPlayer(), "Player not found or offline.");
            return;
        }

        PracticePlayer targetPP = plugin.getPlayerManager().getPlayer(target);
        if (targetPP == null || !party.containsPlayer(targetPP)) {
            fail(leader.getPlayer(), "Player is not in your party!");
            return;
        }

        targetPP.leaveParty();
        party.broadcast(target.getName() + " was kicked from the party.");
    }

    private void leaveParty(PracticePlayer pp) {
        if (!pp.isInParty()) {
            fail(pp.getPlayer(), "You are not in a party.");
            return;
        }
        Party party = pp.getParty();
        pp.leaveParty();
        party.broadcast(pp.getPlayer().getName() + " left the party.");
    }

    private void showPartyInfo(PracticePlayer pp) {
        if (!pp.isInParty()) {
            fail(pp.getPlayer(), "You are not in a party.");
            return;
        }
        Party party = pp.getParty();
        StringBuilder members = new StringBuilder();
        for (PracticePlayer member : party.getPlayers()) {
            members.append(member.getPlayer().getName());
            if (member.equals(party.getLeader())) members.append(" (Leader)");
            members.append(", ");
        }
        if (members.length() >= 2) members.setLength(members.length() - 2);
        success(pp.getPlayer(), "Party Members: " + members.toString());
    }
}
