package top.itsglobally.circlenetwork.circlepractice.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import top.itsglobally.circlenetwork.circlepractice.achievement.Achievement;
import top.itsglobally.circlenetwork.circlepractice.data.Arena;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.managers.PlayerDataManager;
import top.itsglobally.circlenetwork.circlepractice.practical.BedBreak.BedBreakParticle;
import top.itsglobally.circlenetwork.circlepractice.practical.FinalKill.FinalKillParticle;

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
        map.put("voidY", arena.getVoidY());
        map.put("highLimitY", arena.getHighLimitY());
        map.put("remake", arena.isRemake());
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
        if (map.containsKey("voidY")) arena.setVoidY((int) map.get("voidY"));
        if (map.containsKey("highLimitY")) arena.setHighLimitY((int) map.get("highLimitY"));
        if (map.containsKey("remake")) arena.setRemake(Boolean.parseBoolean(String.valueOf(map.get("remake"))));
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
        if (kit.isRespawnable()) {
            map.put("brokeToNoSpawn", kit.getBrokeToNoSpawn().name());
            map.put("respawntime", kit.getRespawnTime());
        }
        if (kit.isCanBuild()) map.put("allowBreakBlocks", kit.getAllowBreakBlocks().stream().toList());
        map.put("freezeoncooldown", kit.isFreezeOnCooldown());
        map.put("nodamage", kit.isNodamage());
        map.put("counthit", kit.isCountHit());
        map.put("counthittodie", kit.getCountHitToDie());
        map.put("voidaddcount", kit.getVoidaddcount());
        map.put("icon", kit.getIcon().name());
        map.put("voidtpback", kit.isVoidTpBack());
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
                        } catch (IllegalArgumentException e) {
                            Bukkit.getLogger().info("Block not found: " + s);
                            e.printStackTrace();
                        }
                    }
                }
                kit.setAllowBreakBlocks(blocks);
            }
        }
        if (map.containsKey("respawntime")) kit.setRespawnTime(((Number) map.get("respawntime")).intValue());

        if (map.containsKey("freezeoncooldown")) kit.setFreezeOnCooldown(Boolean.parseBoolean(String.valueOf(map.get("freezeoncooldown"))));

        if (map.containsKey("nodamage")) kit.setNodamage(Boolean.parseBoolean(String.valueOf(map.get("nodamage"))));
        if (map.containsKey("counthit")) {
            kit.setCountHit(Boolean.parseBoolean(String.valueOf(map.get("counthit"))));
        }
        if (map.containsKey("counthittodie")) {
            kit.setCountHitToDie(((Number) map.get("counthittodie")).intValue());
        }
        if (map.containsKey("voidaddcount")) kit.setVoidaddcount(((Number) map.get("voidaddcount")).intValue());
        if (map.containsKey("voidtpback")) kit.setVoidTpBack(Boolean.parseBoolean(String.valueOf(map.get("voidtpback"))));

        if (map.containsKey("icon")) {
            String mname = String.valueOf(map.get("icon"));
            try {
                kit.setIcon(Material.valueOf(mname));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().info("Block not found: " + mname);
                e.printStackTrace();
                kit.setIcon(Material.IRON_SWORD);
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

    public static Map<String, Object> serializePlayerData(PlayerDataManager.PlayerData pd) {
        if (pd == null) return null;

        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Object> kits = new LinkedHashMap<>();

        for (Map.Entry<String, ItemStack[][]> e : pd.getAllKits().entrySet()) {
            String kitName = e.getKey().toLowerCase();
            ItemStack[][] contents = e.getValue();
            kits.put(kitName, InventorySerializer.serializeInventory(contents[0], contents[1]));
        }

        List<String> achievements = pd.getUnlockedAchievement().stream()
                .map(Achievement::name)
                .toList();

        map.put("uuid", pd.getUuid().toString());
        map.put("kits", kits);
        map.put("stars", pd.getStars());
        map.put("xp", pd.getXps());
        map.put("achievements", achievements);
        map.put("finalKillParticle", pd.getFinalKillParticle().getId());
        map.put("bedBreakParticle", pd.getBedBreakParticle().getId());

        return map;
    }


    @SuppressWarnings("unchecked")
    public static PlayerDataManager.PlayerData deserializePlayerData(Map<String, Object> map) {
        if (map == null) return null;
        if (!map.containsKey("uuid")) return null;
        String uuidStr = String.valueOf(map.get("uuid"));
        PlayerDataManager.PlayerData pd = new PlayerDataManager.PlayerData(UUID.fromString(uuidStr));

        if (map.containsKey("kits")) {
            Object kitsObj = map.get("kits");
            if (kitsObj instanceof Map<?, ?> kitsMap) {
                for (Map.Entry<?, ?> entry : kitsMap.entrySet()) {
                    String kitName = String.valueOf(entry.getKey()).toLowerCase();
                    Object kitData = entry.getValue();

                    if (kitData instanceof Map<?, ?> kitMap) {
                        try {
                            ItemStack[][] contents = InventorySerializer.deserializeInventory((Map<String, Object>) kitMap);
                            pd.setKitContents(kitName, contents);
                        } catch (Exception ex) {
                            System.err.println("[CirclePractice] 無法反序列化 kit: " + kitName);
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }


        if (map.containsKey("achievements")) {
            Object achievementsObj = map.get("achievements");
            if (achievementsObj instanceof List<?> list) {
                for (Object o : list) {
                    if (o instanceof String s) {
                        try {
                            Achievement a = Achievement.valueOf(s);
                            pd.unlockAchievement(a, false);
                        } catch (IllegalArgumentException ignored) {
                            System.err.println("[CirclePractice] 無效的 achievement: " + s);
                        }
                    }
                }
            }
        }

        if (map.containsKey("stars")) {
            Object starsObj = map.get("stars");
            if (starsObj instanceof Number) {
                pd.setStars(((Number) starsObj).longValue());
            } else {
                System.err.println("[CirclePractice] stars 欄位不是數字類型: " + starsObj);
            }
        }

        if (map.containsKey("xp")) {
            Object xpObj = map.get("xp");
            if (xpObj instanceof Number) {
                pd.setXps(((Number) xpObj).longValue());
            } else {
                System.err.println("[CirclePractice] xp 欄位不是數字類型: " + xpObj);
            }
        }

        if (map.containsKey("finalKillParticle")) {
            pd.setFinalKillParticle(FinalKillParticle.fromId(String.valueOf(map.get("finalKillParticle"))));
        }
        if (map.containsKey("bedBreakParticle")) {
            pd.setBedBreakParticle(BedBreakParticle.fromId(String.valueOf(map.get("bedBreakParticle"))));
        }

        return pd;
    }


}
