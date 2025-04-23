package net.wrigglysplash.cookietils.addons.skyblock.farming.garden;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wrigglysplash.cookietils.CookieTils;
import net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets.renderer.ChestRenderer;
import net.wrigglysplash.cookietils.utils.checks.GardenCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PestFinder {
    private final Minecraft mc = Minecraft.getMinecraft();
    private List<EntityLivingBase> pests = new ArrayList<>();
    private int tickCount = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (!GardenCheck.isInArea()) return;
        if (!CookieTils.secretsOverlay) return;

        tickCount++;
        if (tickCount % 10 != 0) return;

        pests = mc.theWorld.loadedEntityList.stream()
                .filter(e -> e instanceof EntityBat || e instanceof EntitySilverfish)
                .map(e -> (EntityLivingBase) e)
                .collect(Collectors.toList());
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (!GardenCheck.isInArea()) return;
        if (!CookieTils.secretsOverlay) return;

        double pt = event.partialTicks;

        double px = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * pt;
        double py = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * pt;
        double pz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * pt;

        for (EntityLivingBase entity : pests) {
            double ex = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pt;
            double ey = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pt;
            double ez = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pt;

            AxisAlignedBB box = new AxisAlignedBB(
                    ex - 0.35, ey, ez - 0.35,
                    ex + 0.35, ey + 0.7, ez + 0.35
            );

            AxisAlignedBB renderBox = box.offset(-px, -py, -pz);
            ChestRenderer.renderBox(renderBox, CookieTils.batOverlayColor, CookieTils.batOverlayAlpha, true, true);
        }
    }
}