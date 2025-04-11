package net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wrigglysplash.cookietils.addons.skyblock.dungeons.utils.DungeonUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SecretLogger {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean hasLogged = false;
    private final Set<BlockPos> logged = new HashSet<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null || hasLogged) return;
        if (!DungeonUtils.isInDungeon()) return;

        World world = mc.theWorld;
        StringBuilder out = new StringBuilder();
        out.append("# CookieTils Secret Log\n");

        for (TileEntity tile : world.loadedTileEntityList) {
            BlockPos pos = tile.getPos();
            if (tile instanceof TileEntityChest && logged.add(pos)) {
                out.append("CHEST at ").append(pos.getX()).append(", ").append(pos.getY()).append(", ").append(pos.getZ()).append("\n");
            }
        }

        for (BlockPos pos : BlockPos.getAllInBox(mc.thePlayer.getPosition().add(-100, -30, -100), mc.thePlayer.getPosition().add(100, 30, 100))) {
            if (world.getBlockState(pos).getBlock() == Blocks.lever && logged.add(pos)) {
                out.append("LEVER at ").append(pos.getX()).append(", ").append(pos.getY()).append(", ").append(pos.getZ()).append("\n");
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("CookieTils_SecretsLog.txt", true));
            writer.write(out.toString());
            writer.newLine();
            writer.close();
            hasLogged = true;
            mc.thePlayer.addChatMessage(new ChatComponentText("§a[CookieTils] Logged dungeon secrets to file."));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isInDungeon() {
        if (mc.theWorld == null || mc.thePlayer == null) return false;
        if (mc.theWorld.getScoreboard() == null) return false;

        ScoreObjective obj = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
        if (obj == null) return false;

        String displayName = obj.getDisplayName();
        return displayName.contains("The Catacombs");
    }
}