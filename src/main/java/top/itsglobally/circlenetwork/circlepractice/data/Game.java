package top.itsglobally.circlenetwork.circlepractice.data;

import java.util.List;
import java.util.UUID;

public class Duel {
    private final UUID id;
    private final PracticePlayer player1;
    private final PracticePlayer player2;
    private final String kit;
    private final Arena arena;
    private final long startTime;
    private final List<UUID> spectators;
    private DuelState state;
    private int countdown;

    public Duel(PracticePlayer player1, PracticePlayer player2, String kit, Arena arena) {
        this.id = UUID.randomUUID();
        this.player1 = player1;
        this.player2 = player2;
        this.kit = kit;
        this.arena = arena;
        this.startTime = System.currentTimeMillis();
        this.spectators = new ArrayList<>();
        this.state = DuelState.STARTING;
        this.countdown = 5;
    }
}
