package net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets.rooms;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wrigglysplash.cookietils.addons.skyblock.dungeons.utils.DungeonUtils;
import org.lwjgl.opengl.GL11;

public class SpawnCornerScanner {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (!DungeonUtils.isInDungeon()) return;
        if (mc.theWorld.getTotalWorldTime() % 100 != 0) return; // check every ~5 seconds

        Entity mort = mc.theWorld.loadedEntityList.stream()
                .filter(e -> e.getName().equals("Mort"))
                .findFirst()
                .orElse(null);

        if (mort == null) return;

        BlockPos mortPos = mort.getPosition();
        BlockPos cornerPos = findDungeonCorner(mc.theWorld, mortPos);

        mc.thePlayer.addChatMessage(new ChatComponentText("§dMort Pos: " + mortPos));
        mc.thePlayer.addChatMessage(new ChatComponentText("§bDungeon Corner: " + cornerPos));
    }

    private BlockPos findDungeonCorner(World world, BlockPos start) {
        int x = start.getX();
        int y = start.getY();
        int z = start.getZ();

        while (isDungeonBlock(world, new BlockPos(x - 1, y, z))) x--;
        while (isDungeonBlock(world, new BlockPos(x, y - 1, z))) y--;
        while (isDungeonBlock(world, new BlockPos(x, y, z - 1))) z--;

        return new BlockPos(x, y, z);
    }

    private boolean isDungeonBlock(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return !world.isAirBlock(pos) && !block.getUnlocalizedName().contains("barrier");
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        Entity mort = mc.theWorld.loadedEntityList.stream()
                .filter(e -> e.getName().equals("Mort"))
                .findFirst()
                .orElse(null);

        if (mort == null) return;

        BlockPos mortPos = mort.getPosition();
        BlockPos cornerPos = findDungeonCorner(mc.theWorld, mortPos);

        double pt = event.partialTicks;
        double dx = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * pt;
        double dy = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * pt;
        double dz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * pt;

        Vec3 mortVec = new Vec3(
                mort.posX - dx,
                mort.posY + mort.getEyeHeight() - dy,
                mort.posZ - dz
        );

        Vec3 cornerVec = new Vec3(
                cornerPos.getX() + 0.5 - dx,
                cornerPos.getY() + 0.5 - dy,
                cornerPos.getZ() + 0.5 - dz
        );

        drawTracerLine(mortVec, cornerVec, 0.2F, 1.0F, 0.2F, 1.0F); // green tracer
    }

    private void drawTracerLine(Vec3 from, Vec3 to, float r, float g, float b, float a) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        wr.pos(from.xCoord, from.yCoord, from.zCoord).color(r, g, b, a).endVertex();
        wr.pos(to.xCoord, to.yCoord, to.zCoord).color(r, g, b, a).endVertex();
        tess.draw();

        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}
