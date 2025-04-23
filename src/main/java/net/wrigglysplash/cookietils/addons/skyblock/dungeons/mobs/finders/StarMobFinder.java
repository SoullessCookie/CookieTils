package net.wrigglysplash.cookietils.addons.skyblock.dungeons.mobs.finders;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wrigglysplash.cookietils.CookieTils;
import net.wrigglysplash.cookietils.addons.skyblock.dungeons.mobs.renderer.StarMobRenderer;
import net.wrigglysplash.cookietils.utils.checks.DungeonCheck;

import java.util.List;
import java.util.stream.Collectors;

public class StarMobFinder {

    private final Minecraft mc = Minecraft.getMinecraft();
    private List<EntityLivingBase> starMobs;
    private int tickCount = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (!DungeonCheck.isInDungeon()) return;
        if (!CookieTils.secretsOverlay) return;

        tickCount++;
        if (tickCount % 10 != 0) return;

        starMobs = mc.theWorld.loadedEntityList.stream()
                .filter(e -> e instanceof EntityLivingBase)
                .map(e -> (EntityLivingBase) e)
                .filter(e -> {
                    String rawName = e.getDisplayName().getFormattedText();
                    String name = StringUtils.stripControlCodes(rawName);
                    return name.indexOf(10031) != -1;
                })
                .collect(Collectors.toList());
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null || starMobs == null) return;
        if (!DungeonCheck.isInDungeon()) return;
        if (!CookieTils.secretsOverlay) return;

        double pt = event.partialTicks;
        double dx = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * pt;
        double dy = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * pt;
        double dz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * pt;

        for (EntityLivingBase entity : starMobs) {
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pt;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pt;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pt;

            AxisAlignedBB box = new AxisAlignedBB(
                    x - 0.3 - dx, y - dy - 1.9,     z - 0.3 - dz,
                    x + 0.3 - dx, y + 1.8 - dy - 1.9, z + 0.3 - dz
            );
            StarMobRenderer.renderBox(box, CookieTils.starMobOverlayColor, CookieTils.starMobOverlayAlpha, true, true);
        }
    }
}