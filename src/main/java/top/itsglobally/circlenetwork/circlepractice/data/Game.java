package top.itsglobally.circlenetwork.circlepractice.data;

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
    private GameStete state;
    private int countdown;

    public Game(PracticePlayer player1, PracticePlayer player2, Kit kit, GameArena arena) {
        this.id = UUID.randomUUID();
        this.player1 = player1;
        this.player2 = player2;
        this.kit = kit;
        this.arena = arena;
        this.startTime = System.currentTimeMillis();
        this.spectators = new ArrayList<>();
        this.state = GameStete.STARTING;
        this.countdown = 5;
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

    public void setState(GameStete state) {
        this.state = state;
    }

    public GameStete getState() {
        return state;
    }

    public PracticePlayer getPlayer2() {
        return player2;
    }

    public int getCountdown() {
        return countdown;
    }

    public Kit getKit() {
        return kit;
    }

    public UUID getId() {
        return id;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }
}