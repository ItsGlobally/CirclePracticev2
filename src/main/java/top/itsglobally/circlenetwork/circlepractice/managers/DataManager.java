package top.itsglobally.circlenetwork.circlepractice.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import top.itsglobally.circlenetwork.circlepractice.data.Arena;
import top.itsglobally.circlenetwork.circlepractice.data.GameArena;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.utils.InventorySerializer;
import top.nontage.nontagelib.config.BaseConfig;
import top.nontage.nontagelib.utils.item.ItemBuilder;

import java.io.File;
import java.util.*;

public class DataManager extends Managers {

    private static final Map<String, Arena> arenaMap = new LinkedHashMap<>();
    private static final Map<String, GameArena> gameArenaMap = new LinkedHashMap<>();
    private static final Map<String, Kit> kitMap = new LinkedHashMap<>();
    private static File configDir;
    private static ArenaConfig arenaConfig;
    private static KitConfig kitConfig;
    private static MainConfig mainConfig;

    public DataManager() {
        init(plugin);
    }

    // -------------------- 初始化 --------------------
    public void init(JavaPlugin plugin) {
        configDir = plugin.getDataFolder();
        if (!configDir.exists()) configDir.mkdirs();

        arenaConfig = register(new ArenaConfig(), "arenas");
        kitConfig = register(new KitConfig(), "kits");
        mainConfig = register(new MainConfig(), "config");
        if (arenaConfig.arenas == null) arenaConfig.arenas = new LinkedHashMap<>();
        if (kitConfig.kits == null) kitConfig.kits = new LinkedHashMap<>();
        reload();
        createDefaults();
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

        if (arenaConfig.arenas == null) arenaConfig.arenas = new LinkedHashMap<>();
        if (kitConfig.kits == null) kitConfig.kits = new LinkedHashMap<>();

        arenaMap.clear();
        for (Map.Entry<String, Map<String, Object>> entry : arenaConfig.arenas.entrySet()) {
            arenaMap.put(entry.getKey(), deserializeArena(entry.getValue()));
        }

        List<Kit> kits = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : kitConfig.kits.entrySet()) {
            kits.add(deserializeKit(entry.getValue()));
        }
        plugin.getKitManager().setKits(kits);
    }
    public void saveAllKits() {
        KitManager km = plugin.getKitManager();
        kitConfig.kits.clear();
        for (Kit kit : km.getKits()) {
            kitConfig.kits.put(kit.getName(), serializeKit(kit));
        }
        kitConfig.save();
    }
    public void saveAllArenas() {
        arenaConfig.save();
    }

    private void createDefaults() {
        if (!plugin.getKitManager().kitAlreadyExist("NoDebuff")) {
            Kit noDebuff = new Kit("NoDebuff");

            ItemStack[] armor = new ItemStack[4];
            armor[3] = new ItemBuilder(Material.DIAMOND_HELMET)
                    .enchant(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .enchant(org.bukkit.enchantments.Enchantment.DURABILITY, 3)
                    .unBreak()
                    .build();
            armor[2] = new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                    .enchant(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .enchant(org.bukkit.enchantments.Enchantment.DURABILITY, 3)
                    .unBreak()
                    .build();
            armor[1] = new ItemBuilder(Material.DIAMOND_LEGGINGS)
                    .enchant(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .enchant(org.bukkit.enchantments.Enchantment.DURABILITY, 3)
                    .unBreak()
                    .build();
            armor[0] = new ItemBuilder(Material.DIAMOND_BOOTS)
                    .enchant(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .enchant(org.bukkit.enchantments.Enchantment.DURABILITY, 3)
                    .unBreak()
                    .build();

            ItemStack[] contents = new ItemStack[36];
            contents[0] = new ItemBuilder(Material.DIAMOND_SWORD)
                    .enchant(Enchantment.DAMAGE_ALL, 3)
                    .enchant(Enchantment.DURABILITY, 3)
                    .enchant(Enchantment.FIRE_ASPECT, 2)
                    .unBreak()
                    .build();

            contents[1] = new ItemStack(Material.COOKED_BEEF, 64);

            contents[2] = new ItemBuilder(Material.POTION)
                    .durability((short) 8226)
                    .build();
            ItemStack firepot = new ItemBuilder(Material.POTION)
                    .durability((short) 8195)
                    .build();
            PotionMeta meta = (PotionMeta) firepot.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true);
            firepot.setItemMeta(meta);

            contents[3] = firepot;
            contents[4] = new ItemBuilder(Material.ENDER_PEARL)
                    .setAmount(16)
                    .build();

            for (int i = 5; i < 33; i++) {
                contents[i] = new ItemBuilder(Material.POTION)
                        .durability((short) 16421)
                        .build();
            }
            for (int i = 33; i < 36; i++) {
                contents[i] = new ItemBuilder(Material.POTION)
                        .durability((short) 8226)
                        .build();
            }


            noDebuff.setArmor(armor);
            noDebuff.setContents(contents);
            noDebuff.setHunger(true);
            noDebuff.setEnabled(true);

            plugin.getKitManager().addKit(noDebuff);
            saveAllKits();
        }

        if (getArena("arena") == null) {
            Arena arena = new Arena("arena");
            arena.setWorldName("arena");
            arena.setPos1(new Location(Bukkit.getWorld(arena.getWorldName()), 50, 51, 0, 0, 0));
            arena.setPos2(new Location(Bukkit.getWorld(arena.getWorldName()), -50, 51, 0, 180, 0));
            arena.setSpectatorSpawn(new Location(Bukkit.getWorld(arena.getWorldName()), 0, 75, 0, 90, 0));
            arena.addKit("NoDebuff");

            addArena(arena);
            saveAllArenas();
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
        if (map.containsKey("spectatorSpawn"))
            arena.setSpectatorSpawn(deserializeLocation((Map<String, Object>) map.get("spectatorSpawn")));
        if (map.containsKey("kits")) arena.getKits().addAll((List<String>) map.get("kits"));
        return arena;
    }

    // -------------------- Kit 序列化 --------------------
    private Map<String, Object> serializeKit(Kit kit) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", kit.getName());
        map.put("inventory", InventorySerializer.serializeInventory(kit.getContents(), kit.getArmor()));
        map.put("hunger", kit.isHunger());
        map.put("forDuel", kit.isForDuels());
        map.put("enabled", kit.isEnabled());
        map.put("canBuild", kit.isCanBuild());
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
        if (map.containsKey("enabled")) {
            kit.setHunger((Boolean) map.get("enabled"));
        }
        if (map.containsKey("forDuel")) {
            kit.setHunger((Boolean) map.get("forDuel"));
        }
        if (map.containsKey("canBuild")) {
            kit.setHunger((Boolean) map.get("canBuild"));
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

    public void teleportToSpawn(Player p) {
        p.teleport(mainConfig.getSpawn());
    }

    // -------------------- Config 內部類 --------------------
    public class ArenaConfig extends BaseConfig {
        public Map<String, Map<String, Object>> arenas = new LinkedHashMap<>();
    }

    public class KitConfig extends BaseConfig {
        public Map<String, Map<String, Object>> kits = new LinkedHashMap<>();
    }

    public class MainConfig extends BaseConfig {
        // -------------------- 可配置的欄位 --------------------
        public String defaultKit = "NoDebuff";
        public boolean allowSpectators = true;
        public int duelRequestExpire = 60;
        public int maxGameTime = 900;
        public String spawnWorld = "spawn";
        public double spawnX = 0.5;
        public double spawnY = 65;
        public double spawnZ = 0.5;
        public float spawnYaw = 0f;
        public float spawnPitch = 0f;

        public Location getSpawn() {
            if (Bukkit.getWorld(spawnWorld) == null) return null;
            return new Location(Bukkit.getWorld(spawnWorld), spawnX, spawnY, spawnZ, spawnYaw, spawnPitch);
        }

        // -------------------- API --------------------
        public String getDefaultKit() {
            return defaultKit;
        }

        public boolean isAllowSpectators() {
            return allowSpectators;
        }

        public int getDuelRequestExpire() {
            return duelRequestExpire;
        }

        public int getMaxGameTime() {
            return maxGameTime;
        }
    }
}
