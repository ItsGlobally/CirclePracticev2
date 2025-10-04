package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class GameArena {
    private final String name;
    private Kit kit;
    private Location pos1;
    private Location pos2;
    private Location spectatorSpawn;
    private boolean inUse;
    private String worldName;

    public GameArena(String name, String worldName) {
        this.name = name;
        this.inUse = false;
        this.worldName = worldName;
    }
    public void convertFromArena(Arena a, Kit kit) {
        setPos1(a.getPos1());
        setPos2(a.getPos2());
        setSpectatorSpawn(a.getSpectatorSpawn());
        this.kit = kit;
    }
    public String getName() {
        return name;
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

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public Kit getKit() {
        return kit;
    }
}
