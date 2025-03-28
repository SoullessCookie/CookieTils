package net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets.finders;

import net.minecraft.block.BlockLever;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wrigglysplash.cookietils.CookieTils;
import net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets.renderer.ChestRenderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LeverFinder {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<BlockPos> foundLevers = new ArrayList<>();
    private int tickCount = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (!isInDungeon()) return;
        if (!CookieTils.secretsOverlay) return;

        tickCount++;
        if (tickCount % 20 != 0) return;

        foundLevers.clear();

        for (BlockPos pos : BlockPos.getAllInBox(
                mc.thePlayer.getPosition().add(-32, -32, -32),
                mc.thePlayer.getPosition().add(32, 32, 32))) {

            if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockLever) {
                foundLevers.add(pos);
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (!isInDungeon()) return;
        if (!CookieTils.secretsOverlay) return;

        double pt = event.partialTicks;
        double dx = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * pt;
        double dy = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * pt;
        double dz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * pt;

        for (BlockPos pos : foundLevers) {
            AxisAlignedBB bb = new AxisAlignedBB(pos, pos.add(1, 1, 1)).offset(-dx, -dy, -dz);
            ChestRenderer.renderBox(bb, CookieTils.leverOverlayColor, CookieTils.leverOverlayAlpha, true, true);
        }
    }

    private int dungeonCheckCooldown = 0;
    private boolean cachedDungeonStatus = false;

    private boolean isInDungeon() {
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

            mc.thePlayer.addChatMessage(new ChatComponentText("§8> §7" + line));

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