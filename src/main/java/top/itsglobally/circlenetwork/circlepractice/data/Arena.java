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
    private String worldName;
    private boolean respawnableKit;
    private Location bnsb1;
    private Location bnsb2;
    private boolean remake;
    private int voidY = 0;


    public Arena(String name) {
        this.name = name;
        this.remake = false;
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

    public void addKit(String kit) {
        kits.add(kit);
    }

    public List<String> getKits() {
        return kits;
    }

    public boolean isComplete() {
        return pos1 != null && pos2 != null && spectatorSpawn != null && Bukkit.getWorld(worldName) != null;
    }

    public Location getBnsb1() {
        return bnsb1;
    }

    public void setBnsb1(Location bnsb1) {
        this.bnsb1 = bnsb1;
    }

    public Location getBnsb2() {
        return bnsb2;
    }

    public void setBnsb2(Location bnsb2) {
        this.bnsb2 = bnsb2;
    }

    public boolean isRespawnableKit() {
        return respawnableKit;
    }

    public void setRespawnableKit(boolean respawnableKit) {
        this.respawnableKit = respawnableKit;
    }

    public boolean isRemake() {
        return remake;
    }

    public void setRemake(boolean remake) {
        this.remake = remake;
    }

    public int getVoidY() {
        return voidY;
    }

    public void setVoidY(int voidY) {
        this.voidY = voidY;
    }
}
