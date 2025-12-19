package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class Party {

    private final List<PracticePlayer> players;
    private final PracticePlayer leader;

    public Party(PracticePlayer leader) {
        this.leader = leader;
        this.players = new ArrayList<>();
        this.players.add(leader);
    }
    public void addPlayer(PracticePlayer pp) {
        if (!players.contains(pp)) players.add(pp);
    }

    public void removePlayer(PracticePlayer pp) {
        players.remove(pp);
    }

    public List<PracticePlayer> getPlayers() {
        return new ArrayList<>(players);
    }

    public PracticePlayer getLeader() {
        return leader;
    }
    public void broadcast(String message) {
        for (PracticePlayer pp : players) {
            Player p = pp.getPlayer();
            if (p != null && p.isOnline()) {
                MessageUtil.sendMessage(p, message);
            }
        }
    }

    public List<PracticePlayer> getAlivePlayers() {
        List<PracticePlayer> alive = new ArrayList<>();
        for (PracticePlayer pp : players) {
            Player p = pp.getPlayer();
            if (p != null && p.isOnline() && p.getHealth() > 0) {
                alive.add(pp);
            }
        }
        return alive;
    }

    public boolean isAlive() {
        return !getAlivePlayers().isEmpty();
    }

    public boolean containsPlayer(PracticePlayer pp) {
        return players.contains(pp);
    }
}
