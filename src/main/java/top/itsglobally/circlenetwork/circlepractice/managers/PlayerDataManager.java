package top.itsglobally.circlenetwork.circlepractice.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.itsglobally.circlenetwork.circlepractice.achievement.Achievement;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.practical.BedBreak.BedBreakParticle;
import top.itsglobally.circlenetwork.circlepractice.practical.FinalKill.FinalKillParticle;
import top.itsglobally.circlenetwork.circlepractice.utils.ConfigRegister;
import top.itsglobally.circlenetwork.circlepractice.utils.MessageUtil;
import top.itsglobally.circlenetwork.circlepractice.utils.serializer;
import top.nontage.nontagelib.config.BaseConfig;

import java.util.*;

public class PlayerDataManager implements GlobalInterface {

    private final PlayerDatas playerDatas;
    public Map<UUID, PlayerData> apds = new LinkedHashMap<>();

    public PlayerDataManager() {
        this.playerDatas = ConfigRegister.register(new PlayerDatas(), "playerdatas");
        reload();
    }

    public PlayerData getData(Player player) {
        return apds.computeIfAbsent(player.getUniqueId(), k -> new PlayerData(player.getUniqueId()));
    }

    public void saveAll() {
        playerDatas.datas.clear();
        for (Map.Entry<UUID, PlayerData> e : apds.entrySet()) {
            playerDatas.datas.put(e.getKey(), serializer.serializePlayerData(e.getValue()));
        }
        playerDatas.save();
    }

    public void reload() {
        playerDatas.reload();
        if (playerDatas.datas == null) playerDatas.datas = new LinkedHashMap<>();

        apds.clear();

        for (Map.Entry<?, ?> e : playerDatas.datas.entrySet()) {
            UUID uuid;
            if (e.getKey() instanceof UUID u) {
                uuid = u;
            } else {
                uuid = UUID.fromString(e.getKey().toString());
            }

            Map<String, Object> dataMap = (Map<String, Object>) e.getValue();
            apds.put(uuid, serializer.deserializePlayerData(dataMap));
        }
    }

    public void remove(Player player) {
        apds.remove(player.getUniqueId());
    }

    public Map<UUID, PlayerData> getAllData() {
        return apds;
    }

    public static class PlayerData {
        private final Map<String, ItemStack[][]> kitContents = new HashMap<>();
        private final List<Achievement> aa = new ArrayList<>();
        private final UUID uuid;
        private long stars = 1;
        private long xps = 0;
        private String finalKillParticle = "none";
        private String bedBreakParticle = "none";



        public PlayerData(UUID uuid) {
            this.uuid = uuid;
        }

        public UUID getUuid() {
            return uuid;
        }

        public long getStars() {
            return stars;
        }

        public void setStars(long stars) {
            this.stars = stars;
        }

        public long getXps() {
            return xps;
        }

        public void setXps(long xps) {
            if (xps < 0) xps = 0;
            processXp(xps);
        }

        public void addXps(long x) {
            if (x <= 0) return;
            processXp(this.xps + x);
        }

        public void addStars(long stars) {
            this.stars += stars;
        }

        private void processXp(long totalXp) {
            if (totalXp >= 100) {
                long gainedStars = totalXp / 100;
                long remainingXp = totalXp % 100;
                addStars(gainedStars);
                this.xps = remainingXp;

            } else {
                this.xps = totalXp;
            }
        }
        public FinalKillParticle getFinalKillParticle() {
            return FinalKillParticle.fromId(finalKillParticle);
        }
        public void setFinalKillParticle(FinalKillParticle p) {
            this.finalKillParticle = p.getId();
        }
        public BedBreakParticle getBedBreakParticle() {
            return BedBreakParticle.fromId(bedBreakParticle);
        }
        public void setBedBreakParticle(BedBreakParticle p) {
            this.bedBreakParticle = p.getId();
        }


        public void setKitContents(String name, ItemStack[][] contents) {
            kitContents.put(name.toLowerCase(), contents);
        }

        public ItemStack[][] getKitContents(String name) { //0: contents 1: armors
            return kitContents.getOrDefault(name.toLowerCase(), new ItemStack[][]{plugin.getKitManager().getKit(name).getContents(), plugin.getKitManager().getKit(name).getArmor()});
        }

        public Map<String, ItemStack[][]> getAllKits() {
            return kitContents;
        }

        public boolean hasAchievement(Achievement a) {
            return aa.contains(a);
        }

        public void unlockAchievement(Achievement a) {
            unlockAchievement(a, true);
        }

        public void unlockAchievement(Achievement a, boolean send) {
            if (hasAchievement(a)) return;
            aa.add(a);
            if (send) {
                Component c = Component.text(MessageUtil.formatMessage("&e&ke&a>> " + "Achievement Unlocked: " + "&6" + Achievement.JOIN.getTitle() + "&a<<&e&ke"))
                        .hoverEvent(HoverEvent.showText(Component.text(MessageUtil.formatMessage("&a" + Achievement.JOIN.getDescription()))));
                MessageUtil.sendMessage(Bukkit.getPlayer(uuid), c);
            }
        }

        public List<Achievement> getUnlockedAchievement() {
            return aa;
        }

    }

    public static class PlayerDatas extends BaseConfig {
        public Map<UUID, Map<String, Object>> datas = new LinkedHashMap<>();
    }
}
