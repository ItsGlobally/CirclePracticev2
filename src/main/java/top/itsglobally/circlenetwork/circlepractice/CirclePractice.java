package top.itsglobally.circlenetwork.circlepractice;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import top.itsglobally.circlenetwork.circlepractice.managers.*;
import top.nontage.nontagelib.command.NontageCommandLoader;
import top.nontage.nontagelib.listener.ListenerRegister;

public final class CirclePractice extends JavaPlugin {

    static CirclePractice plugin;
    private static BukkitAudiences adventure;
    private GameManager gm;
    private KitManager km;
    private PlayerManager pm;
    private DataManager dm;
    private PluginManager plm;
    private ArenaManager am;
    private static LuckPerms luckPerms;

    public static CirclePractice getPlugin() {
        return plugin;
    }

    public static Audience audience(Player player) {
        return adventure.player(player);
    }

    @Override
    public void onEnable() {
        plugin = this;
        NontageCommandLoader.registerAll(this);
        ListenerRegister.registerAll(this);
        initManagers();
    }

    private void initManagers() {
        luckPerms = LuckPermsProvider.get();
        gm = new GameManager();
        km = new KitManager();
        pm = new PlayerManager();
        dm = new DataManager();
        plm = new PluginManager();
        am = new ArenaManager();
    }

    @Override
    public void onDisable() {
    }

    public GameManager getGameManager() {
        return gm;
    }

    public KitManager getKitManager() {
        return km;
    }

    public PlayerManager getPlayerManager() {
        return pm;
    }

    public ArenaManager getArenaManager() {
        return am;
    }

    public DataManager getDataManager() {
        return dm;
    }

    public PluginManager getPluginManager() {
        return plm;
    }
    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
