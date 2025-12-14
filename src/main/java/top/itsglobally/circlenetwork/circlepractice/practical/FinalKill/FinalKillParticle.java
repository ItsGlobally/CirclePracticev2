package top.itsglobally.circlenetwork.circlepractice.practical.FinalKill;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum FinalKillParticle {

    NONE("none", Material.BARRIER) {
        @Override
        public void play(Location l) {}
    },

    REDSTONE("redstone", Material.REDSTONE_BLOCK) {
        @Override
        public void play(Location l) {
            Location loc = l.add(0, 1, 0);
            loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        }
    };


    private final String id;
    private final Material icon;
    FinalKillParticle(String id, Material icon) {
        this.id = id;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public Material getIcon() {
        return icon;
    }

    public abstract void play(Location l);

    public static FinalKillParticle fromId(String id) {
        for (FinalKillParticle p : values()) {
            if (p.id.equalsIgnoreCase(id)) {
                return p;
            }
        }
        return REDSTONE;
    }
}

