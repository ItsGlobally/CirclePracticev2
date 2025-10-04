package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PracticePlayer {
    private final UUID uuid;
    private final String name;
    private final Player player;
    private PlayerState state;
    private Game currentGame = null;
    private String queuedKit;


    public PracticePlayer(Player p) {
        this.uuid = p.getUniqueId();
        this.name = p.getName();
        this.player = p;
        this.state = PlayerState.SPAWN;
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    public boolean isInDuel() {
        return state == PlayerState.DUEL;
    }

    public boolean isInFFA() {
        return state == PlayerState.FFA;
    }

    public boolean isSpectating() {
        return state == PlayerState.SPECTATING;
    }

    public boolean isInSpawn() {
        return state == PlayerState.SPAWN || state == PlayerState.EDITING;
    }
    public boolean isInSpawnNotEditing() {
        return state == PlayerState.SPAWN;
    }
    public boolean isEditing() {
        return state == PlayerState.EDITING || state == PlayerState.EDITINGGLOBALLY;
    }

    public String getQueuedKit() {
        return queuedKit;
    }

    public void setQueuedKit(String queuedKit) {
        this.queuedKit = queuedKit;
    }
}
