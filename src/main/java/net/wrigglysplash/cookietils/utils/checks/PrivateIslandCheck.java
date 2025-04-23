package net.wrigglysplash.cookietils.utils.checks;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

import java.util.HashSet;
import java.util.Set;

public class PrivateIslandCheck {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static int privateIslandCheckCooldown = 0;
    private static boolean cachedPrivateIslandStatus = false;

    public static boolean isInGarden() {
        if (privateIslandCheckCooldown > 0) {
            privateIslandCheckCooldown--;
            return cachedPrivateIslandStatus;
        }

        if (mc.theWorld == null || mc.thePlayer == null) return false;

        Scoreboard sb = mc.theWorld.getScoreboard();
        ScoreObjective sidebar = sb.getObjectiveInDisplaySlot(1);
        if (sidebar == null) return false;

        Set<String> seenLines = new HashSet<>();

        for (Score score : sb.getSortedScores(sidebar)) {
            if (score == null || score.getPlayerName() == null) continue;

            String line = sb.getPlayersTeam(score.getPlayerName()) != null
                    ? sb.getPlayersTeam(score.getPlayerName()).formatString(score.getPlayerName())
                    : score.getPlayerName();

            line = line.replaceAll("§.", "").toLowerCase().trim();
            if (line.isEmpty() || seenLines.contains(line)) continue;
            seenLines.add(line);

            if (line.contains("your island")) {
                cachedPrivateIslandStatus = true;
                privateIslandCheckCooldown = 20;
                return true;
            }
        }

        cachedPrivateIslandStatus = false;
        privateIslandCheckCooldown = 20;
        return false;
    }
}
