package top.itsglobally.circlenetwork.circlepractice.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.nontage.nontagelib.utils.inventory.InventoryBuilder;
import top.nontage.nontagelib.utils.item.ItemBuilder;

import java.util.Collection;
import java.util.List;

public class Menus implements GlobalInterface {

    @FunctionalInterface
    private interface KitClickAction {
        void onClick(Player p, Kit kit);
    }

    @FunctionalInterface
    private interface PlayerClickAction {
        void onClick(Player clicker, Player target);
    }

    private static Inventory buildKitMenu(Player p, String title, KitClickAction clickAction) {
        List<Kit> kits = plugin.getKitManager().getKits();
        InventoryBuilder ib = new InventoryBuilder(getInventorySize(kits.size()), title);

        fillBackground(ib);

        int currentSlot = 0;
        for (Kit k : kits) {
            ib.setItem(new ItemBuilder(Material.IRON_SWORD)
                    .setName(k.getName())
                    .build(), currentSlot);

            final int slot = currentSlot;
            ib.setClickEvent(clickInventoryEvent -> clickAction.onClick(p, k), slot);

            currentSlot++;
        }

        ib.setAllowableDrag(false);
        ib.setAllClickable(false);

        return ib.getInventory();
    }

    public static Inventory duel(Player p, Player target) {
        return buildKitMenu(p, "Duel", (player, kit) ->
                player.performCommand("duel " + target.getName() + " " + kit.getName())
        );
    }

    public static Inventory queue(Player p) {
        return buildKitMenu(p, "Queue", (player, kit) ->
                player.performCommand("queue " + kit.getName())
        );
    }

    public static Inventory kitEdit(Player p) {
        p.closeInventory();
        return buildKitMenu(p, "Kit Edit", (player, kit) ->
                player.performCommand("kit edit " + kit.getName())
        );
    }

    private static Inventory buildPlayerMenu(String title, PlayerClickAction clickAction) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        InventoryBuilder ib = new InventoryBuilder(getInventorySize(players.size()), title);

        fillBackground(ib);

        int currentSlot = 0;
        for (Player p : players) {
            ib.setItem(new ItemBuilder(Material.SKULL_ITEM)
                            .owner(p.getName())
                            .durability(3)
                            .build(),
                    currentSlot
            );

            final Player target = p;
            ib.setClickEvent(clickInventoryEvent -> clickAction.onClick(clickInventoryEvent.getPlayer(), target), currentSlot);

            currentSlot++;
        }

        ib.setAllowableDrag(false);
        ib.setAllClickable(false);

        return ib.getInventory();
    }

    public static Inventory allPlayers(String command) {
        return buildPlayerMenu("All Players", (clicker, target) ->
                clicker.performCommand(command.replace("%player%", target.getName()))
        );
    }
    private static void fillBackground(InventoryBuilder ib) {
        for (int i = 0; i <= 36; i++) {
            ib.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setName("&c")
                    .durability(7)
                    .build());
        }
    }

    public static int getInventorySize(int size) {
        int rows = (int) Math.ceil(size / 9.0);
        return Math.min(rows * 9, 54);
    }
}
