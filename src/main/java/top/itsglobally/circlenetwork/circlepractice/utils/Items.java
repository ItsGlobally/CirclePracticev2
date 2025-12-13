package top.itsglobally.circlenetwork.circlepractice.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import top.nontage.nontagelib.utils.item.ItemBuilder;

public class Items {
    public static ItemStack[] spawnInv() {
        ItemStack[] spawnInv = new ItemStack[36];
        spawnInv[0] = new ItemBuilder(Material.GOLD_SWORD)
                .onLeftClick(player -> {
                    player.openInventory(Menus.allPlayers("duel %player%"));
                })
                .onRightClick(player -> {
                    player.openInventory(Menus.allPlayers("duel %player%"));
                })
                .unBreak()
                .build();
        return spawnInv;
    }

}
