package net.wrigglysplash.cookietils.addons.skyblock.fairysouls;

import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wrigglysplash.cookietils.CookieTils;
import net.wrigglysplash.cookietils.utils.checks.DungeonCheck;
import net.wrigglysplash.cookietils.utils.AreaUtil;
import net.wrigglysplash.cookietils.addons.skyblock.fairysouls.utils.FairyProfileUtils;

import java.util.ArrayList;
import java.util.List;

public class FairySoulFinder {
    private final Minecraft mc = Minecraft.getMinecraft();
    private int tickCount = 0;
    private List<BlockPos> soulPositions = new ArrayList<>();
    private String lastArea = "";

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (DungeonCheck.isInDungeon()) return;
        if (!FairyProfileUtils.isInSkyBlock()) return;
        if (!CookieTils.soulEnabled) return;

        tickCount++;
        if (tickCount % 20 != 0) return;

        String area = AreaUtil.getCurrentArea();
        if (!area.equalsIgnoreCase(lastArea)) {
            soulPositions = FairySoulStorage.loadFairySoulPositions();
            lastArea = area;
            mc.thePlayer.addChatMessage(new ChatComponentText("§aLoaded " + soulPositions.size() + " fairy souls."));
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (DungeonCheck.isInDungeon()) return;
        if (!FairyProfileUtils.isInSkyBlock()) return;
        if (!CookieTils.soulEnabled) return;

        for (BlockPos pos : soulPositions) {
            // for the distance
            double dx = pos.getX() + 0.5 - mc.thePlayer.posX;
            double dy = pos.getY() + 0.5 - mc.thePlayer.posY;
            double dz = pos.getZ() + 0.5 - mc.thePlayer.posZ;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            // for the boxes
            double pt = event.partialTicks;
            double px = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * pt;
            double py = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * pt;
            double pz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * pt;

            if (distance <= 15) {
                AxisAlignedBB bb = new AxisAlignedBB(pos, pos.add(1, 1, 1)).offset(-px, -py, -pz);
                FairySoulRenderer.renderBox(bb, CookieTils.soulOverlayColor, CookieTils.soulOverlayAlpha, true, true);
            }

            String distanceStr = String.format("%.0fm", distance);
            FairySoulRenderer.renderDistanceLabel(pos, distanceStr, event.partialTicks);
            FairySoulRenderer.renderBeaconBeam(pos, event.partialTicks, 0xB330FF);
        }
    }
}