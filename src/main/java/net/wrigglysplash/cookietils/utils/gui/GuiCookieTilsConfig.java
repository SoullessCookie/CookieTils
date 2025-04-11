package net.wrigglysplash.cookietils.utils.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.wrigglysplash.cookietils.CookieTils;

import java.util.Arrays;

public class GuiCookieTilsConfig extends GuiConfig {
    public GuiCookieTilsConfig(GuiScreen parentScreen) {
        super(parentScreen,
                Arrays.asList(
                        new ConfigElement(CookieTils.config.getCategory("skyblock dungeons")),
                        new ConfigElement(CookieTils.config.getCategory("fairy souls"))
                ),
                CookieTils.MODID,
                false,
                false,
                "CookieTils Config");
    }
}