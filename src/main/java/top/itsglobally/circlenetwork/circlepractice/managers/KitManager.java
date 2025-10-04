package top.itsglobally.circlenetwork.circlepractice.managers;

import org.bukkit.inventory.ItemStack;
import top.itsglobally.circlenetwork.circlepractice.data.Kit;

import java.util.ArrayList;
import java.util.List;

public class KitManager extends Managers {

    private final List<Kit> kits = new ArrayList<>();

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

    public Kit createKit(String name) {
        return new Kit(name);
    }

    public void updateKit(String name, ItemStack[] contents, ItemStack[] armor) {
        kits.removeIf(k -> k.getName().equalsIgnoreCase(name));

        Kit newKit = new Kit(name);
        newKit.setContents(contents);
        newKit.setArmor(armor);
        kits.add(newKit);
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
}
