package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class GameArena {
    private final String name;
    private final List<String> kits = new ArrayList<>();
    private Location pos1;
    private Location pos2;
    private Location spectatorSpawn;
    private boolean inUse;
    private final String worldName;
    private Location bnsb1;
    private Location bnsb2;
    private boolean respawnableKit;

    public GameArena(String name, String worldName) {
        this.name = name;
        this.inUse = false;
        this.worldName = worldName;
    }

    public void convertFromArena(Arena a) {

        kits.addAll(a.getKits());
        setPos1(a.getPos1());
        setPos2(a.getPos2());
        setSpectatorSpawn(a.getSpectatorSpawn());
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

    public void addKit(String kit) {
        kits.add(kit);
    }

    public List<String> getKits() {
        return kits;
    }

    public Location getBnsb1() {
        return bnsb1;
    }

    public Location getBnsb2() {
        return bnsb2;
    }

    public void setBnsb1(Location bnsb1) {
        this.bnsb1 = bnsb1;
    }

    public void setBnsb2(Location bnsb2) {
        this.bnsb2 = bnsb2;
    }

    public void setRespawnableKit(boolean respawnableKit) {
        this.respawnableKit = respawnableKit;
    }

    public boolean isRespawnableKit() {
        return respawnableKit;
    }

}
