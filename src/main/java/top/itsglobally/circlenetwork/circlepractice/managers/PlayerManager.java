package top.itsglobally.circlenetwork.circlepractice.managers;

import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements GlobalInterface {
    private final Map<UUID, PracticePlayer> playerList = new HashMap<>();

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
        if (user == null) return "";
        String prefix = user.getCachedData().getMetaData().getPrefix();
        return prefix != null ? prefix : "";
    }

    public String getPrefixColor(Player p) {
        User user = plugin.getLuckPerms().getUserManager().getUser(p.getUniqueId());
        if (user == null) return "&f";
        String prefixColor = user.getCachedData().getMetaData().getMetaValue("prefixcolor");
        if (prefixColor != null) return prefixColor;
        String prefix = getPrefix(p);
        return prefix.length() >= 2 ? prefix.substring(0, 2) : "&f";
    }

    public String getPrefixedName(Player p) {
        return getPrefix(p) + p.getName();
    }


}
