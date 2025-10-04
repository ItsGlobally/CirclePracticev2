package top.itsglobally.circlenetwork.circlepractice.managers;

import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager extends Managers {
    private final Map<UUID, PracticePlayer> playerList = new HashMap();

    public PlayerManager() {

    }

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

    public String getPrefix(Player p) {
        User user = plugin.getLuckPerms().getUserManager().getUser(p.getUniqueId());
        return user.getCachedData().getMetaData().getPrefix();
    }

    public String getPrefixColor(Player p) {
        User user = plugin.getLuckPerms().getUserManager().getUser(p.getUniqueId());
        return (user.getCachedData().getMetaData().getMetaValue("prefixcolor") == null) ? user.getCachedData().getMetaData().getMetaValue("prefixcolor") : getPrefix(p).substring(0, 2);
    }

    public String getPrefixedName(Player p) {
        return getPrefix(p) + p.getName();
    }


}
