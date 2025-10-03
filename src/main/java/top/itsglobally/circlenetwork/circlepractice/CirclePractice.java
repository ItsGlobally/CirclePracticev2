package top.itsglobally.circlenetwork.circlepractice;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import top.itsglobally.circlenetwork.circlepractice.managers.*;

public final class CirclePractice extends JavaPlugin {

    static CirclePractice plugin;

    private GameManager dm;
    private KitManager km;
    private PlayerManager pm;
    private DataManager cm;
    private PluginManager plm;
    private ArenaManager am;
    private static BukkitAudiences adventure;

    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic

    }

    private void initManagers() {
        dm = new GameManager();
        km = new KitManager();
        pm = new PlayerManager();
        cm = new DataManager();
        plm = new PluginManager();
        am = new ArenaManager();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static CirclePractice getPlugin() {
        return plugin;
    }

    public GameManager getDm() {
        return dm;
    }

    public KitManager getKm() {
        return km;
    }

    public PlayerManager getPm() {
        return pm;
    }

    public ArenaManager getAm() {
        return am;
    }

    public DataManager getCm() {
        return cm;
    }

    public PluginManager getPlm() {
        return plm;
    }
    public static Audience audience(Player player) {
        return adventure.player(player);
    }
}
