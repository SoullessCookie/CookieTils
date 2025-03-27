package net.wrigglysplash.cookietils.utils.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.wrigglysplash.cookietils.CookieTils;

public class CommandCookieTils extends CommandBase {

    @Override
    public String getCommandName() {
        return "cookietils";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cookietils <setcolor|setalpha|reload>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText("Usage: /cookietils <setcolor|setalpha|reload>"));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "setcolor":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText("Usage: /cookietils setcolor <#HEX>"));
                    return;
                }
                CookieTils.chestOverlayColor = args[1];
                CookieTils.config.get("Skyblock Dungeons", "Overlay Color", "#FF0000").set(args[1]);
                CookieTils.config.save();
                sender.addChatMessage(new ChatComponentText("Overlay color set to " + args[1]));
                break;

            case "setalpha":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText("Usage: /cookietils setalpha <0.0-1.0>"));
                    return;
                }
                try {
                    float alpha = Float.parseFloat(args[1]);
                    CookieTils.chestOverlayAlpha = alpha;
                    CookieTils.config.get("Skyblock Dungeons", "Overlay Transparency", 0.5).set(alpha);
                    CookieTils.config.save();
                    sender.addChatMessage(new ChatComponentText("Overlay transparency set to " + alpha));
                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText("Invalid number."));
                }
                break;

            case "reload":
                CookieTils.config.load();
                CookieTils.chestOverlayColor = CookieTils.config.get("Skyblock Dungeons", "Overlay Color", "#FF0000").getString();
                CookieTils.chestOverlayAlpha = (float) CookieTils.config.get("Skyblock Dungeons", "Overlay Transparency", 0.5).getDouble();
                sender.addChatMessage(new ChatComponentText("CookieTils config reloaded."));
                break;

            case "toggle":
                CookieTils.secretsEnabled = !CookieTils.secretsEnabled;
                CookieTils.config.get("Skyblock Dungeons", "Secrets Enabled", true).set(CookieTils.secretsEnabled);
                CookieTils.config.save();
                sender.addChatMessage(new ChatComponentText("Secrets module is now " + (CookieTils.secretsEnabled ? "enabled." : "disabled.")));
                break;

            default:
                sender.addChatMessage(new ChatComponentText("Unknown subcommand."));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // allow all players to run
    }
}