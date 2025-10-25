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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
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

    private static final Map<String, Arena> arenaMap = new LinkedHashMap<>();
    private static final Map<String, GameArena> gameArenaMap = new LinkedHashMap<>();
    private static ArenaConfig arenaConfig;
    private final SlimePlugin slime = plugin.getPluginManager().getSlimePlugin;
    private final SlimeLoader sl = plugin.getPluginManager().getSlimePlugin.getLoader("file");

    public ArenaManager() {
        arenaConfig = ConfigRegister.register(new ArenaConfig(), "arenas");
        if (arenaConfig.arenas == null) arenaConfig.arenas = new LinkedHashMap<>();
        reload();
        createDefaultArenas();
    }

    public void cloneArena(Arena arena, String newWorldName) {
        if (arena == null || newWorldName == null || newWorldName.isEmpty()) {
            Bukkit.getLogger().warning("Cannot clone arena: invalid parameters");
            return;
        }

        try {
            SlimeLoader loader = slime.getLoader("file");

            if (Bukkit.getWorld(arena.getWorldName()) != null) {
                sl.unlockWorld(arena.getWorldName());
            }

            SlimeWorld sourceWorld = slime.loadWorld(loader, arena.getWorldName(), false, gameArenaProps());
            SlimeWorld cloned = sourceWorld.clone(newWorldName);
            slime.generateWorld(cloned);

        } catch (UnknownWorldException | IOException | CorruptedWorldException |
                 NewerFormatException | WorldInUseException ex) {
            Bukkit.broadcastMessage(MessageUtil.formatMessage("&d&l✗ &fError loading arena: " + ex.getMessage()));
            ex.printStackTrace();
        }
    }

    public GameArena createGameArena(Kit kit) {
        if (kit == null) {
            Bukkit.getLogger().warning("Cannot create game arena: kit is null");
            return null;
        }

        String newWorldName = "arena_" + UUID.randomUUID().toString().substring(0, 8);

        Collection<Arena> arenas = getAvailableArenas();
        if (arenas.isEmpty()) {
            Bukkit.getLogger().warning("No available arenas found");
            return null;
        }

        List<Arena> compatibleArenas = arenas.stream()
                .filter(arena -> arena.getKits() != null && arena.getKits().contains(kit.getName()))
                .toList();

        if (compatibleArenas.isEmpty()) {
            Bukkit.getLogger().warning("No arenas found for kit: " + kit.getName());
            return null;
        }

        Arena selectedArena = compatibleArenas.get(RandomUtil.nextInt(compatibleArenas.size()));
        cloneArena(selectedArena, newWorldName);

        GameArena gameArena = new GameArena(selectedArena.getName(), newWorldName);
        gameArena.convertFromArena(selectedArena);

        addGameArena(gameArena);
        return gameArena;
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

        if (arenaConfig.arenas == null) {
            arenaConfig.arenas = new LinkedHashMap<>();
        }

        arenaMap.clear();
        for (Map.Entry<String, Map<String, Object>> entry : arenaConfig.arenas.entrySet()) {
            try {
                Arena arena = serializer.deserializeArena(entry.getValue());
                if (arena != null) {
                    arenaMap.put(entry.getKey(), arena);
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to deserialize arena: " + entry.getKey());
                e.printStackTrace();
            }
        }
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
        gameArenaMap.put(arena.getName().toLowerCase(), arena);
    }

    public Collection<GameArena> getGameArenas() {
        return gameArenaMap.values();
    }

    public void removeGameArena(GameArena arena) {
        if (arena == null) return;

        String key = arena.getWorldName().toLowerCase();

        try {
            sl.unlockWorld(arena.getWorldName());
        } catch (UnknownWorldException | IOException ignored) {
        }

        World world = Bukkit.getWorld(arena.getWorldName());

        if (!Bukkit.unloadWorld(world, false)) {
            Bukkit.broadcastMessage("§d§l✗ §fFailed to unload world: §d" + arena.getWorldName());
        }

        if (gameArenaMap.remove(arena.getName().toLowerCase()) == null) {
            Bukkit.broadcastMessage("§c[DEBUG] Map did not contain key: " + key);
        }
    }

    public void clearGameArenas() {
        for (GameArena ga : new ArrayList<>(gameArenaMap.values())) {
            removeGameArena(ga);
        }
    }

    public class ArenaConfig extends BaseConfig {
        public Map<String, Map<String, Object>> arenas = new LinkedHashMap<>();
    }
}
