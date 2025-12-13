package top.itsglobally.circlenetwork.circlepractice.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.PlayerState;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;

import java.util.*;

public class QueueManager implements GlobalInterface {

    private final Map<String, Deque<UUID>> queues = new HashMap<>();

    public void joinQueue(Player player, String kitName) {
        queues.putIfAbsent(kitName, new ArrayDeque<>());
        Deque<UUID> queue = queues.get(kitName);

        if (queue.contains(player.getUniqueId())) {
            MessageUtil.sendMessage(player, "§cYou are already in queue.");
            return;
        }

        queue.addLast(player.getUniqueId());
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(player);
        pp.setState(PlayerState.QUEUE);
        MessageUtil.sendMessage(player, "§aJoined " + kitName + " queue.");

        tryMatch(kitName);
    }

    private void tryMatch(String kitName) {
        Deque<UUID> queue = queues.get(kitName);

        if (queue.size() < 2) return;

        Player p1 = Bukkit.getPlayer(queue.pollFirst());
        Player p2 = Bukkit.getPlayer(queue.pollFirst());

        if (p1 == null || p2 == null) {
            tryMatch(kitName);
            return;
        }

        plugin.getGameManager().processNewGame(
                p1,
                p2,
                plugin.getKitManager().getKit(kitName)
        );
    }

    public void leaveQueue(Player player) {
        for (Deque<UUID> queue : queues.values()) {
            queue.remove(player.getUniqueId());
        }
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(player);
        pp.setState(PlayerState.SPAWN);
    }
}
