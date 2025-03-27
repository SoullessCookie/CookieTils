package net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class ChestRenderer {
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

    private static float[] hexToRGBA(String hex, float alpha) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        int rgb = Integer.parseInt(hex, 16);
        float r = ((rgb >> 16) & 0xFF) / 255.0F;
        float g = ((rgb >> 8) & 0xFF) / 255.0F;
        float b = (rgb & 0xFF) / 255.0F;
        return new float[]{r, g, b, alpha};
    }
}
