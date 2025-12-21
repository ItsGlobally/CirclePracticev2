package top.itsglobally.circlenetwork.circlepractice.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.utils.ConfigRegister;
import top.itsglobally.circlenetwork.circlepractice.utils.ConfigSerializer;
import top.nontage.nontagelib.config.BaseConfig;
import top.nontage.nontagelib.utils.item.ItemBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KitManager implements GlobalInterface {
    private static KitConfig kitConfig;
    private final List<Kit> kits = new ArrayList<>();

    public KitManager() {
        kitConfig = ConfigRegister.register(new KitConfig(), "kits");
        if (kitConfig.kits == null) kitConfig.kits = new LinkedHashMap<>();
        reload();
        createDefaultKits();
    }

    public List<Kit> getKits() {
        return kits;
    }

    public void setKits(List<Kit> kits) {
        this.kits.clear();
        this.kits.addAll(kits);
    }

    public Kit getKit(String name) {
        return kits.stream()
                .filter(k -> k.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public boolean kitAlreadyExist(String name) {
        return kits.stream()
                .anyMatch(k -> k.getName().equalsIgnoreCase(name));
    }

    public void updateKit(String name, ItemStack[] contents, ItemStack[] armor) {
        if (name == null || name.isEmpty()) return;

        Kit existingKit = getKit(name);
        if (existingKit == null) {
            Kit newKit = new Kit(name);
            newKit.setContents(contents);
            newKit.setArmor(armor);
            kits.add(newKit);
            return;
        }

        existingKit.setContents(contents);
        existingKit.setArmor(armor);

        kits.removeIf(k -> k.getName().equalsIgnoreCase(name));
        kits.add(existingKit);
    }


    public void updateKit(Kit kit) {
        if (kit == null) return;
        kits.removeIf(k -> k.getName().equalsIgnoreCase(kit.getName()));
        kits.add(kit);
    }

    public void addKit(Kit kit) {
        if (kit == null) return;

        kits.removeIf(k -> k.getName().equalsIgnoreCase(kit.getName()));

        kits.add(kit);
    }

    public void removeKit(Kit kit) {
        kits.removeIf(k -> k.getName().equalsIgnoreCase(kit.getName()));
    }

    public void removeKit(String name) {
        kits.removeIf(k -> k.getName().equalsIgnoreCase(name));
    }

    public void reload() {
        kitConfig.reload();
        if (kitConfig.kits == null) {
            kitConfig.kits = new LinkedHashMap<>();
        }

        List<Kit> loadedKits = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : kitConfig.kits.entrySet()) {
            try {
                Kit kit = ConfigSerializer.deserializeKit(entry.getValue());
                if (kit != null) {
                    loadedKits.add(kit);
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to deserialize kit: " + entry.getKey());
                e.printStackTrace();
            }
        }
        setKits(loadedKits);
    }

    public void saveAllKits() {
        kitConfig.kits.clear();
        for (Kit kit : getKits()) {
            kitConfig.kits.put(kit.getName(), ConfigSerializer.serializeKit(kit));
        }
        kitConfig.save();
    }

    public void createDefaultKits() {
        if (!kitAlreadyExist("NoDebuff")) {
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
            noDebuff.setRespawnable(false);
            addKit(noDebuff);
        }
        if (!kitAlreadyExist("bedfight")) {
            Kit bedFight = new Kit("bedfight");

            ItemStack[] armor = new ItemStack[4];

            armor[3] = new ItemBuilder(Material.LEATHER_HELMET)
                    .unBreak()
                    .build();
            armor[2] = new ItemBuilder(Material.LEATHER_CHESTPLATE)
                    .unBreak()
                    .build();
            armor[1] = new ItemBuilder(Material.LEATHER_LEGGINGS)
                    .unBreak()
                    .build();
            armor[0] = new ItemBuilder(Material.LEATHER_BOOTS)
                    .unBreak()
                    .build();

            ItemStack[] contents = new ItemStack[36];
            contents[0] = new ItemBuilder(Material.WOOD_SWORD)
                    .unBreak()
                    .build();
            contents[1] = new ItemBuilder(Material.SHEARS)
                    .unBreak()
                    .build();
            contents[2] = new ItemBuilder(Material.WOOD_AXE)
                    .enchant(Enchantment.DIG_SPEED, 1)
                    .unBreak()
                    .build();
            contents[3] = new ItemBuilder(Material.WOOD_PICKAXE)
                    .enchant(Enchantment.DIG_SPEED, 1)
                    .unBreak()
                    .build();
            contents[4] = new ItemBuilder(Material.WOOL)
                    .setAmount(64)
                    .build();

            bedFight.setArmor(armor);
            bedFight.setContents(contents);
            bedFight.setRespawnable(true);
            bedFight.setBrokeToNoSpawn(Material.BED_BLOCK);
            bedFight.setCanBuild(true);
            bedFight.setHunger(false);
            bedFight.setEnabled(true);
            bedFight.addAllowBreakBlocks(Material.WOOL);
            bedFight.addAllowBreakBlocks(Material.ENDER_STONE);
            bedFight.addAllowBreakBlocks(Material.WOOD);
            addKit(bedFight);
        }
        saveAllKits();
    }

    public class KitConfig extends BaseConfig {
        public Map<String, Map<String, Object>> kits = new LinkedHashMap<>();
    }

}
