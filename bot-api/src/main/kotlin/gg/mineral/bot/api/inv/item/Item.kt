package gg.mineral.bot.api.inv.item

interface Item {
    /**
     * Gets the id of the item.
     *
     * @return the id of the item
     */
    val id: Int

    enum class Type(private vararg val ids: Int) {
        HELMET(LEATHER_HELMET, CHAINMAIL_HELMET, IRON_HELMET, DIAMOND_HELMET, GOLDEN_HELMET),
        CHESTPLATE(LEATHER_CHESTPLATE, CHAINMAIL_CHESTPLATE, IRON_CHESTPLATE, DIAMOND_CHESTPLATE, GOLDEN_CHESTPLATE),
        LEGGINGS(LEATHER_LEGGINGS, CHAINMAIL_LEGGINGS, IRON_LEGGINGS, DIAMOND_LEGGINGS, GOLDEN_LEGGINGS),
        BOOTS(LEATHER_BOOTS, CHAINMAIL_BOOTS, IRON_BOOTS, DIAMOND_BOOTS, GOLDEN_BOOTS),
        FOOD(
            APPLE, MUSHROOM_STEW, BREAD, PORKCHOP, COOKED_PORKCHOP, COOKIE, MELON, BEEF, COOKED_BEEF,
            CHICKEN, COOKED_CHICKEN, ROTTEN_FLESH, SPIDER_EYE, CARROT, POTATO, BAKED_POTATO, POISONOUS_POTATO,
            PUMPKIN_PIE, COOKIE, GOLDEN_CARROT
        ),
        SWORD(
            IRON_SWORD, WOODEN_SWORD, STONE_SWORD, DIAMOND_SWORD, GOLDEN_SWORD
        ),
        NONE(0);

        fun isType(id: Int): Boolean {
            for (i in ids.indices) if (ids[i] == id) return true
            return false
        }
    }

