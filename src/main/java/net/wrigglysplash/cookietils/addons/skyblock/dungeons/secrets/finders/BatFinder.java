package net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets.finders;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wrigglysplash.cookietils.CookieTils;
import net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets.renderer.ChestRenderer;
import net.wrigglysplash.cookietils.utils.checks.DungeonCheck;

import java.util.ArrayList;
import java.util.List;

public class BatFinder {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<EntityBat> foundBats = new ArrayList<>();
    private int tickCount = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (!DungeonCheck.isInDungeon()) return;
        if (!CookieTils.secretsOverlay) return;

        tickCount++;
        if (tickCount % 20 != 0) return;

        foundBats.clear();
        for (Object entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityBat) {
                EntityBat bat = (EntityBat) entity;
                if (!bat.isDead && bat.isEntityAlive()) {
                    foundBats.add(bat);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (!DungeonCheck.isInDungeon()) return;
        if (!CookieTils.secretsOverlay) return;

        double pt = event.partialTicks;
        double dx = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * pt;
        double dy = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * pt;
        double dz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * pt;

        for (EntityBat bat : foundBats) {
            AxisAlignedBB bb = bat.getEntityBoundingBox().offset(-dx, -dy, -dz);
            ChestRenderer.renderBox(bb, CookieTils.batOverlayColor, CookieTils.batOverlayAlpha, true, true);
        }
    }
}
