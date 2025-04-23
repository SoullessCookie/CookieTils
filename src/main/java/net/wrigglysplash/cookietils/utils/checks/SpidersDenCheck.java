package net.wrigglysplash.cookietils.utils.checks;

import net.wrigglysplash.cookietils.utils.AreaUtil;

public class SpidersDenCheck {
    private static int areaCheckCooldown = 0;
    private static boolean cachedAreaStatus = false;

    public static boolean isInArea() {
        if (areaCheckCooldown > 0) {
            areaCheckCooldown--;
            return cachedAreaStatus;
        }

        String area = AreaUtil.getCurrentArea().toLowerCase().trim();

        cachedAreaStatus = area.contains("spider's den");
        areaCheckCooldown = 20;

        return cachedAreaStatus;
    }
}
