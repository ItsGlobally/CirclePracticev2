package top.itsglobally.circlenetwork.circlepractice.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.itsglobally.circlenetwork.circlepractice.achievement.Achievement;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PracticePlayer {
    private final UUID uuid;
    private final String name;
    private final Player player;
    private PlayerState state;
    private Game currentGame = null;
    private String queuedKit;
    private ItemStack[] armor;
    private ItemStack[] inventory;
    private final Set<Achievement> unlocked = new HashSet<>();
    private boolean playedFirst;

    public PracticePlayer(Player p) {
        this.uuid = p.getUniqueId();
        this.name = p.getName();
        this.player = p;
        this.state = PlayerState.SPAWN;
        this.armor = new ItemStack[4];
        this.inventory = new ItemStack[36];
        this.playedFirst = false;
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

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public boolean hasAchievement(Achievement a) {
        return unlocked.contains(a);
    }

    public void unlockAchievement(Achievement a) {
        if (hasAchievement(a)) return;
        unlocked.add(a);
        Component c = Component.text(MessageUtil.formatMessage("&e&ke&a>> " + "Achievement Unlocked:" + "&6" + a.getTitle() + "&a<<&e&ke"))
                .hoverEvent(HoverEvent.showText(Component.text(MessageUtil.formatMessage("&a" + a.getDescription()))));
        MessageUtil.sendMessage(player, c);
    }

    public Set<Achievement> getUnlockedAchievement() {
        return unlocked;
    }

    public boolean isPlayedFirst() {
        return playedFirst;
    }

    public void setPlayedFirst(boolean playedFirst) {
        this.playedFirst = playedFirst;
    }
}
