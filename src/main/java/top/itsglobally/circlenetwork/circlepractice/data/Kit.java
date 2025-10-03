package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Kit {
    private final String name;
    private ItemStack[] contents;
    private ItemStack[] armor;

    public Kit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }
}
