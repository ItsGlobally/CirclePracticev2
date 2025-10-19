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
import org.bukkit.Location;
import top.itsglobally.circlenetwork.circlepractice.data.Arena;
import top.itsglobally.circlenetwork.circlepractice.data.GameArena;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.utils.ConfigRegister;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.itsglobally.circlenetwork.circlepractice.utils.RandomUtil;
import top.itsglobally.circlenetwork.circlepractice.utils.serializer;
import top.nontage.nontagelib.config.BaseConfig;

import java.io.IOException;
import java.util.*;

public class ArenaManager extends Managers {

    private final SlimePlugin slime = plugin.getPluginManager().getSlimePlugin;
    private final SlimeLoader sl = plugin.getPluginManager().getSlimePlugin.getLoader("file");
    private static ArenaConfig arenaConfig;
    private static final Map<String, Arena> arenaMap = new LinkedHashMap<>();
    private static final Map<String, GameArena> gameArenaMap = new LinkedHashMap<>();

    public ArenaManager() {
        arenaConfig = ConfigRegister.register(new ArenaConfig(), "arenas");
        if (arenaConfig.arenas == null) arenaConfig.arenas = new LinkedHashMap<>();
        reload();
        createDefaultArenas();
    }

    public Arena randomArena() {
        List<Arena> allArenas = new ArrayList<>(getArenas());
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
        Collection<Arena> arenas = getAvailableArenas();
        if (arenas.isEmpty()) return null;
        List<Arena> list = arenas.stream()
                .filter(a -> a.getKits() != null && a.getKits().contains(kit.getName()))
                .toList();

        if (list.isEmpty()) return null;
        Arena na = list.get(RandomUtil.nextInt(list.size()));
        cloneArena(na, newWorldName);
        GameArena ga = new GameArena(na.getWorldName(), newWorldName);
        ga.convertFromArena(na);
        addGameArena(ga);
        return ga;
    }


    private SlimePropertyMap gameArenaProps() {
        SlimePropertyMap props = new SlimePropertyMap();
        props.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        props.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        props.setString(SlimeProperties.DIFFICULTY, "hard");
        return props;
    }

    public void reload() {
        arenaConfig.reload();


        if (arenaConfig.arenas == null) arenaConfig.arenas = new LinkedHashMap<>();


        arenaMap.clear();
        for (Map.Entry<String, Map<String, Object>> entry : arenaConfig.arenas.entrySet()) {
            arenaMap.put(entry.getKey(), serializer.deserializeArena(entry.getValue()));
        }
    }

    public class ArenaConfig extends BaseConfig {
        public Map<String, Map<String, Object>> arenas = new LinkedHashMap<>();
    }

    public void createDefaultArenas() {
        if (getArena("arena") == null) {
            Arena arena = new Arena("arena");
            arena.setWorldName("arena");
            arena.setPos1(new Location(Bukkit.getWorld(arena.getWorldName()), 50, 51, 0, 0, 0));
            arena.setPos2(new Location(Bukkit.getWorld(arena.getWorldName()), -50, 51, 0, 180, 0));
            arena.setSpectatorSpawn(new Location(Bukkit.getWorld(arena.getWorldName()), 0, 75, 0, 90, 0));
            arena.addKit("NoDebuff");
            arena.setRespawnableKit(false);
            addArena(arena);
            saveAllArenas();
        }
    }

    public void saveAllArenas() {
        arenaConfig.save();
    }

    public void addArena(Arena arena) {
        arenaConfig.arenas.put(arena.getName(), serializer.serializeArena(arena));
        arenaConfig.save();
        arenaMap.put(arena.getName(), arena);
    }

    public void updateArena(Arena arena) {
        arenaConfig.arenas.put(arena.getName(), serializer.serializeArena(arena));
        arenaConfig.save();
    }

    public Arena getArena(String name) {
        return arenaMap.get(name);
    }

    public Collection<Arena> getArenas() {
        return arenaMap.values();
    }

    public Collection<Arena> getAvailableArenas() {
        Collection<Arena> list = new ArrayList<>();
        for (Arena a : getArenas()) {
            if (a.isComplete()) {
                list.add(a);
            }
        }
        return list;
    }

    public void removeArena(String name) {
        arenaConfig.arenas.remove(name);
        arenaConfig.save();
        arenaMap.remove(name);
    }

    public void addGameArena(GameArena arena) {
        gameArenaMap.put(arena.getName(), arena);
    }

    public Collection<GameArena> getGameArenas() {
        return gameArenaMap.values();
    }

    public void removeGameArena(String name) {
        gameArenaMap.remove(name);
    }


}
