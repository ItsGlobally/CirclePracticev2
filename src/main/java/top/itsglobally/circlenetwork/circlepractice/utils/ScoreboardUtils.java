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
                int redSize = game.getRedPlayers().size();
                int blueSize = game.getBluePlayers().size();

                if (redSize == 1 && blueSize == 1) {
                    PracticePlayer opponent = game.getRedPlayers().contains(pp)
                            ? game.getBluePlayers().iterator().next()
                            : game.getRedPlayers().iterator().next();
                    lines.add("&fOpponent: &d" + opponent.getName());

                    if (game.getKit().isRespawnable()) {
                        lines.add("&fYour bed: " + (game.getPlayerRespawnable(pp) ? "&d✓" : "&c✗"));
                        lines.add("&fTheir bed: " + (game.getPlayerRespawnable(opponent) ? "&d✓" : "&c✗"));
                    }

                    if (game.getKit().isCountHit()) {
                        lines.add("&fYour hits&d: " + game.getPlayerhit(pp) + "&f/&d" + game.getKit().getCountHitToDie());
                        lines.add("&fTheir hits&d: " + game.getPlayerhit(opponent) + "&f/&d" + game.getKit().getCountHitToDie());
                    }

                    lines.add("&fYour ping&d: " + NMSUtils.getPing(p));
                    lines.add("&fTheir ping&d: " + NMSUtils.getPing(opponent.getPlayer()));

                } else {
                    lines.add("&d&lRed Team:");
                    for (PracticePlayer red : game.getRedPlayers()) {
                        StringBuilder info = new StringBuilder("&f" + red.getPlayer().getName());
                        if (game.getKit().isRespawnable()) info.append(" | Bed: ").append(game.getPlayerRespawnable(red) ? "&d✓" : "&c✗");
                        if (game.getKit().isCountHit()) info.append(" | Hits: ").append(game.getPlayerhit(red));
                        info.append(" | Ping: ").append(NMSUtils.getPing(red.getPlayer()));
                        lines.add(info.toString());
                    }

                    lines.add("&d&lBlue Team:");
                    for (PracticePlayer blue : game.getBluePlayers()) {
                        StringBuilder info = new StringBuilder("&f" + blue.getPlayer().getName());
                        if (game.getKit().isRespawnable()) info.append(" | Bed: ").append(game.getPlayerRespawnable(blue) ? "&d✓" : "&c✗");
                        if (game.getKit().isCountHit()) info.append(" | Hits: ").append(game.getPlayerhit(blue));
                        info.append(" | Ping: ").append(NMSUtils.getPing(blue.getPlayer()));
                        lines.add(info.toString());
                    }
                }
            }

            case PlayerState.SPAWN, PlayerState.EDITING, PlayerState.EDITINGGLOBALLY, PlayerState.QUEUE -> {
                lines.add("&d&lServer");
                lines.add("&f» Online&d: " + Bukkit.getOnlinePlayers().size());
                lines.add("&d&lPersonal Info");
                lines.add("&f» Your ping&d: " + NMSUtils.getPing(p));
            }

            case PlayerState.SPECTATING -> {
                Game game = pp.getCurrentGame();
                lines.add("&d&lRed Team:");
                for (PracticePlayer red : game.getRedPlayers()) {
                    StringBuilder info = new StringBuilder("&f" + red.getPlayer().getName());
                    if (game.getKit().isRespawnable()) info.append(" | Bed: ").append(game.getPlayerRespawnable(red) ? "&d✓" : "&c✗");
                    if (game.getKit().isCountHit()) info.append(" | Hits: ").append(game.getPlayerhit(red));
                    info.append(" | Ping: ").append(NMSUtils.getPing(red.getPlayer()));
                    lines.add(info.toString());
                }
                lines.add("&d&lBlue Team:");
                for (PracticePlayer blue : game.getBluePlayers()) {
                    StringBuilder info = new StringBuilder("&f" + blue.getPlayer().getName());
                    if (game.getKit().isRespawnable()) info.append(" | Bed: ").append(game.getPlayerRespawnable(blue) ? "&d✓" : "&c✗");
                    if (game.getKit().isCountHit()) info.append(" | Hits: ").append(game.getPlayerhit(blue));
                    info.append(" | Ping: ").append(NMSUtils.getPing(blue.getPlayer()));
                    lines.add(info.toString());
                }
            }
        }

        lines.add("&dtw4.shdctw.com:25476");
        lines.add("&7&m                            ");

        setScoreboard(p, "&dCircle Practice", MessageUtil.formatList(lines));
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

