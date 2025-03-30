package net.wrigglysplash.cookietils.addons.skyblock.dungeons.mobs.finders;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wrigglysplash.cookietils.CookieTils;
import net.wrigglysplash.cookietils.addons.skyblock.dungeons.mobs.renderer.StarMobRenderer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StarMobFinder {

    private final Minecraft mc = Minecraft.getMinecraft();
    private List<EntityLivingBase> starMobs;
    private int tickCount = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (!isInDungeon()) return;
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
        if (!isInDungeon()) return;
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