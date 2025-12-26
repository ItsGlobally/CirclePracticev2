package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;

import java.util.HashMap;
import java.util.UUID;

public class Party implements GlobalInterface{
    private final HashMap<UUID, PracticePlayer> players;
    private PracticePlayer leader;

    public Party(PracticePlayer leader) {
        this.leader = leader;
        this.players = new HashMap<>();
        players.put(leader.getUuid(), leader);
    }

    public HashMap<UUID, PracticePlayer> getPlayers() {
        return players;
    }

    public PracticePlayer getLeader() {
        return leader;
    }

    public void setLeader(PracticePlayer leader) {
        this.leader = leader;
    }

    public void disband() {
        for (PracticePlayer pp : players.values()) {
            if (pp.equals(leader)) continue;
            pp.setParty(null);
            MessageUtil.sendMessage(pp.getPlayer(), "&d&lParty » &cParty leader disbanded the party.");
        }
        leader.setParty(null);
        MessageUtil.sendMessage(leader.getPlayer(), "&d&lParty » &cDisbanded the party.");
    }

    public void chat(PracticePlayer pp, String msg) {
        if (!players.containsValue(pp)) return;

        for (PracticePlayer app : players.values()) {
            Player p = app.getPlayer();
            MessageUtil.sendMessage(p, "&d&lParty » &r" + plugin.getPlayerManager().getPrefixedName(pp.getPlayer()) + ": &r" + msg);
        }
    }
}
