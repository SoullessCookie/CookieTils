package net.wrigglysplash.cookietils;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.wrigglysplash.cookietils.addons.skyblock.dungeons.secrets.GridBasedSecretFinder;
import net.wrigglysplash.cookietils.utils.commands.CommandCookieTils;
import net.wrigglysplash.cookietils.utils.gui.GuiCloseHandler;

import java.io.File;

@Mod(modid = CookieTils.MODID, version = CookieTils.VERSION, guiFactory = "net.wrigglysplash.cookietils.utils.gui.GuiFactory")
public class CookieTils
{
    public static final String MODID = "cookietils";
    public static final String VERSION = "1.0";

    public static Configuration config;

    // Config values
    public static boolean secretsEnabled;
    public static boolean secretsOverlay;
    public static String chestOverlayColor;
    public static float chestOverlayAlpha;
    public static String leverOverlayColor;
    public static float leverOverlayAlpha;
    public static String batOverlayColor;
    public static float batOverlayAlpha;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configFile = event.getSuggestedConfigurationFile();
        config = new Configuration(configFile);
        config.load();

        secretsEnabled = config.get("Skyblock Dungeons", "Secrets Enabled", true).getBoolean();
        secretsOverlay = config.get("Skyblock Dungeons", "Secrets Overlay", true).getBoolean();
        chestOverlayColor = config.get("Skyblock Dungeons", "Chest Color", "#0aff12").getString();
        chestOverlayAlpha = (float) config.get("Skyblock Dungeons", "Chest Overlay Transparency", 0.3).getDouble();
        leverOverlayColor = config.get("Skyblock Dungeons", "Lever Color", "#fc23f9").getString();
        leverOverlayAlpha = (float) config.get("Skyblock Dungeons", "Lever Overlay Transparency", 0.3).getDouble();
        batOverlayColor = config.get("Skyblock Dungeons", "Bat Color", "#1cc2ff").getString();
        batOverlayAlpha = (float) config.get("Skyblock Dungeons", "Bat Overlay Transparency", 0.3).getDouble();

        if (config.hasChanged()) config.save();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // Temporary
        MinecraftForge.EVENT_BUS.register(new GridBasedSecretFinder());

        MinecraftForge.EVENT_BUS.register(new GuiCloseHandler());

        //MinecraftForge.EVENT_BUS.register(new ChestFinder());
        //MinecraftForge.EVENT_BUS.register(new LeverFinder());
        //MinecraftForge.EVENT_BUS.register(new BatFinder());
        System.out.println("CookieTils loaded!");
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandCookieTils());
    }

    public static void reloadConfigValues() {
        secretsEnabled = config.get("Skyblock Dungeons", "Secrets Enabled", true).getBoolean();
        secretsOverlay = config.get("Skyblock Dungeons", "Secrets Overlay", true).getBoolean();
        chestOverlayColor = config.get("Skyblock Dungeons", "Chest Color", "#0aff12").getString();
        chestOverlayAlpha = (float) config.get("Skyblock Dungeons", "Chest Overlay Transparency", 0.3).getDouble();
        leverOverlayColor = config.get("Skyblock Dungeons", "Lever Color", "#fc23f9").getString();
        leverOverlayAlpha = (float) config.get("Skyblock Dungeons", "Lever Overlay Transparency", 0.3).getDouble();
        batOverlayColor = config.get("Skyblock Dungeons", "Bat Color", "#1cc2ff").getString();
        batOverlayAlpha = (float) config.get("Skyblock Dungeons", "Bat Overlay Transparency", 0.3).getDouble();
    }
}


