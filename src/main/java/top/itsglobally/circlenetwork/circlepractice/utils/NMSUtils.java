package top.itsglobally.circlenetwork.circlepractice.utils;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collection;

public class NMSUtils {
    private static final String VERSION;

    static {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        VERSION = name.substring(name.lastIndexOf('.') + 1);
    }

    public static Class<?> getNMSClass(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + VERSION + "." + className);
    }

    public static Class<?> getCraftClass(String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + VERSION + "." + className);
    }

    public static String getVersion() {
        return VERSION;
    }

    public static int getPing(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Field pingField = entityPlayer.getClass().getDeclaredField("ping");
            return pingField.getInt(entityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void playFakeDeath(Player target, Collection<Player> viewers) {
        if (target == null) return;

        PacketPlayOutEntityStatus packet =
                new PacketPlayOutEntityStatus(
                        ((CraftPlayer) target).getHandle(),
                        (byte) 3
                );

        for (Player viewer : viewers) {
            if (viewer == null) continue;
            ((CraftPlayer) viewer).getHandle()
                    .playerConnection
                    .sendPacket(packet);

            viewer.hidePlayer(target);
        }
    }


}
