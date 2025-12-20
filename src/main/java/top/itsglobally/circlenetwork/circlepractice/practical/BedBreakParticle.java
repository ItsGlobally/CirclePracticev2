package top.itsglobally.circlenetwork.circlepractice.practical;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

public enum BedBreakParticle {
    NONE("none", Material.BARRIER, "") {
        @Override
        public void play(Location l) {
        }
    },

    EXPLOSION("explosion", Material.REDSTONE_BLOCK, "circlepractice.BedBreak.explosion") {
        @Override
        public void play(Location l) {
            if (l == null || l.getWorld() == null) return;

            Location center = l.clone().add(0.5, 0.5, 0.5);

            center.getWorld().playSound(
                    center,
                    Sound.EXPLODE,
                    1.2f,
                    0.9f
            );

            center.getWorld().playEffect(center, Effect.EXPLOSION_LARGE, 0);

            for (int i = 0; i < 12; i++) {
                Location debris = center.clone().add(
                        randomOffset(),
                        randomOffset(),
                        randomOffset()
                );

                debris.getWorld().playEffect(
                        debris,
                        Effect.STEP_SOUND,
                        Material.BED_BLOCK
                );
            }
            for (int i = 0; i < 6; i++) {
                Location smoke = center.clone().add(
                        randomOffset(),
                        randomOffset(),
                        randomOffset()
                );

                smoke.getWorld().playEffect(smoke, Effect.SMOKE, 4);
            }
        }

        private double randomOffset() {
            return (Math.random() - 0.5) * 1.2;
        }
    };

    private final String id;
    private final Material icon;
    private final String permission;

    BedBreakParticle(String id, Material icon, String permission) {
        this.id = id;
        this.icon = icon;
        this.permission = permission;
    }

    public static BedBreakParticle fromId(String id) {
        for (BedBreakParticle p : values()) {
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
