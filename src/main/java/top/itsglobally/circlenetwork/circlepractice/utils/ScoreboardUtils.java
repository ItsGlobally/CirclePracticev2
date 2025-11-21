package top.itsglobally.circlenetwork.circlepractice.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import top.itsglobally.circlenetwork.circlepractice.data.Game;
import top.itsglobally.circlenetwork.circlepractice.data.PlayerState;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;

import java.util.*;

public class ScoreboardUtils implements GlobalInterface {
    public static final Map<UUID, List<String>> lastScoreboardLines = new HashMap<>();

    public static void updateScoreboard(Player p) {
        List<String> lines = new ArrayList<>();
        PracticePlayer pp = plugin.getPlayerManager().getPlayer(p);
        lines.add("&7&m                            &r");
        switch (pp.getState()) {
            case PlayerState.DUEL -> {
                Game game = pp.getCurrentGame();
                PracticePlayer opponent = game.getOpponent(pp);
                lines.add("&fYour opponent: &d: " + opponent.getName());
                lines.add("&d");
                if (game.getKit().isRespawnable()) {
                    lines.add("&fYour bed: " + (game.getPlayerRespawnable(pp) ? "&d✓" : "&c✗"));
                    lines.add("&fTheir bed: " + (game.getPlayerRespawnable(opponent) ? "&d✓" : "&c✗"));
                    lines.add("&d&f");
                }
                if (game.getKit().isCountHit()) {
                    lines.add("&fYour hits&d: " + game.getPlayerhit(pp) + "&f/&d" + game.getKit().getCountHitToDie());
                    lines.add("&fTheir hits&d: " + game.getPlayerhit(opponent) + "&f/&d" + game.getKit().getCountHitToDie());
                    lines.add("&d&f");
                }
                lines.add("&fYour ping&d: " + NMSUtils.getPing(p));
                lines.add("&fTheir ping&d: " + NMSUtils.getPing(opponent.getPlayer()));
                lines.add("&f");
            }
            case PlayerState.SPAWN -> {
                lines.add("&d&lServer");
                lines.add("&f* Online&d: " + Bukkit.getOnlinePlayers().size());
                lines.add("&d&lPersonal Info");
                lines.add("&f* Your ping&d: " + NMSUtils.getPing(p));
                lines.add("&f");

            }
            case PlayerState.SPECTATING -> {

            }
        }
        lines.add("&dtw4.shdctw.com:25476");
        lines.add("&7&m                            ");
        setScoreboard(p, "&dCircle Practice", MessageUtil.formatList(lines));
    }
    /*
    Circle Practice

    Your ping
     */

    public static void setScoreboard(Player player, String title, List<String> lines) {
        Scoreboard scoreboard = player.getScoreboard();

        if (scoreboard == null || scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        Objective objective = scoreboard.getObjective("ccpscoreboard");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("ccpscoreboard", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        if (!Objects.equals(objective.getDisplayName(), MessageUtil.formatMessage(title))) {
            objective.setDisplayName(MessageUtil.formatMessage(title));
        }

        List<String> lastLines = lastScoreboardLines.getOrDefault(player.getUniqueId(), Collections.emptyList());

        if (lastLines.size() != lines.size()) {
            scoreboard.getEntries().forEach(scoreboard::resetScores);
            lastLines = new ArrayList<>(Collections.nCopies(lines.size(), ""));
        }

        int score = lines.size();
        for (int i = 0; i < lines.size(); i++) {
            String newLine = lines.get(i);
            String oldLine = lastLines.size() > i+1 ? lastLines.get(i) : "";

            if (!Objects.equals(newLine, oldLine)) {
                scoreboard.resetScores(oldLine);
                objective.getScore(newLine).setScore(score);
            }

            score--;
        }

        lastScoreboardLines.put(player.getUniqueId(), new ArrayList<>(lines));
    }
}

