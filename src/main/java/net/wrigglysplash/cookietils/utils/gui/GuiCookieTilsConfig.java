package net.wrigglysplash.cookietils.utils.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.wrigglysplash.cookietils.CookieTils;

public class GuiCookieTilsConfig extends GuiConfig {
    public GuiCookieTilsConfig(GuiScreen parentScreen) {
        super(parentScreen,
                new ConfigElement(CookieTils.config.getCategory("skyblock dungeons")).getChildElements(),
                CookieTils.MODID,
                false,
                false,
                "CookieTils Config");
    }
}