package top.itsglobally.circlenetwork.circlepractice.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;
import top.itsglobally.circlenetwork.circlepractice.practical.BedBreakParticle;
import top.itsglobally.circlenetwork.circlepractice.practical.FinalKillParticle;
import top.itsglobally.circlenetwork.circlepractice.practical.Particles;
import top.nontage.nontagelib.utils.inventory.InventoryBuilder;
import top.nontage.nontagelib.utils.item.ItemBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Menus implements GlobalInterface {

    private static Inventory buildKitMenu(Player p, String title, KitClickAction clickAction) {
        List<Kit> kits = plugin.getKitManager().getKits();
        InventoryBuilder ib = filledBackground(new InventoryBuilder(getInventorySize(kits.size()), title));


        int currentSlot = 0;
        for (Kit k : kits) {
            ib.setItem(new ItemBuilder(k.getIcon())
                    .setName("&e&l" + k.getName())
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
        InventoryBuilder ib = filledBackground(new InventoryBuilder(getInventorySize(players.size()), title));

        int currentSlot = 0;
        for (Player p : players) {
            ib.setItem(new ItemBuilder(Material.SKULL_ITEM)
                            .owner(p.getName())
                            .setName("&e&l" + p.getName())
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

    public static Inventory particleMenu(Player p, Particles par) {
        InventoryBuilder ib = filledBackground(new InventoryBuilder(9 * 5, "Particles"));
        int currentSlot = 0;
        if (par == Particles.FinalKill) {
            Bukkit.getLogger().info(String.valueOf(FinalKillParticle.values().length));
            Bukkit.getLogger().info(Arrays.toString(FinalKillParticle.values()));
            for (FinalKillParticle fkp : FinalKillParticle.values()) {
                if (!p.hasPermission(fkp.getPermission())) continue;
                ib.setItem(new ItemBuilder(fkp.getIcon()).setName("&e&l" + fkp.name()).build(), currentSlot);
                ib.setClickEvent(clickInventoryEvent -> {
                    p.performCommand("particle " + fkp.name());
                }, currentSlot);
                currentSlot++;
            }
        }
        if (par == Particles.BedBreak) {
            Bukkit.getLogger().info(String.valueOf(BedBreakParticle.values().length));
            Bukkit.getLogger().info(Arrays.toString(BedBreakParticle.values()));
            for (BedBreakParticle fkp : BedBreakParticle.values()) {
                if (!p.hasPermission(fkp.getPermission())) continue;
                ib.setItem(new ItemBuilder(fkp.getIcon()).setName("&e&l" + fkp.name()).build(), currentSlot);
                ib.setClickEvent(clickInventoryEvent -> {
                    p.performCommand("particle " + fkp.name());
                }, currentSlot);
                currentSlot++;
            }
        }
        ib.setAllowableDrag(false);
        ib.setAllClickable(false);
        return ib.getInventory();
    }

    public static Inventory settings(Player p) {
        InventoryBuilder ib = filledBackground(new InventoryBuilder(9 * 5, "Setings"));
        ib.setItem(new ItemBuilder(Material.DIAMOND_SWORD).setName("&e&lFinal Kill Particles").build(), 22);
        ib.setClickEvent(clickInventoryEvent -> {
            p.openInventory(particleMenu(p, Particles.FinalKill));
        }, 21);
        ib.setItem(new ItemBuilder(Material.DIAMOND_SWORD).setName("&e&lBed Break Particles").build(), 22);
        ib.setClickEvent(clickInventoryEvent -> {
            p.openInventory(particleMenu(p, Particles.BedBreak));
        }, 23);
        ib.setAllowableDrag(false);
        ib.setAllClickable(false);
        return ib.getInventory();
    }

    public static Inventory allPlayers(String command) {
        return buildPlayerMenu("All Players", (clicker, target) ->
                clicker.performCommand(command.replace("%player%", target.getName()))
        );
    }

    private static InventoryBuilder filledBackground(InventoryBuilder ib) {

        for (int i = 0; i <= 36; i++) {
            ib.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setName("&c")
                    .durability(7)
                    .build());
        }
        return ib;
    }

    public static int getInventorySize(int size) {
        int rows = (int) Math.ceil(size / 9.0);
        return Math.min(rows * 9, 54);
    }

    @FunctionalInterface
    private interface KitClickAction {
        void onClick(Player p, Kit kit);
    }

    @FunctionalInterface
    private interface PlayerClickAction {
        void onClick(Player clicker, Player target);
    }
}
