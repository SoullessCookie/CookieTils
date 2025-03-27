package net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wrigglysplash.cookietils.CookieTils;
import net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets.renderer.ChestRenderer;

import java.util.ArrayList;
import java.util.List;

public class ChestFinder {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<BlockPos> foundChests = new ArrayList<>();
    private int tickCount = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (!isInDungeon()) return;
        if (!CookieTils.secretsOverlay) return;

        tickCount++;
        if (tickCount % 20 != 0) return; // Scan every 20 ticks (1 second)

        foundChests.clear();
        for (TileEntity tile : mc.theWorld.loadedTileEntityList) {
            if (tile instanceof TileEntityChest) {
                foundChests.add(tile.getPos());
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null || !isInDungeon()) return;
        // 💡 This MUST be here to respect live config changes
        if (!CookieTils.secretsOverlay) return;

        double pt = event.partialTicks;
        double dx = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * pt;
        double dy = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * pt;
        double dz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * pt;

        for (BlockPos pos : foundChests) {
            AxisAlignedBB bb = new AxisAlignedBB(pos, pos.add(1, 1, 1)).offset(-dx, -dy, -dz);
            ChestRenderer.renderBox(bb, CookieTils.chestOverlayColor, CookieTils.chestOverlayAlpha, true, true);
        }
    }




   /* private boolean isInDungeon() {
        Scoreboard sb = mc.theWorld.getScoreboard();
        if (sb == null) return false;

        ScoreObjective obj = sb.getObjectiveInDisplaySlot(1); // Sidebar
        if (obj != null) {
            String name = obj.getDisplayName();
            return name.contains("The Catacombs") || name.contains("Floor");
        }

        return false;
    } */
    private boolean isInDungeon() {
        return true;
    }
}