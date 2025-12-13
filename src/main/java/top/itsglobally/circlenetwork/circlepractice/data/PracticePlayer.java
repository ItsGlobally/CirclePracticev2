package top.itsglobally.circlenetwork.circlepractice.data;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.itsglobally.circlenetwork.circlepractice.achievement.Achievement;
import top.itsglobally.circlenetwork.circlepractice.managers.PlayerDataManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class PracticePlayer implements GlobalInterface{
    private final UUID uuid;
    private final String name;
    private final Player player;
    private final Set<Achievement> unlocked = new HashSet<>();
    private final PlayerDataManager.PlayerData playerData;
    private PlayerState state;
    private Game currentGame = null;
    private String queuedKit;
    private ItemStack[] armor;
    private ItemStack[] inventory;
    private boolean playedFirst;

    public PracticePlayer(Player p) {
        this.uuid = p.getUniqueId();
        this.name = p.getName();
        this.player = p;
        this.state = PlayerState.SPAWN;
        this.armor = new ItemStack[4];
        this.inventory = new ItemStack[36];
        this.playedFirst = false;
        this.playerData = plugin.getPlayerDataManager().getData(p);
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
        return state == PlayerState.SPAWN || state == PlayerState.EDITING || state == PlayerState.QUEUE;
    }

    public boolean isInSpawnNotEditingOrQueuing() {
        return state == PlayerState.SPAWN;
    }
    public boolean isQueuing() {
        return state == PlayerState.QUEUE;
    }
    public boolean isInSpawnOrEditingNotQueuing() {
        return state == PlayerState.SPAWN || state == PlayerState.EDITING;
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

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public boolean isPlayedFirst() {
        return playedFirst;
    }

    public void setPlayedFirst(boolean playedFirst) {
        this.playedFirst = playedFirst;
    }

    public PlayerDataManager.PlayerData getPlayerData() {
        return playerData;
    }
}
