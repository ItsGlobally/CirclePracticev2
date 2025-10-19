package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {
    private final UUID id;
    private final PracticePlayer player1;
    private final PracticePlayer player2;
    private final Kit kit;
    private final GameArena arena;
    private final long startTime;
    private final List<UUID> spectators;
    private GameState state;
    private int countdown;
    private boolean p1respawnable;
    private boolean p2respawnable;

    public Game(PracticePlayer player1, PracticePlayer player2, Kit kit, GameArena arena) {
        this.id = UUID.randomUUID();
        this.player1 = player1;
        this.player2 = player2;
        this.kit = kit;
        this.arena = arena;
        this.startTime = System.currentTimeMillis();
        this.spectators = new ArrayList<>();
        this.state = GameState.STARTING;
        this.countdown = 5;
        this.p1respawnable = kit.isRespawnable();
        this.p2respawnable = kit.isRespawnable();
    }

    public GameArena getArena() {
        return arena;
    }

    public long getStartTime() {
        return startTime;
    }

    public List<UUID> getSpectators() {
        return spectators;
    }

    public PracticePlayer getPlayer1() {
        return player1;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public PracticePlayer getPlayer2() {
        return player2;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public Kit getKit() {
        return kit;
    }

    public UUID getId() {
        return id;
    }

    public int getPlayer1OrPlayer2(PracticePlayer player) {
        return player.equals(player1) ? 1 : 2;
    }

    public PracticePlayer getOpponent(PracticePlayer player) {
        return player.equals(player1) ? player2 : player1;
    }

    public Location getPlayerSpawnPoint(PracticePlayer pp) {
        if (getPlayer1OrPlayer2(pp) == 1) {
            return getArena().getPos1();
        } else {
            return getArena().getPos2();
        }
    }

    public void setP1respawnable(boolean p1respawnable) {
        this.p1respawnable = p1respawnable;
    }

    public void setP2respawnable(boolean p2respawnable) {
        this.p2respawnable = p2respawnable;
    }

    public boolean isP1respawnable() {
        return p1respawnable;
    }

    public boolean isP2respawnable() {
        return p2respawnable;
    }

    public boolean getPlayerRespawnable(PracticePlayer pp) {
        if (getPlayer1OrPlayer2(pp) == 1) {
            return isP1respawnable();
        } else {
            return isP2respawnable();
        }
    }

    public void setRespawnable(PracticePlayer pp, boolean status) {
        if (getPlayer1OrPlayer2(pp) == 1) {
            setP1respawnable(status);
        } else {
            setP2respawnable(status);
        }
    }

    public boolean getIsEnemysBnsb(PracticePlayer pp, Location l) {
        if (l == null || !pp.isInDuel()) return false;

        GameArena arena = getArena();

        if (arena == null) return false;

        Location enemyBedHead = (getPlayer1OrPlayer2(pp) == 1)
                ? arena.getBnsb2()
                : arena.getBnsb1();
        Location enemyBedFoot = (enemyBedHead != null) ? enemyBedHead.clone().add(1, 0, 0) : null;

        if (enemyBedHead == null) return false;
        if (!l.getWorld().equals(enemyBedHead.getWorld())) return false;

        int x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();
        boolean isHead = (x == enemyBedHead.getBlockX() && y == enemyBedHead.getBlockY() && z == enemyBedHead.getBlockZ());
        boolean isFoot = (enemyBedFoot != null && x == enemyBedFoot.getBlockX() && y == enemyBedFoot.getBlockY() && z == enemyBedFoot.getBlockZ());

        return isHead || isFoot;
    }

}