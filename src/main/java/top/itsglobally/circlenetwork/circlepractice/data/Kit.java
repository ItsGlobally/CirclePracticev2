package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.inventory.ItemStack;

public class Kit {
    private final String name;
    private ItemStack[] contents;
    private ItemStack[] armor;
    private boolean hunger;
    private boolean enabled;
    private boolean build;
    private boolean forDuels;

    public Kit(String name) {
        this.name = name;
        this.hunger = false;
        this.enabled = false;
        this.build = false;
        this.forDuels = true;
    }

    public String getName() {
        return name;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public boolean isHunger() {
        return hunger;
    }

    public void setHunger(boolean hunger) {
        this.hunger = hunger;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isBuild() {
        return build;
    }

    public void setBuild(boolean build) {
        this.build = build;
    }
    public boolean isComplate() {
        return contents != null && armor != null;
    }

    public void setForDuels(boolean forDuels) {
        this.forDuels = forDuels;
    }

    public boolean isForDuels() {
        return forDuels;
    }
}
