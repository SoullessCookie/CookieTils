package net.wrigglysplash.cookietils.addons.skyblock.dungeons.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

import java.util.HashSet;
import java.util.Set;

public class DungeonUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static int dungeonCheckCooldown = 0;
    private static boolean cachedDungeonStatus = false;

    public static boolean isInDungeon() {
        if (dungeonCheckCooldown > 0) {
            dungeonCheckCooldown--;
            return cachedDungeonStatus;
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

            if (line.contains("catacombs") || line.contains("(f")) {
                cachedDungeonStatus = true;
                dungeonCheckCooldown = 20;
                return true;
            }
        }

        cachedDungeonStatus = false;
        dungeonCheckCooldown = 20;
        return false;
    }
}
