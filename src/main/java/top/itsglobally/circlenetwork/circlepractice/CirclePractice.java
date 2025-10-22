package top.itsglobally.circlenetwork.circlepractice;

import github.scarsz.discordsrv.DiscordSRV;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import top.itsglobally.circlenetwork.circlepractice.discord.SlashCommands;
import top.itsglobally.circlenetwork.circlepractice.managers.*;
import top.nontage.nontagelib.command.NontageCommandLoader;
import top.nontage.nontagelib.listener.ListenerRegister;

public final class CirclePractice extends JavaPlugin {

    static CirclePractice plugin;
    private static BukkitAudiences adventure;
    private static LuckPerms luckPerms;
    private GameManager gm;
    private KitManager km;
    private PlayerManager pm;
    private PluginManager plm;
    private ArenaManager am;
    private ConfigManager cm;
    private PlayerDataManager pdm;

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
        DiscordSRV.api.addSlashCommandProvider(new SlashCommands());
    }

    private void initManagers() {
        adventure = BukkitAudiences.create(this);
        luckPerms = LuckPermsProvider.get();
        gm = new GameManager();
        km = new KitManager();
        pm = new PlayerManager();
        plm = new PluginManager();
        am = new ArenaManager();
        cm = new ConfigManager();
        pdm = new PlayerDataManager();
    }

    @Override
    public void onDisable() {
        km.saveAllKits();
        am.saveAllArenas();
        pdm.saveAll();
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

    public PluginManager getPluginManager() {
        return plm;
    }

    public ConfigManager getConfigManager() {
        return cm;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public PlayerDataManager getPlayerDataManager() {
        return pdm;
    }
}
