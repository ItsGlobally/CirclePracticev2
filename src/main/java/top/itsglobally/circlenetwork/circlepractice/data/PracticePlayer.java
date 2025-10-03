package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PracticePlayer {
    private final UUID uuid;
    private final String name;
    private final Player player;
    private PlayerState state;
    private Game currentGame = null;



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

    public UUID getUuid() {
        return uuid;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }
}
