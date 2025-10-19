package top.itsglobally.circlenetwork.circlepractice.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import top.itsglobally.circlenetwork.circlepractice.data.Arena;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;

import java.util.*;

public class serializer {
    public static Map<String, Object> serializeArena(Arena arena) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("worldName", arena.getWorldName());
        map.put("name", arena.getName());
        map.put("pos1", serializeLocation(arena.getPos1()));
        map.put("pos2", serializeLocation(arena.getPos2()));
        map.put("spectatorSpawn", serializeLocation(arena.getSpectatorSpawn()));
        map.put("kits", new ArrayList<>(arena.getKits()));
        map.put("respawnablekit", arena.isRespawnableKit());
        if (arena.isRespawnableKit()) {
            map.put("bnsb1", serializeLocation(arena.getBnsb1()));
            map.put("bnsb2", serializeLocation(arena.getBnsb2()));
        }
        return map;
    }

    public static Arena deserializeArena(Map<String, Object> map) {
        String name = (String) map.getOrDefault("name", "Unknown");
        Arena arena = new Arena(name);
        if (map.containsKey("worldName")) arena.setWorldName(map.get("worldName").toString());
        if (map.containsKey("pos1")) arena.setPos1(deserializeLocation((Map<String, Object>) map.get("pos1")));
        if (map.containsKey("pos2")) arena.setPos2(deserializeLocation((Map<String, Object>) map.get("pos2")));
        if (map.containsKey("spectatorSpawn"))
            arena.setSpectatorSpawn(deserializeLocation((Map<String, Object>) map.get("spectatorSpawn")));
        if (map.containsKey("kits")) arena.getKits().addAll((List<String>) map.get("kits"));
        if (map.containsKey("respawnablekit")) arena.setRespawnableKit((Boolean) map.get("respawnablekit"));
        if (arena.isRespawnableKit()) {
            if (map.containsKey("bnsb1")) arena.setBnsb1(deserializeLocation((Map<String, Object>) map.get("bnsb1")));
            if (map.containsKey("bnsb2")) arena.setBnsb2(deserializeLocation((Map<String, Object>) map.get("bnsb2")));
        }
        return arena;
    }

    public static Map<String, Object> serializeKit(Kit kit) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", kit.getName());
        map.put("inventory", InventorySerializer.serializeInventory(kit.getContents(), kit.getArmor()));
        map.put("hunger", kit.isHunger());
        map.put("forDuel", kit.isForDuels());
        map.put("enabled", kit.isEnabled());
        map.put("canBuild", kit.isCanBuild());
        map.put("respawnable", kit.isRespawnable());
        if (kit.isRespawnable()) map.put("brokeToNoSpawn", kit.getBrokeToNoSpawn().name());
        if (kit.isCanBuild()) map.put("allowBreakBlocks", kit.getAllowBreakBlocks().stream().toList());
        return map;
    }

    public static Kit deserializeKit(Map<String, Object> map) {
        String name = (String) map.getOrDefault("name", "Unknown");
        Kit kit = new Kit(name);

        if (map.containsKey("inventory")) {
            Object invObj = map.get("inventory");
            ItemStack[][] items = null;

            if (invObj instanceof Map) {
                items = InventorySerializer.deserializeInventory((Map<String, Object>) invObj);
            }

            if (items != null) {
                kit.setContents(items[0]);
                if (items.length > 1) kit.setArmor(items[1]);
            }
        }

        if (map.containsKey("hunger")) {
            kit.setHunger((Boolean) map.get("hunger"));
        }
        if (map.containsKey("enabled")) {
            kit.setEnabled((Boolean) map.get("enabled"));
        }
        if (map.containsKey("forDuel")) {
            kit.setForDuels((Boolean) map.get("forDuel"));
        }
        if (map.containsKey("canBuild")) {
            kit.setCanBuild((Boolean) map.get("canBuild"));
        }
        if (map.containsKey("respawnable")) {
            kit.setRespawnable((Boolean) map.get("respawnable"));
        }
        if (map.containsKey("brokeToNoSpawn") && kit.isRespawnable()) {
            kit.setBrokeToNoSpawn(Material.valueOf((String) map.get("brokeToNoSpawn")));
        }
        if (kit.isCanBuild() && map.containsKey("allowBreakBlocks")) {
            Object obj = map.get("allowBreakBlocks");
            if (obj instanceof List<?> list) {
                Set<Material> blocks = new HashSet<>();
                for (Object o : list) {
                    if (o instanceof String s) {
                        try {
                            blocks.add(Material.valueOf(s));
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
                kit.setAllowBreakBlocks(blocks);
            }
        }



        return kit;
    }


    public static Map<String, Object> serializeLocation(Location loc) {
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

    public static Location deserializeLocation(Map<String, Object> map) {
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
