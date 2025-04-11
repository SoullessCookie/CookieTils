package net.wrigglysplash.cookietils.addons.skyblock.fairysouls.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

import java.util.Collection;

public class FairyProfileUtils {

    public static boolean isInSkyBlock() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return false;

        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);
        if (sidebar == null) return false;

        String title = sidebar.getDisplayName().replaceAll("§.", "").toUpperCase();
        return title.contains("SKYBLOCK");
    }

    public static String getCurrentProfile() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return "Unknown";

        Collection<NetworkPlayerInfo> infoList = mc.getNetHandler().getPlayerInfoMap();
        for (NetworkPlayerInfo info : infoList) {
            String line = info.getDisplayName() != null ? info.getDisplayName().getFormattedText() : "";
            if (line.contains("Profile: ")) {
                String rawProfile = line.split("Profile: ")[1].split(" ")[0].trim();
                // Fully sanitize it:
                return rawProfile.replaceAll("[^a-zA-Z0-9_-]", "");
            }
        }

        return "Unknown";
    }
}
