package top.itsglobally.circlenetwork.circlepractice.managers;

import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;

import java.util.*;

public class PlayerManager extends Managers{
    public PlayerManager() {

    }

    private Map<UUID, PracticePlayer> playerList= new HashMap();

    public PracticePlayer getPlayer(UUID uuid) {
        return playerList.get(uuid);
    }

    public PracticePlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public void addPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playerList.containsKey(uuid)) {
            playerList.put(uuid, new PracticePlayer(player));
        }
    }

    public void removePlayer(UUID uuid) {
        playerList.remove(uuid);
    }

    public Map<UUID, PracticePlayer> getAllPlayers() {
        return playerList;
    }



}
