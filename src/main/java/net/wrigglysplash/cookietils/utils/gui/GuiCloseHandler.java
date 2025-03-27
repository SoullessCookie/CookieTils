package net.wrigglysplash.cookietils.utils.gui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wrigglysplash.cookietils.CookieTils;

public class GuiCloseHandler {
    @SubscribeEvent
    public void onGuiClosed(GuiOpenEvent event) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiConfig) {
            GuiConfig current = (GuiConfig) Minecraft.getMinecraft().currentScreen;

            // Check if it's your mod's config
            if (current.modID.equals(CookieTils.MODID)) {
                System.out.println("[CookieTils] Detected closing of config GUI. Reloading...");
                CookieTils.reloadConfigValues();
            }
        }
    }
}
