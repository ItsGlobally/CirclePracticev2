package top.itsglobally.circlenetwork.circlepractice.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import top.nontage.nontagelib.utils.item.ItemBuilder;

public class Items {
    public static ItemStack[] spawnInv() {
        ItemStack[] spawnInv = new ItemStack[36];
        spawnInv[0] = new ItemBuilder(Material.GOLD_SWORD)
                .setName("&e&lQueue")
                .onLeftClick(player -> {
                    player.openInventory(Menus.queue(player));
                })
                .onRightClick(player -> {
                    player.openInventory(Menus.queue(player));
                })
                .unBreak()
                .build();
        spawnInv[1] = new ItemBuilder(Material.DIAMOND_SWORD)
                .setName("&e&lDuel Online Players")
                .onLeftClick(player -> {
                    player.openInventory(Menus.allPlayers("duel %player%"));
                })
                .onRightClick(player -> {
                    player.openInventory(Menus.allPlayers("duel %player%"));
                })
                .unBreak()
                .build();
        spawnInv[4] = new ItemBuilder(Material.BOOK)
                .setName("&e&lKit Editor")
                .onLeftClick(player -> {
                    player.openInventory(Menus.kitEdit(player));
                })
                .onRightClick(player -> {
                    player.openInventory(Menus.kitEdit(player));
                })
                .unBreak()
                .build();
        spawnInv[8] = new ItemBuilder(Material.ITEM_FRAME)
                .setName("&e&lSettings")
                .onLeftClick(player -> {
                    player.openInventory(Menus.settings(player));
                })
                .onRightClick(player -> {
                    player.openInventory(Menus.settings(player));
                })
                .unBreak()
                .build();
        return spawnInv;
    }

}
