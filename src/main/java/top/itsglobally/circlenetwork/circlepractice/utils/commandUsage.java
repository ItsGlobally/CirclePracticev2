package top.itsglobally.circlenetwork.circlepractice.utils;

import org.bukkit.entity.Player;

public class commandUsage {
    public static String duel = "&c/duel <player> <kit>";

    public static String getCommandUsage(String cmd) {
        return getCommandUsage(cmd, false);
    }
    public static String getCommandUsage(String cmd, boolean raw) {
        String usage;
        switch (cmd) {
            case "duel": {
                usage = duel;
            }
            default: {
                usage = "&cINVALID";
            }
        }
        return raw ? MessageUtil.formatMessage(usage) : usage;
    }
    public static void sendCommandUsage(Player p, String cmd) {
        MessageUtil.sendMessage(p, cmd);
    }
}
