package top.itsglobally.circlenetwork.circlepractice.utils;

import org.bukkit.ChatColor;

import java.text.DecimalFormat;

public class StarUtils {

    private static final ChatColor[] RAINBOW = new ChatColor[]{
            ChatColor.RED,
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.AQUA,
            ChatColor.BLUE,
            ChatColor.LIGHT_PURPLE
    };

    private StarUtils() {
    }

    public static String getColoredStars(long stars) {
        String number = formatNumber(stars);
        String content = "[" + number + "✫]";

        if (stars >= 1000) {
            return rainbowize(content);
        } else {
            ChatColor color = prestigeColor(stars);
            return color + content + ChatColor.RESET;
        }
    }

    public static String formatNumber(long num) {
        DecimalFormat df = new DecimalFormat("0");
        return df.format(num);
    }

    private static ChatColor prestigeColor(long stars) {
        if (stars < 100) {
            return ChatColor.GRAY;
        } else if (stars < 200) {
            return ChatColor.WHITE;
        } else if (stars < 300) {
            return ChatColor.GOLD;
        } else if (stars < 400) {
            return ChatColor.AQUA;
        } else if (stars < 500) {
            return ChatColor.DARK_GREEN;
        } else if (stars < 600) {
            return ChatColor.DARK_AQUA;
        } else if (stars < 700) {
            return ChatColor.DARK_RED;
        } else if (stars < 800) {
            return ChatColor.LIGHT_PURPLE;
        } else if (stars < 900) {
            return ChatColor.BLUE;
        } else {
            return ChatColor.DARK_PURPLE;
        }
    }

    private static String rainbowize(String input) {
        StringBuilder sb = new StringBuilder();
        int colorIndex = 0;
        for (char c : input.toCharArray()) {
            ChatColor color = RAINBOW[colorIndex % RAINBOW.length];
            sb.append(color).append(c);
            colorIndex++;
        }
        sb.append(ChatColor.RESET);
        return sb.toString();
    }
}