    companion object {
        const val IRON_SHOVEL: Int = 256
        const val IRON_PICKAXE: Int = 257
        const val IRON_AXE: Int = 258
        const val FLINT_AND_STEEL: Int = 259
        const val APPLE: Int = 260
        const val BOW: Int = 261
        const val ARROW: Int = 262
        const val COAL: Int = 263
        const val DIAMOND: Int = 264
        const val IRON_INGOT: Int = 265
        const val GOLD_INGOT: Int = 266
        const val IRON_SWORD: Int = 267
        const val WOODEN_SWORD: Int = 268
        const val WOODEN_SHOVEL: Int = 269
        const val WOODEN_PICKAXE: Int = 270
        const val WOODEN_AXE: Int = 271
        const val STONE_SWORD: Int = 272
        const val STONE_SHOVEL: Int = 273
        const val STONE_PICKAXE: Int = 274
        const val STONE_AXE: Int = 275
        const val DIAMOND_SWORD: Int = 276
        const val DIAMOND_SHOVEL: Int = 277
        const val DIAMOND_PICKAXE: Int = 278
        const val DIAMOND_AXE: Int = 279
        const val STICK: Int = 280
        const val BOWL: Int = 281
        const val MUSHROOM_STEW: Int = 282
        const val GOLDEN_SWORD: Int = 283
        const val GOLDEN_SHOVEL: Int = 284
        const val GOLDEN_PICKAXE: Int = 285
        const val GOLDEN_AXE: Int = 286
        const val STRING: Int = 287
        const val FEATHER: Int = 288
        const val GUNPOWDER: Int = 289
        const val WOODEN_HOE: Int = 290
        const val STONE_HOE: Int = 291
        const val IRON_HOE: Int = 292
        const val DIAMOND_HOE: Int = 293
        const val GOLDEN_HOE: Int = 294
        const val WHEAT_SEEDS: Int = 295
        const val WHEAT: Int = 296
        const val BREAD: Int = 297
        const val LEATHER_HELMET: Int = 298
        const val LEATHER_CHESTPLATE: Int = 299
        const val LEATHER_LEGGINGS: Int = 300
        const val LEATHER_BOOTS: Int = 301
        const val CHAINMAIL_HELMET: Int = 302
        const val CHAINMAIL_CHESTPLATE: Int = 303
        const val CHAINMAIL_LEGGINGS: Int = 304
        const val CHAINMAIL_BOOTS: Int = 305
        const val IRON_HELMET: Int = 306
        const val IRON_CHESTPLATE: Int = 307
        const val IRON_LEGGINGS: Int = 308
        const val IRON_BOOTS: Int = 309
        const val DIAMOND_HELMET: Int = 310
        const val DIAMOND_CHESTPLATE: Int = 311
        const val DIAMOND_LEGGINGS: Int = 312
        const val DIAMOND_BOOTS: Int = 313
        const val GOLDEN_HELMET: Int = 314
        const val GOLDEN_CHESTPLATE: Int = 315
        const val GOLDEN_LEGGINGS: Int = 316
        const val GOLDEN_BOOTS: Int = 317
        const val FLINT: Int = 318
        const val PORKCHOP: Int = 319
        const val COOKED_PORKCHOP: Int = 320
        const val PAINTING: Int = 321
        const val GOLDEN_APPLE: Int = 322
        const val SIGN: Int = 323
        const val WOODEN_DOOR: Int = 324
        const val BUCKET: Int = 325
        const val WATER_BUCKET: Int = 326
        const val LAVA_BUCKET: Int = 327
        const val MINECART: Int = 328
        const val SADDLE: Int = 329
        const val IRON_DOOR: Int = 330
        const val REDSTONE: Int = 331
        const val SNOWBALL: Int = 332
        const val BOAT: Int = 333
        const val LEATHER: Int = 334
        const val MILK_BUCKET: Int = 335
        const val BRICK: Int = 336
        const val CLAY_BALL: Int = 337
        const val REEDS: Int = 338
        const val PAPER: Int = 339
        const val BOOK: Int = 340
        const val SLIME_BALL: Int = 341
        const val CHEST_MINECART: Int = 342
        const val FURNACE_MINECART: Int = 343
        const val EGG: Int = 344
        const val COMPASS: Int = 345
        const val FISHING_ROD: Int = 346
        const val CLOCK: Int = 347
        const val GLOWSTONE_DUST: Int = 348
        const val FISH: Int = 349
        const val COOKED_FISH: Int = 350
        const val DYE: Int = 351
        const val BONE: Int = 352
        const val SUGAR: Int = 353
        const val CAKE: Int = 354
        const val BED: Int = 355
        const val REPEATER: Int = 356
        const val COOKIE: Int = 357
        const val FILLED_MAP: Int = 358
        const val SHEARS: Int = 359
        const val MELON: Int = 360
        const val PUMPKIN_SEEDS: Int = 361
        const val MELON_SEEDS: Int = 362
        const val BEEF: Int = 363
        const val COOKED_BEEF: Int = 364
        const val CHICKEN: Int = 365
        const val COOKED_CHICKEN: Int = 366
        const val ROTTEN_FLESH: Int = 367
        const val ENDER_PEARL: Int = 368
        const val BLAZE_ROD: Int = 369
        const val GHAST_TEAR: Int = 370
        const val GOLD_NUGGET: Int = 371
        const val NETHER_WART: Int = 372
        const val POTION: Int = 373
        const val GLASS_BOTTLE: Int = 374
        const val SPIDER_EYE: Int = 375
        const val FERMENTED_SPIDER_EYE: Int = 376
        const val BLAZE_POWDER: Int = 377
        const val MAGMA_CREAM: Int = 378
        const val BREWING_STAND: Int = 379
        const val CAULDRON: Int = 380
        const val ENDER_EYE: Int = 381
        const val SPECKLED_MELON: Int = 382
        const val SPAWN_EGG: Int = 383
        const val EXPERIENCE_BOTTLE: Int = 384
        const val FIRE_CHARGE: Int = 385
        const val WRITABLE_BOOK: Int = 386
        const val WRITTEN_BOOK: Int = 387
        const val EMERALD: Int = 388
        const val ITEM_FRAME: Int = 389
        const val FLOWER_POT: Int = 390
        const val CARROT: Int = 391
        const val POTATO: Int = 392
        const val BAKED_POTATO: Int = 393
        const val POISONOUS_POTATO: Int = 394
        const val MAP: Int = 395
        const val GOLDEN_CARROT: Int = 396
        const val SKULL: Int = 397
        const val CARROT_ON_A_STICK: Int = 398
        const val NETHER_STAR: Int = 399
        const val PUMPKIN_PIE: Int = 400
        const val FIREWORKS: Int = 401
        const val FIREWORK_CHARGE: Int = 402
        const val ENCHANTED_BOOK: Int = 403
        const val COMPARATOR: Int = 404
        const val NETHERBRICK: Int = 405
        const val QUARTZ: Int = 406
        const val TNT_MINECART: Int = 407
        const val HOPPER_MINECART: Int = 408
        const val IRON_HORSE_ARMOR: Int = 417
        const val GOLDEN_HORSE_ARMOR: Int = 418
        const val DIAMOND_HORSE_ARMOR: Int = 419
        const val LEAD: Int = 420
        const val NAME_TAG: Int = 421
        const val COMMAND_BLOCK_MINECART: Int = 422
        const val RECORD_13: Int = 2256
        const val RECORD_CAT: Int = 2257
        const val RECORD_BLOCKS: Int = 2258
        const val RECORD_CHIRP: Int = 2259
        const val RECORD_FAR: Int = 2260
        const val RECORD_MALL: Int = 2261
        const val RECORD_MELLOHI: Int = 2262
        const val RECORD_STAL: Int = 2263
        const val RECORD_STRAD: Int = 2264
        const val RECORD_WARD: Int = 2265
        const val RECORD_11: Int = 2266
        const val RECORD_WAIT: Int = 2267
    }
}
