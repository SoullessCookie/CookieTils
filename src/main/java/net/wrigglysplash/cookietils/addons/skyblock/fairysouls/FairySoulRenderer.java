package net.wrigglysplash.cookietils.addons.skyblock.fairysouls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

public class FairySoulRenderer {
    public static void renderBox(AxisAlignedBB bb, String hexColor, float fillAlpha, boolean filled, boolean outline) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (filled) {
            float[] fillColor = hexToRGBA(hexColor, fillAlpha);
            GlStateManager.color(fillColor[0], fillColor[1], fillColor[2], fillColor[3]);
            drawFilledBox(bb);
        }

        if (outline) {
            float[] outlineColor = hexToRGBA(hexColor, 0.7F); // fixed outline alpha
            GlStateManager.color(outlineColor[0], outlineColor[1], outlineColor[2], outlineColor[3]);
            GL11.glLineWidth(2.0F);
            Tessellator tess = Tessellator.getInstance();
            WorldRenderer wr = tess.getWorldRenderer();
            wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
            drawOutlinedBox(wr, bb);
            tess.draw();
        }

        // Reset GL state
        GL11.glEnable(GL11.GL_CULL_FACE);
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void drawOutlinedBox(WorldRenderer wr, AxisAlignedBB bb) {
        // Bottom
        wr.pos(bb.minX, bb.minY, bb.minZ).endVertex(); wr.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).endVertex(); wr.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).endVertex(); wr.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).endVertex(); wr.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        // Top
        wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex(); wr.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).endVertex(); wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        // Verticals
        wr.pos(bb.minX, bb.minY, bb.minZ).endVertex(); wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).endVertex(); wr.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
    }

    private static void drawFilledBox(AxisAlignedBB bb) {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        // Bottom
        wr.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).endVertex();

        // Top
        wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();

        // North
        wr.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex();

        // South
        wr.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();

        // West
        wr.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex();

        // East
        wr.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();

        tess.draw();
    }

    public static void renderDistanceLabel(BlockPos pos, String text, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRendererObj;
        RenderManager renderManager = mc.getRenderManager();

        double dx = pos.getX() + 0.5 - renderManager.viewerPosX;
        double dy = pos.getY() + 1.2 - renderManager.viewerPosY;
        double dz = pos.getZ() + 0.5 - renderManager.viewerPosZ;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        float scale = (float) (0.001F * distance);

        GlStateManager.pushMatrix();
        GlStateManager.translate(dx, dy, dz);
        GlStateManager.rotate(-renderManager.playerViewY, 0F, 1F, 0F);
        GlStateManager.rotate(renderManager.playerViewX, 1F, 0F, 0F);
        GlStateManager.scale(-scale, -scale, scale);

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        int textWidth = font.getStringWidth(text) / 2;
        int alpha = (int)(0.9f * 255); // 90% opacity
        int color = 0xFFFFFF | (alpha << 24);

        font.drawString(text, -textWidth, 0, color);

        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public static void renderBeaconBeam(BlockPos pos, float partialTicks, int color) {
        Minecraft mc = Minecraft.getMinecraft();
        double x = pos.getX() + 0.5 - mc.getRenderManager().viewerPosX;
        double y = pos.getY() - mc.getRenderManager().viewerPosY;
        double z = pos.getZ() + 0.5 - mc.getRenderManager().viewerPosZ;

        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        float width = 0.2F;
        float height = 256F;

        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // North-South face
        wr.pos(-width, 0, -width).color(r, g, b, 0.4F).endVertex();
        wr.pos(width, 0, -width).color(r, g, b, 0.4F).endVertex();
        wr.pos(width, height, -width).color(r, g, b, 0.4F).endVertex();
        wr.pos(-width, height, -width).color(r, g, b, 0.4F).endVertex();

        wr.pos(-width, 0, width).color(r, g, b, 0.4F).endVertex();
        wr.pos(width, 0, width).color(r, g, b, 0.4F).endVertex();
        wr.pos(width, height, width).color(r, g, b, 0.4F).endVertex();
        wr.pos(-width, height, width).color(r, g, b, 0.4F).endVertex();

        // East-West face
        wr.pos(-width, 0, -width).color(r, g, b, 0.4F).endVertex();
        wr.pos(-width, 0, width).color(r, g, b, 0.4F).endVertex();
        wr.pos(-width, height, width).color(r, g, b, 0.4F).endVertex();
        wr.pos(-width, height, -width).color(r, g, b, 0.4F).endVertex();

        wr.pos(width, 0, -width).color(r, g, b, 0.4F).endVertex();
        wr.pos(width, 0, width).color(r, g, b, 0.4F).endVertex();
        wr.pos(width, height, width).color(r, g, b, 0.4F).endVertex();
        wr.pos(width, height, -width).color(r, g, b, 0.4F).endVertex();

        tess.draw();

        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static float[] hexToRGBA(String hex, float alpha) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        int rgb = Integer.parseInt(hex, 16);
        float r = ((rgb >> 16) & 0xFF) / 255.0F;
        float g = ((rgb >> 8) & 0xFF) / 255.0F;
        float b = (rgb & 0xFF) / 255.0F;
        return new float[]{r, g, b, alpha};
    }
}