package top.itsglobally.circlenetwork.circlepractice.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * InventorySerializer - YAML-friendly readable serialization.
 * Converts inventories to List<Map<String, Object>> instead of Base64.
 */
public class InventorySerializer {

    /**
     * Serialize inventory contents and armor into a readable structure.
     */
    public static Map<String, Object> serializeInventory(ItemStack[] contents, ItemStack[] armor) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("contents", serializeItemList(contents));
        map.put("armor", serializeItemList(armor));
        return map;
    }

    /**
     * Deserialize inventory contents and armor.
     */
    @SuppressWarnings("unchecked")
    public static ItemStack[][] deserializeInventory(Map<String, Object> data) {
        if (data == null) return new ItemStack[][]{new ItemStack[0], new ItemStack[0]};

        List<Map<String, Object>> contentsList = (List<Map<String, Object>>) data.get("contents");
        List<Map<String, Object>> armorList = (List<Map<String, Object>>) data.get("armor");

        ItemStack[] contents = deserializeItemList(contentsList);
        ItemStack[] armor = deserializeItemList(armorList);

        return new ItemStack[][]{contents, armor};
    }

    // ----------------- Internal helpers -----------------

    private static List<Map<String, Object>> serializeItemList(ItemStack[] items) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (items == null) return list;
        for (ItemStack item : items) list.add(serializeItem(item));
        return list;
    }

    private static ItemStack[] deserializeItemList(List<Map<String, Object>> list) {
        if (list == null) return new ItemStack[0];
        ItemStack[] items = new ItemStack[list.size()];
        for (int i = 0; i < list.size(); i++) {
            items[i] = deserializeItem(list.get(i));
        }
        return items;
    }

    private static Map<String, Object> serializeItem(ItemStack item) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (item == null || item.getType() == Material.AIR) {
            map.put("type", "AIR");
            return map;
        }

        map.put("type", item.getType().name());
        map.put("amount", item.getAmount());
        map.put("durability", item.getDurability());
        map.put("unbreakable", item.getItemMeta().spigot().isUnbreakable());

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) map.put("name", meta.getDisplayName());
            if (meta.hasLore()) map.put("lore", meta.getLore());

            if (meta.hasEnchants()) {
                Map<String, Integer> enchants = new LinkedHashMap<>();
                for (Map.Entry<Enchantment, Integer> e : meta.getEnchants().entrySet()) {
                    enchants.put(e.getKey().getName(), e.getValue());
                }
                map.put("enchants", enchants);
            }
        }

        return map;
    }

    @SuppressWarnings("unchecked")
    private static ItemStack deserializeItem(Map<String, Object> map) {
        if (map == null || !map.containsKey("type")) return new ItemStack(Material.AIR);
        Material type = Material.getMaterial((String) map.getOrDefault("type", "AIR"));
        if (type == null) type = Material.AIR;

        ItemStack item = new ItemStack(type);
        item.setAmount((int) map.getOrDefault("amount", 1));
        item.setDurability(((Number) map.getOrDefault("durability", 0)).shortValue());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (map.containsKey("name")) meta.setDisplayName((String) map.get("name"));
            if (map.containsKey("lore")) meta.setLore((List<String>) map.get("lore"));
            if (map.containsKey("unbreakable")) meta.spigot().setUnbreakable(Boolean.parseBoolean(String.valueOf(map.get("unbreakable"))));
            if (map.containsKey("enchants")) {
                Map<String, Integer> enchants = (Map<String, Integer>) map.get("enchants");
                for (Map.Entry<String, Integer> e : enchants.entrySet()) {
                    Enchantment ench = Enchantment.getByName(e.getKey());
                    if (ench != null) meta.addEnchant(ench, e.getValue(), true);
                }
            }

            item.setItemMeta(meta);
        }

        return item;
    }
}
