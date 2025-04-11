package net.wrigglysplash.cookietils.addons.skyblock.fairysouls;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.wrigglysplash.cookietils.addons.skyblock.fairysouls.utils.FairyAreaUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FairySoulStorage {

    public static List<BlockPos> loadFairySoulPositions() {
        List<BlockPos> positions = new ArrayList<>();
        Minecraft mc = Minecraft.getMinecraft();

        String area = FairyAreaUtils.getCurrentArea().toLowerCase().replace(" ", "_");
        File file = null;

        // I know this isn't ideal, but for some reason when trying to get the name stripped of formatting, it always
        // had other random characters in it like \r7 and whatnot that weren't strippable for some fucking reason
        if (area.contains("hub")) {
            file = new File(mc.mcDataDir, "CookieTils/FairySouls/locations/Peach/hub.csv");
        } else if (area.contains("the_end")) {
            file = new File(mc.mcDataDir, "CookieTils/FairySouls/locations/Peach/the_end.csv");
        } else if (area.contains("spiders_den")) {
            file = new File(mc.mcDataDir, "CookieTils/FairySouls/locations/Peach/spiders_den.csv");
        } else if (area.contains("the_park")) {
            file = new File(mc.mcDataDir, "CookieTils/FairySouls/locations/Peach/the_park.csv");
        } else if (area.contains("the_farming_islands")) {
            file = new File(mc.mcDataDir, "CookieTils/FairySouls/locations/Peach/the_farming_islands.csv");
        } else if (area.contains("gold_mine")) {
            file = new File(mc.mcDataDir, "CookieTils/FairySouls/locations/Peach/gold_mine.csv");
        } else if (area.contains("deep_caverns")) {
            file = new File(mc.mcDataDir, "CookieTils/FairySouls/locations/Peach/deep_caverns.csv");
        } else if (area.contains("dwarven_mines")) {
            file = new File(mc.mcDataDir, "CookieTils/FairySouls/locations/Peach/dwarven_mines.csv");
        } else if (area.contains("crimson_isle")) {
            file = new File(mc.mcDataDir, "CookieTils/FairySouls/locations/Peach/crimson_isle.csv");
        } else if (area.contains("dungeon_hub")) {
            file = new File(mc.mcDataDir, "CookieTils/FairySouls/locations/Peach/dungeon_hub.csv");
        } else if (area.contains("winter")) {
            file = new File(mc.mcDataDir, "CookieTils/FairySouls/locations/Peach/winter_island.csv");
        }

        if (file == null) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§c[FairySouls] No file mapped for area: " + area));
            return positions;
        }

        if (!file.exists()) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§c[FairySouls] File does not exist: " + file.getAbsolutePath()));
            return positions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",");
                if (parts.length != 3) continue;

                try {
                    int x = Integer.parseInt(parts[0].trim());
                    int y = Integer.parseInt(parts[1].trim());
                    int z = Integer.parseInt(parts[2].trim());
                    positions.add(new BlockPos(x, y, z));
                    count++;
                } catch (NumberFormatException ignored) {}
            }

            mc.thePlayer.addChatMessage(new ChatComponentText("§aLoaded " + count + " fairy souls."));
        } catch (IOException e) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§cError reading file: " + e.getMessage()));
            e.printStackTrace();
        }

        return positions;
    }

    public static void ensureFairySoulFilesExist(String profile) {
        Minecraft mc = Minecraft.getMinecraft();

        File collectedDir = new File(mc.mcDataDir, "CookieTils/FairySouls/collected/" + profile);
        File locationDir = new File(mc.mcDataDir, "CookieTils/FairySouls/locations/" + profile);

        String[] islands = {
                "hub", "the_end", "spiders_den", "the_park", "the_farming_islands",
                "gold_mine", "deep_caverns", "dwarven_mines", "crimson_isle",
                "dungeon_hub", "winter_island"
        };

        if (!collectedDir.exists() && !collectedDir.mkdirs()) {
            System.err.println("Failed to create collected directory: " + collectedDir.getAbsolutePath());
            return;
        }

        if (!locationDir.exists()) {
            if (!locationDir.mkdirs()) {
                System.err.println("Failed to create locations directory: " + locationDir.getAbsolutePath());
                return;
            }
        }

        for (String island : islands) {
            File collectedFile = new File(collectedDir, island + ".csv");
            if (!collectedFile.exists()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(collectedFile))) {
                    writer.write("# Collected fairy soul coordinates for " + island);
                    writer.newLine();
                    writer.write("# Format: x,y,z");
                    writer.newLine();
                    System.out.println("Created collected file for: " + island);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}