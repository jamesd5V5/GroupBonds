package org.mammothplugins.groupbonds.menu;

import org.bukkit.Material;

public enum NonItems {
    AIR,
    WATER,
    STATIONARY_WATER,
    LAVA,
    STATIONARY_LAVA,
    GRASS_PATH,
    BED_BLOCK,
    PISTON_EXTENSION,
    PISTON_MOVING_PIECE,
    DOUBLE_STEP,
    FIRE,
    REDSTONE_WIRE,
    CROPS,
    BURNING_FURNACE,
    SIGN_POST,
    WOODEN_DOOR,
    WALL_SIGN,
    IRON_DOOR_BLOCK,
    GLOWING_REDSTONE_ORE,
    REDSTONE_TORCH_OFF,
    SUGAR_CANE_BLOCK,
    PORTAL,
    CAKE_BLOCK,
    DIODE_BLOCK_OFF,
    DIODE_BLOCK_ON,
    PUMPKIN_STEM,
    MELON_STEM,
    NETHER_WARTS,
    BREWING_STAND,
    CAULDRON,
    ENDER_PORTAL,
    REDSTONE_LAMP_ON,
    WOOD_DOUBLE_STEP,
    COCOA,
    TRIPWIRE,
    FLOWER_POT,
    CARROT,
    POTATO,
    SKULL,
    REDSTONE_COMPARATOR_OFF,
    REDSTONE_COMPARATOR_ON,
    STANDING_BANNER,
    WALL_BANNER,
    DAYLIGHT_DETECTOR_INVERTED,
    DOUBLE_STONE_SLAB2,
    SPRUCE_DOOR,
    BIRCH_DOOR,
    JUNGLE_DOOR,
    ACACIA_DOOR,
    DARK_OAK_DOOR,
    PURPUR_DOUBLE_SLAB,
    BEETROOT_BLOCK,
    END_GATEWAY,
    FROSTED_ICE,
    SOIL,
    DOUBLE_PLANT,
    LONG_GRASS;

    public static boolean isAnItem(Material material) {
        for (NonItems nonItem : NonItems.values()) {
            if (nonItem.name().equals(material.name()))
                return false;
        }
        return true;
    }
}
