package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class DuelRequest {
    private final Player sender;
    private final Player target;
    private final String kit;
    private BukkitTask task;

    public DuelRequest(Player sender, Player target, String kit) {
        this.sender = sender;
        this.target = target;
        this.kit = kit;
    }

    public Player getSender() {
        return sender;
    }

    public Player getTarget() {
        return target;
    }

    public String getKit() {
        return kit;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public void cancelTask() {
        if (task != null) {
            task.cancel();
        }
    }
}

