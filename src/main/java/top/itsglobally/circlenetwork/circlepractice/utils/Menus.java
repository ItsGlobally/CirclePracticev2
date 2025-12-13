package top.itsglobally.circlenetwork.circlepractice.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.nontage.nontagelib.utils.inventory.InventoryBuilder;
import top.nontage.nontagelib.utils.item.ItemBuilder;

public class Menus implements GlobalInterface {
    /*
      0 1 2 3 4 5 6 7 8
    1
    2
    3
    4
    5
    6
     */
    public static Inventory duel(Player p, Player target) {
        InventoryBuilder ib = new InventoryBuilder(9*6, "Duel");
        for (int i = 0; i <= 36; i++) {
            ib.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .setName("&c")
                            .durability(7)
                            .build());
        }
        int currentSlot = 0;
        for (Kit k : plugin.getKitManager().getKits()) {
            ib.setItem(new ItemBuilder(Material.IRON_SWORD)
                    .setName(k.getName())
                    .build(), currentSlot);
            ib.setClickEvent(clickInventoryEvent -> {
                p.performCommand("duel " + target.getName() + " " + k.getName());
            }, currentSlot);
            currentSlot++;
        }
        ib.setAllowableDrag(false);
        ib.setAllClickable(false);
        return ib.getInventory();
    }
    public static int getInventorySize() {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int rows = (int) Math.ceil(onlinePlayers / 9.0);
        return Math.min(rows * 9, 54);
    }
    public static Inventory allPlayers(String command) {
        InventoryBuilder ib = new InventoryBuilder(getInventorySize(), "All Players");
        for (int i = 0; i <= 36; i++) {
            ib.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setName("&c")
                    .durability(7)
                    .build());
        }
        int currentSlot = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            ib.setItem(new ItemBuilder(Material.SKULL_ITEM).owner(p.getName()).build(), currentSlot);
            ib.setClickEvent(clickInventoryEvent -> {
                clickInventoryEvent.getPlayer().performCommand(command.replace("%player%", p.getName()));
            }, currentSlot);
            currentSlot++;
        }

        return ib.getInventory();
    }
}
