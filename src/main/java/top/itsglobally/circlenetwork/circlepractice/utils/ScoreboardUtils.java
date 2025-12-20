package top.itsglobally.circlenetwork.circlepractice.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import top.itsglobally.circlenetwork.circlepractice.data.Game;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;
import top.itsglobally.circlenetwork.circlepractice.data.PlayerState;
import top.itsglobally.circlenetwork.circlepractice.data.PracticePlayer;

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
                if (game == null) break;

                buildDuelLikeLines(lines, pp, game, false);
            }

            case PlayerState.EDITING, PlayerState.EDITINGGLOBALLY, PlayerState.QUEUE, PlayerState.SPAWN -> {
                lines.add("&d&lServer");
                lines.add("&f» Online&d: " + Bukkit.getOnlinePlayers().size());
                lines.add("&d&lPersonal Info");
                lines.add("&f» Your ping&d: " + NMSUtils.getPing(p));
                lines.add("&f");

            }
            case PlayerState.SPECTATING -> {
                Game game = pp.getCurrentGame();
                if (game == null) break;

                buildDuelLikeLines(lines, pp, game, true);
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

    private static void buildDuelLikeLines(
            List<String> lines,
            PracticePlayer viewer,
            Game game,
            boolean spectating
    ) {
        PracticePlayer p1 = game.getPlayer1();
        PracticePlayer p2 = game.getPlayer2();

        if (spectating) {
            lines.add("&d&lMatch");
            lines.add("&c" + p1.getName() + " &fvs &9" + p2.getName());
            lines.add("&f");
        }

        if (game.getKit().isRespawnable()) {
            lines.add("&cRed's &fbed&d: " +
                    (game.getPlayerRespawnable(p1) ? "&d✓" : "&c✗"));
            lines.add("&9Blue's &fbed&d: " +
                    (game.getPlayerRespawnable(p2) ? "&d✓" : "&c✗"));
            lines.add("&f");
        }

        if (game.getKit().isCountHit()) {
            lines.add("&cRed's &fhits&d: &f" +
                    game.getPlayerhit(p1) +
                    "&7/&d" + game.getKit().getCountHitToDie());

            lines.add("&9Blue's &fhits&d: &f" +
                    game.getPlayerhit(p2) +
                    "&7/&d" + game.getKit().getCountHitToDie());
            lines.add("&f");
        }

        lines.add("&dRed's &fping&d: &f" +
                NMSUtils.getPing(p1.getPlayer()) + "ms");
        lines.add("&dBlue's &fping&d: &f" +
                NMSUtils.getPing(p2.getPlayer()) + "ms");
        lines.add("&f");
    }


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
            String oldLine = lastLines.size() > i + 1 ? lastLines.get(i) : "";

            if (!Objects.equals(newLine, oldLine)) {
                scoreboard.resetScores(oldLine);
                objective.getScore(newLine).setScore(score);
            }

            score--;
        }

        lastScoreboardLines.put(player.getUniqueId(), new ArrayList<>(lines));
    }
}

