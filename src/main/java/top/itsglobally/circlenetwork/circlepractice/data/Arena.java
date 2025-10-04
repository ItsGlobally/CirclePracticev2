package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Arena {
    private final String name;
    private final List<String> kits = new ArrayList<>();
    private Location pos1;
    private Location pos2;
    private Location spectatorSpawn;
    private final boolean inUse;
    private String worldName;

    public Arena(String name) {
        this.name = name;
        this.inUse = false;

    }

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public Location getPos1() {
        return pos1;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public Location getSpectatorSpawn() {
        return spectatorSpawn;
    }

    public void setSpectatorSpawn(Location spectatorSpawn) {
        this.spectatorSpawn = spectatorSpawn;
    }

    public void addKits(String kit) {
        kits.add(kit);
    }

    public List<String> getKits() {
        return kits;
    }

    public boolean isComplete() {
        return pos1 != null && pos2 != null && spectatorSpawn != null && Bukkit.getWorld(worldName) != null;
    }
}
