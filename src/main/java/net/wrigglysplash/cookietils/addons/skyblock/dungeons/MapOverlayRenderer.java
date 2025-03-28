package net.wrigglysplash.cookietils.addons.skyblock.dungeons;

import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;

public class MapOverlayRenderer {

    private final Minecraft mc = Minecraft.getMinecraft();
    private int textureId = -1;
    private byte[] lastColors = new byte[0];
    private boolean debugPrinted = false;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!isInDungeon()) return;
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        ItemStack stack = getDungeonMap();
        if (stack == null || !(stack.getItem() instanceof ItemMap)) return;

        MapData data = ((ItemMap) stack.getItem()).getMapData(stack, mc.theWorld);
        if (data == null || data.colors == null) return;

        byte[] colors = data.colors;

        if (!debugPrinted) {
            mc.thePlayer.addChatMessage(new ChatComponentText("Map Item: " + stack.getDisplayName()));
            mc.thePlayer.addChatMessage(new ChatComponentText("Map Colors Length: " + colors.length));
            debugPrinted = true;
        }

        if (colors.length != lastColors.length || !Arrays.equals(colors, lastColors)) {
            lastColors = colors.clone();

            int[] rgbPixels = new int[128 * 128];
            for (int i = 0; i < colors.length; i++) {
                int index = colors[i] & 255;
                MapColor mapColor = MapColor.mapColorArray[index >> 2];
                int shade = index & 3;

                int baseColor = mapColor.colorValue;
                int r = (baseColor >> 16) & 255;
                int g = (baseColor >> 8) & 255;
                int b = baseColor & 255;

                float shadeFactor = new float[]{180F, 220F, 255F, 135F}[shade] / 255F;
                r = (int) (r * shadeFactor);
                g = (int) (g * shadeFactor);
                b = (int) (b * shadeFactor);

                rgbPixels[i] = (255 << 24) | (r << 16) | (g << 8) | b;
            }

            BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
            image.setRGB(0, 0, 128, 128, rgbPixels, 0, 128);

            if (textureId == -1) textureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureId);
            TextureUtil.uploadTextureImageAllocate(textureId, image, false, false);
        }

        int x = 10;
        int y = 10;
        int size = 128;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.color(1F, 1F, 1F, 1F);
        glBindTexture(GL_TEXTURE_2D, textureId);

        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex2f(x, y);
        glTexCoord2f(1, 0); glVertex2f(x + size, y);
        glTexCoord2f(1, 1); glVertex2f(x + size, y + size);
        glTexCoord2f(0, 1); glVertex2f(x, y + size);
        glEnd();

        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private ItemStack getDungeonMap() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemMap) {
                return stack;
            }
        }
        return null;
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