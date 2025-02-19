package gg.mineral.bot.api.world.block

import gg.mineral.bot.api.math.BoundingBox
import gg.mineral.bot.api.world.ClientWorld

interface Block {
    /**
     * Returns the id of the block.
     *
     * @return the id of the block
     */
    val id: Int

    /**
     * Returns the bounding box of the block at the specified coordinates.
     *
     * @param world
     * the world
     * @param xTile
     * the x-coordinate of the block
     * @param yTile
     * the y-coordinate of the block
     * @param zTile
     * the z-coordinate of the block
     * @return the bounding box of the block at the specified coordinates
     */
    fun getCollisionBoundingBox(world: ClientWorld, xTile: Int, yTile: Int, zTile: Int): BoundingBox?

    companion object {
        const val AIR: Int = 0
        const val STONE: Int = 1
        const val GRASS: Int = 2
        const val DIRT: Int = 3
        const val COBBLESTONE: Int = 4
        const val PLANKS: Int = 5
        const val SAPLING: Int = 6
        const val BEDROCK: Int = 7
        const val WATER_FLOWING: Int = 8
        const val WATER_STILL: Int = 9
        const val LAVA_FLOWING: Int = 10
        const val LAVA_STILL: Int = 11
        const val SAND: Int = 12
        const val GRAVEL: Int = 13
        const val GOLD_ORE: Int = 14
        const val IRON_ORE: Int = 15
        const val COAL_ORE: Int = 16
        const val LOG: Int = 17
        const val LEAVES: Int = 18
        const val SPONGE: Int = 19
        const val GLASS: Int = 20
        const val LAPIS_ORE: Int = 21
        const val LAPIS_BLOCK: Int = 22
        const val DISPENSER: Int = 23
        const val SANDSTONE: Int = 24
        const val NOTE_BLOCK: Int = 25
        const val BED: Int = 26
        const val GOLDEN_RAIL: Int = 27
        const val DETECTOR_RAIL: Int = 28
        const val STICKY_PISTON: Int = 29
        const val COBWEB: Int = 30
        const val TALL_GRASS: Int = 31
        const val DEAD_BUSH: Int = 32
        const val PISTON: Int = 33
        const val PISTON_HEAD: Int = 34
        const val WOOL: Int = 35
        const val PISTON_EXTENSION: Int = 36
        const val DANDELION: Int = 37
        const val POPPY: Int = 38
        const val BROWN_MUSHROOM: Int = 39
        const val RED_MUSHROOM: Int = 40
        const val GOLD_BLOCK: Int = 41
        const val IRON_BLOCK: Int = 42
        const val DOUBLE_STONE_SLAB: Int = 43
        const val STONE_SLAB: Int = 44
        const val BRICKS: Int = 45
        const val TNT: Int = 46
        const val BOOKSHELF: Int = 47
        const val MOSSY_COBBLESTONE: Int = 48
        const val OBSIDIAN: Int = 49
        const val TORCH: Int = 50
        const val FIRE: Int = 51
        const val MOB_SPAWNER: Int = 52
        const val OAK_STAIRS: Int = 53
        const val CHEST: Int = 54
        const val REDSTONE_WIRE: Int = 55
        const val DIAMOND_ORE: Int = 56
        const val DIAMOND_BLOCK: Int = 57
        const val CRAFTING_TABLE: Int = 58
        const val CROPS: Int = 59
        const val FARMLAND: Int = 60
        const val FURNACE: Int = 61
        const val LIT_FURNACE: Int = 62
        const val STANDING_SIGN: Int = 63
        const val WOODEN_DOOR: Int = 64
        const val LADDER: Int = 65
        const val RAIL: Int = 66
        const val STONE_STAIRS: Int = 67
        const val WALL_SIGN: Int = 68
        const val LEVER: Int = 69
        const val STONE_PRESSURE_PLATE: Int = 70
        const val IRON_DOOR: Int = 71
        const val WOODEN_PRESSURE_PLATE: Int = 72
        const val REDSTONE_ORE: Int = 73
        const val LIT_REDSTONE_ORE: Int = 74
        const val UNLIT_REDSTONE_TORCH: Int = 75
        const val REDSTONE_TORCH: Int = 76
        const val STONE_BUTTON: Int = 77
        const val SNOW_LAYER: Int = 78
        const val ICE: Int = 79
        const val SNOW: Int = 80
        const val CACTUS: Int = 81
        const val CLAY: Int = 82
        const val SUGAR_CANE: Int = 83
        const val JUKEBOX: Int = 84
        const val FENCE: Int = 85
        const val PUMPKIN: Int = 86
        const val NETHERRACK: Int = 87
        const val SOUL_SAND: Int = 88
        const val GLOWSTONE: Int = 89
        const val NETHER_PORTAL: Int = 90
        const val LIT_PUMPKIN: Int = 91
        const val CAKE: Int = 92
        const val UNPOWERED_REPEATER: Int = 93
        const val POWERED_REPEATER: Int = 94
        const val STAINED_GLASS: Int = 95
        const val TRAPDOOR: Int = 96
        const val MONSTER_EGG: Int = 97
        const val STONE_BRICK: Int = 98
        const val HUGE_BROWN_MUSHROOM: Int = 99
        const val HUGE_RED_MUSHROOM: Int = 100
        const val IRON_BARS: Int = 101
        const val GLASS_PANE: Int = 102
        const val MELON_BLOCK: Int = 103
        const val PUMPKIN_STEM: Int = 104
        const val MELON_STEM: Int = 105
        const val VINE: Int = 106
        const val FENCE_GATE: Int = 107
        const val BRICK_STAIRS: Int = 108
        const val STONE_BRICK_STAIRS: Int = 109
        const val MYCELIUM: Int = 110
        const val LILY_PAD: Int = 111
        const val NETHER_BRICK: Int = 112
        const val NETHER_BRICK_FENCE: Int = 113
        const val NETHER_BRICK_STAIRS: Int = 114
        const val NETHER_WART: Int = 115
        const val ENCHANTING_TABLE: Int = 116
        const val BREWING_STAND: Int = 117
        const val CAULDRON: Int = 118
        const val END_PORTAL: Int = 119
        const val END_PORTAL_FRAME: Int = 120
        const val END_STONE: Int = 121
        const val DRAGON_EGG: Int = 122
        const val REDSTONE_LAMP: Int = 123
        const val LIT_REDSTONE_LAMP: Int = 124
        const val DOUBLE_WOODEN_SLAB: Int = 125
        const val WOODEN_SLAB: Int = 126
        const val COCOA: Int = 127
        const val SANDSTONE_STAIRS: Int = 128
        const val EMERALD_ORE: Int = 129
        const val ENDER_CHEST: Int = 130
        const val TRIPWIRE_HOOK: Int = 131
        const val TRIPWIRE: Int = 132
        const val EMERALD_BLOCK: Int = 133
        const val SPRUCE_STAIRS: Int = 134
        const val BIRCH_STAIRS: Int = 135
        const val JUNGLE_STAIRS: Int = 136
        const val COMMAND_BLOCK: Int = 137
        const val BEACON: Int = 138
        const val COBBLESTONE_WALL: Int = 139
        const val FLOWER_POT: Int = 140
        const val CARROTS: Int = 141
        const val POTATOES: Int = 142
        const val WOODEN_BUTTON: Int = 143
        const val SKULL: Int = 144
        const val ANVIL: Int = 145
        const val TRAPPED_CHEST: Int = 146
        const val LIGHT_WEIGHTED_PRESSURE_PLATE: Int = 147
        const val HEAVY_WEIGHTED_PRESSURE_PLATE: Int = 148
        const val UNPOWERED_COMPARATOR: Int = 149
        const val POWERED_COMPARATOR: Int = 150
        const val DAYLIGHT_DETECTOR: Int = 151
        const val REDSTONE_BLOCK: Int = 152
        const val QUARTZ_ORE: Int = 153
        const val HOPPER: Int = 154
        const val QUARTZ_BLOCK: Int = 155
        const val QUARTZ_STAIRS: Int = 156
        const val ACTIVATOR_RAIL: Int = 157
        const val DROPPER: Int = 158
        const val STAINED_HARDENED_CLAY: Int = 159
        const val STAINED_GLASS_PANE: Int = 160
        const val LEAVES2: Int = 161
        const val LOG2: Int = 162
        const val ACACIA_STAIRS: Int = 163
        const val DARK_OAK_STAIRS: Int = 164
        const val SLIME_BLOCK: Int = 165
        const val BARRIER: Int = 166
        const val IRON_TRAPDOOR: Int = 167
        const val PRISMARINE: Int = 168
        const val SEA_LANTERN: Int = 169
        const val HAY_BLOCK: Int = 170
        const val CARPET: Int = 171
        const val HARDENED_CLAY: Int = 172
        const val COAL_BLOCK: Int = 173
        const val PACKED_ICE: Int = 174
        const val DOUBLE_PLANT: Int = 175
    }
}
