package top.itsglobally.circlenetwork.circlepractice.managers;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.utils.ConfigRegister;
import top.itsglobally.circlenetwork.circlepractice.utils.serializer;
import top.nontage.nontagelib.config.BaseConfig;
import top.nontage.nontagelib.utils.item.ItemBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KitManager extends Managers {
    private static KitConfig kitConfig;
    private final List<Kit> kits = new ArrayList<>();
    public KitManager() {
        kitConfig = ConfigRegister.register(new KitConfig(), "kits");
        if (kitConfig.kits == null) kitConfig.kits = new LinkedHashMap<>();
        reload();
        createDefaultKits();
    }
    public class KitConfig extends BaseConfig {
        public Map<String, Map<String, Object>> kits = new LinkedHashMap<>();
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
        kits.removeIf(k -> k.getName().equalsIgnoreCase(name));

        Kit newKit = new Kit(name);
        newKit.setContents(contents);
        newKit.setArmor(armor);
        kits.add(newKit);
    }
    public void updateKit(Kit kit) {
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
        if (kitConfig.kits == null) kitConfig.kits = new LinkedHashMap<>();
        List<Kit> kits = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : kitConfig.kits.entrySet()) {
            kits.add(serializer.deserializeKit(entry.getValue()));
        }
        setKits(kits);
    }
    public void saveAllKits() {
        kitConfig.kits.clear();
        for (Kit kit : getKits()) {
            kitConfig.kits.put(kit.getName(), serializer.serializeKit(kit));
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

            addKit(noDebuff);
            saveAllKits();
        }
    }

}
