package net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets.finders;

import net.minecraft.block.BlockLever;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wrigglysplash.cookietils.CookieTils;
import net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets.renderer.ChestRenderer;

import java.util.ArrayList;
import java.util.List;

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

    private boolean isInDungeon() {
        return true; // Replace with scoreboard check later
    }
}