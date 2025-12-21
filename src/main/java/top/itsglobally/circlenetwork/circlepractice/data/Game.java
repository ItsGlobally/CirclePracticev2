package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.Location;
import top.itsglobally.circlenetwork.circlepractice.handlers.GameHandler;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;

import java.util.ArrayList;
import java.util.HashMap;
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

    private boolean p1respawnable, p2respawnable;
    private boolean p1attackable, p2attackable;
    private int p1hit, p2hit;
    public final HashMap<UUID, Boolean> respawning;
    public final HashMap<UUID, Boolean> gotHitted;

    private final GameHandler handler;


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
        this.p1attackable = true;
        this.p2attackable = true;
        this.p1hit = 0;
        this.p2hit = 0;
        this.respawning = new HashMap<>();
        this.gotHitted = new HashMap<>();
        this.handler = new GameHandler(this);
    }

    public GameArena getArena() { return arena; }
    public long getStartTime() { return startTime; }
    public List<UUID> getSpectators() { return spectators; }
    public void addSpectator(UUID u) { spectators.add(u); }
    public void removeSpectator(UUID u) { spectators.remove(u); }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public int getCountdown() { return countdown; }
    public void setCountdown(int countdown) { this.countdown = countdown; }
    public Kit getKit() { return kit; }
    public UUID getId() { return id; }

    public PracticePlayer getPlayer1() { return player1; }
    public PracticePlayer getPlayer2() { return player2; }
    public int getPlayer1OrPlayer2(PracticePlayer player) {
        if (player.equals(player1)) return 1;
        if (player.equals(player2)) return 2;
        return -1;
    }
    public PracticePlayer getOpponent(PracticePlayer player) {
        if (player.equals(player1)) return player2;
        if (player.equals(player2)) return player1;
        return null;
    }
    public List<PracticePlayer> getAllPlayers() {
        List<PracticePlayer> all = new ArrayList<>();
        if (player1 != null) all.add(player1);
        if (player2 != null) all.add(player2);
        return all;
    }

    public void broadcast(String m) {
        for (PracticePlayer pp : getAllPlayers()) {
            MessageUtil.sendMessage(pp.getPlayer(), m);
        }
    }
    public String getPrefixedTeamPlayerName(PracticePlayer pp) {
        if (getPlayer1OrPlayer2(pp) == 1) {
            return "&c" + pp.getPlayer().getName();
        }
        return "&9" + pp.getPlayer().getName();
    }

    public boolean isP1attackable() {
        return p1attackable;
    }

    public void setP1attackable(boolean p1attackable) {
        this.p1attackable = p1attackable;
    }

    public boolean isP2attackable() {
        return p2attackable;
    }

    public void setP2attackable(boolean p2attackable) {
        this.p2attackable = p2attackable;
    }

    public boolean isPlayerAttackable(PracticePlayer pp) {
        if (getPlayer1OrPlayer2(pp) == 1) return isP1attackable();
        return isP2attackable();
    }

    public void setPlayerAttackable(PracticePlayer pp, boolean s) {
        if (getPlayer1OrPlayer2(pp) == 1) setP1attackable(s);
        else setP2attackable(s);
    }

    public void addP1hit(int p1hit) {
        this.p1hit = this.p1hit + p1hit;
    }

    public void addP2hit(int p2hit) {
        this.p2hit = this.p2hit + p2hit;
    }

    public void setPlayerhit(PracticePlayer pp, int hit) {
        if (getPlayer1OrPlayer2(pp) == 1) setP1hit(hit);
        else setP2hit(hit);
    }

    public void addPlayerhit(PracticePlayer pp, int hit) {
        if (getPlayer1OrPlayer2(pp) == 1) addP1hit(hit);
        else addP2hit(hit);
    }

    public int getP1hit() {
        return p1hit;
    }

    public void setP1hit(int p1hit) {
        this.p1hit = p1hit;
    }

    public int getP2hit() {
        return p2hit;
    }

    public void setP2hit(int p2hit) {
        this.p2hit = p2hit;
    }

    public int getPlayerhit(PracticePlayer pp) {
        if (getPlayer1OrPlayer2(pp) == 1) return getP1hit();
        return getP2hit();
    }
    public Location getPlayerSpawnPoint(PracticePlayer pp) {
        if (getPlayer1OrPlayer2(pp) == 1) {
            return getArena().getPos1();
        } else {
            return getArena().getPos2();
        }
    }

    public boolean isP1respawnable() {
        return p1respawnable;
    }

    public void setP1respawnable(boolean p1respawnable) {
        this.p1respawnable = p1respawnable;
    }

    public boolean isP2respawnable() {
        return p2respawnable;
    }

    public void setP2respawnable(boolean p2respawnable) {
        this.p2respawnable = p2respawnable;
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

    public boolean isNear(Location loc1, Location loc2, int radius) {
        return Math.abs(loc1.getBlockX() - loc2.getBlockX()) <= radius &&
                Math.abs(loc1.getBlockY() - loc2.getBlockY()) <= radius &&
                Math.abs(loc1.getBlockZ() - loc2.getBlockZ()) <= radius;
    }

    public boolean getIsEnemyBed(PracticePlayer pp, Location loc) {
        Location enemyBed = (getPlayer1OrPlayer2(pp) == 1) ? getArena().getBnsb2() : getArena().getBnsb1();
        Location enemyBedHead = enemyBed.clone();
        Location enemyBedFoot = enemyBed.clone().add(1, 0, 0);
        return isNear(loc, enemyBedHead, 1) || isNear(loc, enemyBedFoot, 1);
    }

    public boolean getIsOwnBed(PracticePlayer pp, Location loc) {
        Location ownBed = (getPlayer1OrPlayer2(pp) == 1) ? getArena().getBnsb1() : getArena().getBnsb2();
        Location ownBedHead = ownBed.clone();
        Location ownBedFoot = ownBed.clone().add(1, 0, 0);
        return isNear(loc, ownBedHead, 1) || isNear(loc, ownBedFoot, 1);
    }

    public GameHandler getHandler() {
        return handler;
    }
}