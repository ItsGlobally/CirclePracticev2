package top.itsglobally.circlenetwork.circlepractice.managers;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import top.itsglobally.circlenetwork.circlepractice.data.Arena;
import top.itsglobally.circlenetwork.circlepractice.data.GameArena;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.itsglobally.circlenetwork.circlepractice.utils.RandomUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ArenaManager extends Managers {

    private final SlimePlugin slime = plugin.getPluginManager().getSlimePlugin;
    private final SlimeLoader sl = plugin.getPluginManager().getSlimePlugin.getLoader("file");

    public ArenaManager() {

    }

    public Arena randomArena() {
        List<Arena> allArenas = new ArrayList<>(plugin.getDataManager().getArenas());
        return allArenas.get(RandomUtil.nextInt(allArenas.size()));
    }

    public void cloneArena(Arena a, String newWorldName) {
        this.cloneArena(a.getWorldName());
    }

    public void cloneArena(String sourceWorldName) {
        try {
            SlimeLoader loader = slime.getLoader("file");


            if (Bukkit.getWorld(sourceWorldName) != null) {
                sl.unlockWorld(sourceWorldName);
            }

            SlimeWorld sourceWorld = slime.loadWorld(loader, sourceWorldName, false, gameArenaProps());

            String newWorldName = sourceWorldName + "_" + System.currentTimeMillis();

            SlimeWorld cloned = sourceWorld.clone(newWorldName);
            slime.generateWorld(cloned);

        } catch (UnknownWorldException | IOException | CorruptedWorldException |
                 NewerFormatException | WorldInUseException ex) {
            Bukkit.broadcastMessage(MessageUtil.formatMessage("&cERROR! " + ex));
            ex.printStackTrace();
        }
    }



    public GameArena createGameArena(Kit kit) {
        String newWorldName = "arena_" + UUID.randomUUID().toString().substring(0, 8);
        Collection<Arena> arenas = plugin.getDataManager().getAvailableArenas();
        if (arenas.isEmpty()) return null;
        List<Arena> list = new ArrayList<>(arenas);
        Arena na = list.get(RandomUtil.nextInt(list.size()));
        cloneArena(na, newWorldName);
        GameArena ga = new GameArena(na.getWorldName(), newWorldName);
        ga.convertFromArena(na, kit);
        plugin.getDataManager().addGameArena(ga);
        return ga;
    }

    private SlimePropertyMap gameArenaProps() {
        SlimePropertyMap props = new SlimePropertyMap();
        props.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        props.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        props.setString(SlimeProperties.DIFFICULTY, "hard");
        return props;
    }
}
