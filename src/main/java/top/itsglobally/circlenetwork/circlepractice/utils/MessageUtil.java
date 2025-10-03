package top.itsglobally.circlenetwork.circlepractice.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circlepractice.CirclePractice;

public class MessageUtil {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    public static void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    public static void sendMessage(Player player1, Player player2, String message) {
        player1.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        player2.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }


    public static void sendActionBar(Player player, String message) {
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        CirclePractice.audience(player).sendActionBar(component);
    }

    public static void sendTitle(Player player, String title, String subtitle) {
        ChatColor.translateAlternateColorCodes('&', title);

        player.sendTitle(
                ChatColor.translateAlternateColorCodes('&', title),
                ChatColor.translateAlternateColorCodes('&', subtitle)
        );
    }


    public static String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}