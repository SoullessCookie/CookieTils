package net.wrigglysplash.cookietils.addons.skyblock.fairysouls.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.Collection;

public class FairyAreaUtils {

    public static String getCurrentArea() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.getNetHandler() == null) return "Unknown";

        Collection<NetworkPlayerInfo> infoList = mc.getNetHandler().getPlayerInfoMap();
        for (NetworkPlayerInfo info : infoList) {
            String line = getFormattedName(info);
            if (line.contains("Area: ")) {
                return line.split("Area: ")[1].trim();
            }
        }

        return "Unknown";
    }

    private static String getFormattedName(NetworkPlayerInfo info) {
        return info.getDisplayName() != null ? info.getDisplayName().getFormattedText() : "";
    }
}