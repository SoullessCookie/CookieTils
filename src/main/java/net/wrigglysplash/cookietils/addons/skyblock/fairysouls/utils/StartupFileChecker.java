package net.wrigglysplash.cookietils.addons.skyblock.fairysouls.utils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wrigglysplash.cookietils.addons.skyblock.fairysouls.FairySoulStorage;

public class StartupFileChecker {
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean hasInitialized = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (hasInitialized) return;

        if (FairyProfileUtils.isInSkyBlock()) {
            String profile = FairyProfileUtils.getCurrentProfile();
            if (!profile.equals("Unknown")) {
                FairySoulStorage.ensureFairySoulFilesExist(profile);
                hasInitialized = true;
            }
        }
    }
}
