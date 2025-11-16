package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.CirclePractice;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;

public interface GlobalInterface {
    CirclePractice plugin = CirclePractice.getPlugin();
    default void usage(Player p, String msg) {
        MessageUtil.sendMessage(p, "&d&lUsage: " + msg);
    }
    default void success(Player p, String msg) {
        MessageUtil.sendMessage(p, "&d&l✓ &f" + msg);
    }
    default void fail(Player p, String msg) {
        MessageUtil.sendMessage(p, "&d&l✗ &f" + msg);
    }
}
