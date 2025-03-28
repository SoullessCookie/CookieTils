package net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Blocks;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wrigglysplash.cookietils.CookieTils;
import net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets.renderer.ChestRenderer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GridBasedSecretFinder {

    private final Minecraft mc = Minecraft.getMinecraft();
    private BlockPos currentAnchor = null;

    private final List<BlockPos> visibleChests = new ArrayList<>();
    private final List<BlockPos> visibleLevers = new ArrayList<>();
    private final List<BlockPos> visibleBats = new ArrayList<>();

    private BlockPos getAnchor(BlockPos pos) {
        int x = Math.floorDiv(pos.getX(), 32) * 32;
        int z = Math.floorDiv(pos.getZ(), 32) * 32;
        return new BlockPos(x, pos.getY(), z);
    }

    private boolean isWithin(BlockPos pos, BlockPos min, BlockPos max) {
        return pos.getX() >= min.getX() && pos.getX() <= max.getX()
                && pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
    }

    private boolean isInDungeon() {
        if (mc.theWorld == null || mc.thePlayer == null) return false;
        if (mc.theWorld.getScoreboard() == null) return false;

        ScoreObjective obj = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
        if (obj == null) return false;

        String displayName = obj.getDisplayName();
        return displayName.contains("The Catacombs");
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null || !isInDungeon()) return;
        if (!CookieTils.secretsOverlay) return;

        BlockPos playerAnchor = getAnchor(mc.thePlayer.getPosition());

        if (!playerAnchor.equals(currentAnchor)) {
            currentAnchor = playerAnchor;

            visibleChests.clear();
            visibleLevers.clear();
            visibleBats.clear();

            BlockPos min = new BlockPos(currentAnchor.getX(), 0, currentAnchor.getZ());
            BlockPos max = min.add(31, 255, 31);

            for (TileEntity tile : mc.theWorld.loadedTileEntityList) {
                BlockPos pos = tile.getPos();

                if (tile instanceof TileEntityChest && isWithin(pos, min, max)) {
                    visibleChests.add(pos);
                }
            }

            for (BlockPos pos : BlockPos.getAllInBox(min, max)) {
                if (mc.theWorld.getBlockState(pos).getBlock() == Blocks.lever) {
                    visibleLevers.add(pos);
                }
            }

            for (Object entity : mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityBat && entity != null && ((EntityBat) entity).isEntityAlive()) {
                    BlockPos pos = ((EntityBat) entity).getPosition();
                    if (isWithin(pos, min, max)) {
                        visibleBats.add(pos);
                    }
                }
            }

            mc.thePlayer.addChatMessage(new ChatComponentText("§7[§bCookieTils§7] Now in anchor square §f" + currentAnchor.getX() + ", " + currentAnchor.getZ()));
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null || !isInDungeon()) return;
        if (!CookieTils.secretsOverlay) return;

        double pt = event.partialTicks;
        double dx = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * pt;
        double dy = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * pt;
        double dz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * pt;

        // Draw current anchor square outline
        BlockPos min = new BlockPos(currentAnchor.getX(), 70, currentAnchor.getZ());
        AxisAlignedBB box = new AxisAlignedBB(min, min.add(32, 1, 32)).offset(-dx, -dy, -dz);

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GL11.glLineWidth(1.0F);
        GlStateManager.color(0F, 1F, 1F, 0.3F); // Cyan outline
        drawOutlinedBox(box);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();

        // Render secrets
        for (BlockPos pos : visibleChests) {
            AxisAlignedBB bb = new AxisAlignedBB(pos, pos.add(1, 1, 1)).offset(-dx, -dy, -dz);
            ChestRenderer.renderBox(bb, CookieTils.chestOverlayColor, CookieTils.chestOverlayAlpha, true, true);
        }

        for (BlockPos pos : visibleLevers) {
            AxisAlignedBB bb = new AxisAlignedBB(pos, pos.add(1, 1, 1)).offset(-dx, -dy, -dz);
            ChestRenderer.renderBox(bb, CookieTils.leverOverlayColor, CookieTils.leverOverlayAlpha, true, true);
        }

        for (BlockPos pos : visibleBats) {
            AxisAlignedBB bb = new AxisAlignedBB(pos, pos.add(1, 1, 1)).offset(-dx, -dy, -dz);
            ChestRenderer.renderBox(bb, CookieTils.batOverlayColor, CookieTils.batOverlayAlpha, true, true);
        }
    }

    private void drawOutlinedBox(AxisAlignedBB bb) {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        wr.pos(bb.minX, bb.minY, bb.minZ).endVertex(); wr.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).endVertex(); wr.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).endVertex(); wr.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).endVertex(); wr.pos(bb.minX, bb.minY, bb.minZ).endVertex();

        wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex(); wr.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).endVertex(); wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex();

        wr.pos(bb.minX, bb.minY, bb.minZ).endVertex(); wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).endVertex(); wr.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();

        tess.draw();
    }
}