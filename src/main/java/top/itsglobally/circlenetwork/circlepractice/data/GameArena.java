package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.Bukkit;
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
    private Arena orgArena;
    private boolean remake;

    public GameArena(String name, String worldName) {
        this.name = name;
        this.inUse = false;
        this.worldName = worldName;
        this.remake = false;
    }

    public void convertFromArena(Arena a) {
        kits.addAll(a.getKits());
        Location l1n = new Location(Bukkit.getWorld(worldName), a.getPos1().getX(), a.getPos1().getY(), a.getPos1().getZ(), a.getPos1().getYaw(), a.getPos1().getPitch());
        Location l2n = new Location(Bukkit.getWorld(worldName), a.getPos2().getX(), a.getPos2().getY(), a.getPos2().getZ(), a.getPos2().getYaw(), a.getPos2().getPitch());
        setPos1(l1n);
        setPos2(l2n);
        setSpectatorSpawn(a.getSpectatorSpawn());
        setRespawnableKit(a.isRespawnableKit());
        if (a.isRespawnableKit()) {
            setBnsb1(a.getBnsb1());
            setBnsb2(a.getBnsb2());
        }
        this.orgArena = a;
    }

    public String getName() {
        return name;
    }

    public Location getPos1() {
        return pos1;
    }

    public String getWorldName() {
        return worldName;
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

    public Arena getOrgArena() {
        return orgArena;
    }

    public boolean isRemake() {
        return remake;
    }

    public void setRemake(boolean remake) {
        this.remake = remake;
    }
}
