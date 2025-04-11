package net.wrigglysplash.cookietils.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ScoreboardChecker {
    private static boolean warningShown = false;
    private static int ticksSinceJoin = 0;
    private static boolean inWorld = false;

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        warningShown = false;
        ticksSinceJoin = 0;
        inWorld = true;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.phase != TickEvent.Phase.END || mc.theWorld == null || mc.thePlayer == null) return;

        if (!inWorld || warningShown) return;

        ticksSinceJoin++;

        // Wait about 2 seconds, I might change this later, but idk yet. Depends on how slow the connection and joining is
        if (ticksSinceJoin >= 40) {
            Scoreboard sb = mc.theWorld.getScoreboard();
            ScoreObjective sidebar = sb.getObjectiveInDisplaySlot(1);

            if (sidebar == null) {
                mc.thePlayer.addChatMessage(new ChatComponentText("§c[CookieTils] Scoreboard is disabled. Please enable it for full functionality."));
            }

            warningShown = true;
        }
    }
}