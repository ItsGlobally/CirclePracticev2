package top.itsglobally.circlenetwork.circlepractice.practical;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;

public enum FinalKillParticle {

    NONE("none", Material.BARRIER, "") {
        @Override
        public void play(Location l) {
        }
    },

    REDSTONE("redstone", Material.REDSTONE_BLOCK, "circlepractice.FinalKillParticle.redstone") {
        @Override
        public void play(Location l) {
            Location loc = l.add(0, 1, 0);
            loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        }
    };


    private final String id;
    private final Material icon;
    private final String permission;

    FinalKillParticle(String id, Material icon, String permission) {
        this.id = id;
        this.icon = icon;
        this.permission = permission;
    }

    public static FinalKillParticle fromId(String id) {
        for (FinalKillParticle p : values()) {
            if (p.id.equalsIgnoreCase(id)) {
                return p;
            }
        }
        return NONE;
    }

    public String getId() {
        return id;
    }

    public Material getIcon() {
        return icon;
    }

    public String getPermission() {
        return permission;
    }

    public abstract void play(Location l);
}

