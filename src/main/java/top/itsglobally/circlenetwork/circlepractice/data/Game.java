package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.Location;
import top.itsglobally.circlenetwork.circlepractice.handlers.GameHandler;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;

import java.util.*;

public class Game implements GlobalInterface{

    public final HashMap<UUID, Boolean> respawning, gotHitted, attackable, respawnable;
    private final UUID id;
    private final HashMap<UUID, PracticePlayer> red, blue, ored, oblue, lasthit;
    private final Kit kit;
    private final GameArena arena;
    private final long startTime;
    private final List<UUID> spectators;
    private final GameHandler handler;
    private GameState state;
    private int countdown;
    public final HashMap<UUID, Integer> hit;


    public Game(HashMap<UUID, PracticePlayer> red, HashMap<UUID, PracticePlayer> blue, Kit kit, GameArena arena) {
        this.id = UUID.randomUUID();
        this.red = red;
        this.blue = blue;
        this.ored = red;
        this.oblue = blue;
        this.kit = kit;
        this.arena = arena;
        this.startTime = System.currentTimeMillis();
        this.spectators = new ArrayList<>();
        this.state = GameState.STARTING;
        this.countdown = 5;
        this.attackable = new HashMap<>();
        this.respawnable = new HashMap<>();
        this.lasthit = new HashMap<>();
        this.hit = new HashMap<>();
        this.respawning = new HashMap<>();
        this.gotHitted = new HashMap<>();
        this.handler = new GameHandler(this);
        for (PracticePlayer pp : red.values()) {
            attackable.put(pp.getUuid(), true);
        }
        for (PracticePlayer pp : blue.values()) {
            attackable.put(pp.getUuid(), true);
        }
    }

    public HashMap<UUID, PracticePlayer> getLastHit() {
        return lasthit;
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

    public void addSpectator(UUID u) {
        spectators.add(u);
    }

    public void removeSpectator(UUID u) {
        spectators.remove(u);
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
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

    public HashMap<UUID, PracticePlayer> getRed() {
        return red;
    }

    public HashMap<UUID, PracticePlayer> getBlue() {
        return blue;
    }

    public List<PracticePlayer> getAllPlayers() {
        List<PracticePlayer> all = new ArrayList<>();
        all.addAll(red.values());
        all.addAll(blue.values());
        return all;
    }

    public void broadcast(String m) {
        for (PracticePlayer pp : getAllPlayers()) {
            MessageUtil.sendMessage(pp.getPlayer(), m);
        }
    }

    public String getPrefixedTeamPlayerName(PracticePlayer pp) {
        if (red.containsKey(pp.getUuid())) {
            return "&c" + pp.getPlayer().getName();
        }
        return "&9" + pp.getPlayer().getName();
    }

    public boolean isPlayerAttackable(PracticePlayer pp) {
        return attackable.getOrDefault(pp.getUuid(), true);
    }

    public void setPlayerAttackable(PracticePlayer pp, boolean s) {
        attackable.put(pp.getUuid(), s);
    }

    public void addPlayerhit(PracticePlayer pp, int add) {
        hit.put(pp.getUuid(), hit.getOrDefault(pp.getUuid(), 0) + 1);
    }

    public void setPlayerhit(PracticePlayer pp, int set) {
        hit.put(pp.getUuid(), set);
    }

    public int getPlayerhit(PracticePlayer pp) {
        return hit.getOrDefault(pp.getUuid(), 0);
    }

    public Location getPlayerSpawnPoint(PracticePlayer pp) {
        if (red.containsKey(pp.getUuid())) {
            return getArena().getPos1();
        } else {
            return getArena().getPos2();
        }
    }
    public boolean getPlayerRespawnable(PracticePlayer pp) {
        return respawnable.getOrDefault(pp.getUuid(), kit.isRespawnable());
    }

    public void setRespawnable(PracticePlayer pp, boolean status) {
        respawnable.put(pp.getUuid(), status);
    }

    public boolean isNear(Location loc1, Location loc2, int radius) {
        return Math.abs(loc1.getBlockX() - loc2.getBlockX()) <= radius &&
                Math.abs(loc1.getBlockY() - loc2.getBlockY()) <= radius &&
                Math.abs(loc1.getBlockZ() - loc2.getBlockZ()) <= radius;
    }
    private boolean isBedNear(Location bedBase, Location loc) {
        Location head = bedBase.clone();
        Location footX = bedBase.clone().add(1, 0, 0);
        Location footZ = bedBase.clone().add(0, 0, 1);

        return isNear(loc, head, 1)
                || isNear(loc, footX, 1)
                || isNear(loc, footZ, 1);
    }

    public boolean getIsEnemyBed(PracticePlayer pp, Location loc) {
        Location enemyBed;

        if (red.containsKey(pp.getUuid())) {
            enemyBed = arena.getBnsb2();
        } else if (blue.containsKey(pp.getUuid())) {
            enemyBed = arena.getBnsb1();
        } else {
            return false;
        }

        return isBedNear(enemyBed, loc);
    }

    public boolean getIsOwnBed(PracticePlayer pp, Location loc) {
        Location ownBed;

        if (red.containsKey(pp.getUuid())) {
            ownBed = arena.getBnsb1();
        } else if (blue.containsKey(pp.getUuid())) {
            ownBed = arena.getBnsb2();
        } else {
            return false;
        }

        return isBedNear(ownBed, loc);
    }


    public GameHandler getHandler() {
        return handler;
    }

    public HashMap<UUID, PracticePlayer> getLasthit() {
        return lasthit;
    }

    public boolean isRed(PracticePlayer pp) {
        return red.containsKey(pp.getUuid());
    }

    public boolean isBlue(PracticePlayer pp) {
        return blue.containsKey(pp.getUuid());
    }

    public Collection<PracticePlayer> getEnemyTeam(PracticePlayer pp) {
        if (isRed(pp)) {
            return blue.values();
        }
        if (isBlue(pp)) {
            return red.values();
        }
        return List.of();
    }

    public Collection<PracticePlayer> getOwnTeam(PracticePlayer pp) {
        if (isRed(pp)) {
            return red.values();
        }
        if (isBlue(pp)) {
            return blue.values();
        }
        return List.of();
    }

    public void removePlayer(PracticePlayer pp) {
        if (isRed(pp)) {
            if (red.size() - 1 == 0) {
                plugin.getGameManager().endGame(this, getBluePlayers());
                return;
            }
            red.remove(pp.getUuid());
            return;
        }
        if (blue.size() - 1 == 0) {
            plugin.getGameManager().endGame(this, getRedPlayers());
            return;
        }
        blue.remove(pp.getUuid());
    }
    public Collection<PracticePlayer> getRedPlayers() {
        return red.values();
    }
    public Collection<PracticePlayer> getBluePlayers() {
        return blue.values();
    }

    public HashMap<UUID, PracticePlayer> getOringnalBlue() {
        return oblue;
    }

    public HashMap<UUID, PracticePlayer> getOringnalRed() {
        return ored;
    }
}