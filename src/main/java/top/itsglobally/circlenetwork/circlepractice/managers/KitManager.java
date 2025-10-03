package top.itsglobally.circlenetwork.circlepractice.managers;

import top.itsglobally.circlenetwork.circlepractice.data.Kit;

import java.util.ArrayList;
import java.util.List;

public class KitManager extends Managers {

    private final List<Kit> kits = new ArrayList<>();

    public KitManager() {

    }

    public List<Kit> getKits() {
        return kits;
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
    public void addKit(Kit kit) {
        if (kit != null && !kits.contains(kit)) {
            kits.add(kit);
        }
    }

    public void removeKit(Kit kit) {
        kits.remove(kit);
    }
}
