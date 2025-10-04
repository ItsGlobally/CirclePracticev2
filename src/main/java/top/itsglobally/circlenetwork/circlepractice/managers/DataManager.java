package top.itsglobally.circlenetwork.circlepractice.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import top.itsglobally.circlenetwork.circlepractice.data.Arena;
import top.itsglobally.circlenetwork.circlepractice.data.GameArena;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.utils.InventorySerializer;
import top.nontage.nontagelib.config.BaseConfig;

import java.io.File;
import java.util.*;

public class DataManager {

    private static File configDir;
    private static ArenaConfig arenaConfig;
    private static KitConfig kitConfig;

    private static final Map<String, Arena> arenaMap = new LinkedHashMap<>();
    private static final Map<String, GameArena> gameArenaMap = new LinkedHashMap<>();
    private static final Map<String, Kit> kitMap = new LinkedHashMap<>();

    // -------------------- 初始化 --------------------
    public void init(JavaPlugin plugin) {
        configDir = plugin.getDataFolder();
        if (!configDir.exists()) configDir.mkdirs();

        arenaConfig = register(new ArenaConfig(), "arenas");
        kitConfig = register(new KitConfig(), "kits");

        reload();
    }

    private <T extends BaseConfig> T register(T config, String name) {
        File file = new File(configDir, name + ".yml");
        config.initFile(file);
        config.reload();
        return config;
    }

    // -------------------- 重新加載 --------------------
    public void reload() {
        arenaConfig.reload();
        kitConfig.reload();

        arenaMap.clear();
        for (Map.Entry<String, Map<String, Object>> entry : arenaConfig.arenas.entrySet()) {
            arenaMap.put(entry.getKey(), deserializeArena(entry.getValue()));
        }

        kitMap.clear();
        for (Map.Entry<String, Map<String, Object>> entry : kitConfig.kits.entrySet()) {
            kitMap.put(entry.getKey(), deserializeKit(entry.getValue()));
        }
    }

    // -------------------- Arena API --------------------
    public void addArena(Arena arena) {
        arenaConfig.arenas.put(arena.getName(), serializeArena(arena));
        arenaConfig.save();
        arenaMap.put(arena.getName(), arena);
    }

    public Arena getArena(String name) {
        return arenaMap.get(name);
    }

    public Collection<Arena> getArenas() {
        return arenaMap.values();
    }

    public void removeArena(String name) {
        arenaConfig.arenas.remove(name);
        arenaConfig.save();
        arenaMap.remove(name);
    }
    public void addGameArena(GameArena arena) {
        gameArenaMap.put(arena.getName(),arena);
    }

    public Collection<GameArena> getGameArenas() {
        return gameArenaMap.values();
    }

    public void removeGameArena(String name) {
        gameArenaMap.remove(name);
    }

    // -------------------- Kit API --------------------
    public void addKit(Kit kit) {
        kitConfig.kits.put(kit.getName(), serializeKit(kit));
        kitConfig.save();
        kitMap.put(kit.getName(), kit);
    }

    public Kit getKit(String name) {
        return kitMap.get(name);
    }

    public Collection<Kit> getKits() {
        return kitMap.values();
    }

    public void removeKit(String name) {
        kitConfig.kits.remove(name);
        kitConfig.save();
        kitMap.remove(name);
    }

    // -------------------- Config 內部類 --------------------
    public class ArenaConfig extends BaseConfig {
        public Map<String, Map<String, Object>> arenas = new LinkedHashMap<>();
    }

    public class KitConfig extends BaseConfig {
        public Map<String, Map<String, Object>> kits = new LinkedHashMap<>();
    }

    // -------------------- Arena 序列化 --------------------
    private Map<String, Object> serializeArena(Arena arena) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("worldName", arena.getWorldName());
        map.put("name", arena.getName());
        map.put("pos1", serializeLocation(arena.getPos1()));
        map.put("pos2", serializeLocation(arena.getPos2()));
        map.put("spectatorSpawn", serializeLocation(arena.getSpectatorSpawn()));
        map.put("kits", new ArrayList<>(arena.getKits()));
        return map;
    }

    private Arena deserializeArena(Map<String, Object> map) {
        String name = (String) map.getOrDefault("name", "Unknown");
        Arena arena = new Arena(name);
        if (map.containsKey("worldName")) arena.setWorldName(map.get("worldName").toString());
        if (map.containsKey("pos1")) arena.setPos1(deserializeLocation((Map<String, Object>) map.get("pos1")));
        if (map.containsKey("pos2")) arena.setPos2(deserializeLocation((Map<String, Object>) map.get("pos2")));
        if (map.containsKey("spectatorSpawn")) arena.setSpectatorSpawn(deserializeLocation((Map<String, Object>) map.get("spectatorSpawn")));
        if (map.containsKey("kits")) arena.getKits().addAll((List<String>) map.get("kits"));
        return arena;
    }

    // -------------------- Kit 序列化 --------------------
    private Map<String, Object> serializeKit(Kit kit) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", kit.getName());
        map.put("inventory", InventorySerializer.serializeInventory(kit.getContents(), kit.getArmor()));
        map.put("hunger", kit.isHunger());
        return map;
    }

    private Kit deserializeKit(Map<String, Object> map) {
        String name = (String) map.getOrDefault("name", "Unknown");
        Kit kit = new Kit(name);

        if (map.containsKey("inventory")) {
            String data = (String) map.get("inventory");
            ItemStack[][] items = InventorySerializer.deserializeInventory(data);
            if (items != null) {
                kit.setContents(items[0]);
                if (items.length > 1) kit.setArmor(items[1]);
            }
        }
        if (map.containsKey("hunger")) {
            kit.setHunger((Boolean) map.get("hunger"));
        }

        return kit;
    }

    // -------------------- Location 序列化 --------------------
    private Map<String, Object> serializeLocation(Location loc) {
        if (loc == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("world", loc.getWorld().getName());
        map.put("x", loc.getX());
        map.put("y", loc.getY());
        map.put("z", loc.getZ());
        map.put("yaw", loc.getYaw());
        map.put("pitch", loc.getPitch());
        return map;
    }

    private Location deserializeLocation(Map<String, Object> map) {
        if (map == null) return null;
        return new Location(
                Bukkit.getWorld((String) map.get("world")),
                ((Number) map.get("x")).doubleValue(),
                ((Number) map.get("y")).doubleValue(),
                ((Number) map.get("z")).doubleValue(),
                ((Number) map.get("yaw")).floatValue(),
                ((Number) map.get("pitch")).floatValue()
        );
    }
}
