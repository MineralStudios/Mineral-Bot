package net.minecraft.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.jdt.annotation.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class EntityList {
    private static final Logger logger = LogManager.getLogger(EntityList.class);

    /**
     * Provides a mapping between entity classes and a string
     */
    private static Map<String, Class<? extends Entity>> stringToClassMapping = new Object2ObjectOpenHashMap<>();

    /**
     * Provides a mapping between a string and an entity classes
     */
    private static Map<Class<? extends Entity>, String> classToStringMapping = new Object2ObjectOpenHashMap<>();

    /**
     * provides a mapping between an entityID and an Entity Class
     */
    private static Int2ObjectOpenHashMap<Class<? extends Entity>> iDtoClassMapping = new Int2ObjectOpenHashMap<>();

    /**
     * provides a mapping between an Entity Class and an entity ID
     */
    private static Object2IntOpenHashMap<Class<? extends Entity>> classToIDMapping = new Object2IntOpenHashMap<>();

    /**
     * Maps entity names to their numeric identifiers
     */
    private static Object2IntOpenHashMap<String> stringToIDMapping = new Object2IntOpenHashMap<>();

    /**
     * This is a HashMap of the Creative Entity Eggs/Spawners.
     */
    public static Int2ObjectLinkedOpenHashMap<EntityList.EntityEggInfo> entityEggs = new Int2ObjectLinkedOpenHashMap<>();

    /**
     * adds a mapping between Entity classes and both a string representation and an
     * ID
     */
    private static void addMapping(Class<? extends Entity> clazz, String name, int id) {
        if (stringToClassMapping.containsKey(name))
            throw new IllegalArgumentException("ID is already registered: " + name);
        if (iDtoClassMapping.containsKey(id))
            throw new IllegalArgumentException("ID is already registered: " + id);

        stringToClassMapping.put(name, clazz);
        classToStringMapping.put(clazz, name);
        iDtoClassMapping.put(id, clazz);
        classToIDMapping.put(clazz, id);
        stringToIDMapping.put(name, id);
    }

    /**
     * Adds a entity mapping with egg info.
     */
    private static void addMapping(Class<? extends Entity> clazz, String name, int id,
                                   int p_75614_3_,
                                   int p_75614_4_) {
        addMapping(clazz, name, id);
        entityEggs.put(id, new EntityList.EntityEggInfo(id, p_75614_3_, p_75614_4_));
    }

    /**
     * Create a new instance of an entity in the world by using the entity name.
     */
    public static Entity createEntityByName(String p_75620_0_, World p_75620_1_) {
        Entity var2 = null;

        try {
            Class<? extends Entity> var3 = stringToClassMapping.get(p_75620_0_);

            if (var3 != null)
                var2 = var3.getConstructor(new Class[]{World.class})
                        .newInstance(new Object[]{p_75620_1_});

        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return var2;
    }

    /**
     * create a new instance of an entity from NBT store
     */
    public static Entity createEntityFromNBT(NBTTagCompound p_75615_0_, World p_75615_1_) {
        Entity var2 = null;

        if ("Minecart".equals(p_75615_0_.getString("id"))) {
            switch (p_75615_0_.getInteger("Type")) {
                case 0:
                    p_75615_0_.setString("id", "MinecartRideable");
                    break;

                case 1:
                    p_75615_0_.setString("id", "MinecartChest");
                    break;

                case 2:
                    p_75615_0_.setString("id", "MinecartFurnace");
            }

            p_75615_0_.removeTag("Type");
        }

        try {
            Class<? extends Entity> var3 = stringToClassMapping.get(p_75615_0_.getString("id"));

            if (var3 != null) {
                var2 = var3.getConstructor(new Class[]{World.class})
                        .newInstance(new Object[]{p_75615_1_});
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        if (var2 != null) {
            var2.readFromNBT(p_75615_0_);
        } else {
            logger.warn("Skipping Entity with id " + p_75615_0_.getString("id"));
        }

        return var2;
    }

    /**
     * Create a new instance of an entity in the world by using an entity ID.
     */
    @Nullable
    public static Entity createEntityByID(int typeId, World world) {
        Entity var2 = null;

        try {
            Class<? extends Entity> var3 = getClassFromID(typeId);

            // if (var3 == null)
            // System.out.println("Entity ID: " + typeId);

            if (var3 != null)
                var2 = var3.getConstructor(new Class[]{World.class})
                        .newInstance(new Object[]{world});

        } catch (Exception var4) {
            var4.printStackTrace();
        }

        if (var2 == null) {
            logger.warn("Skipping Entity with id " + typeId);
        }

        return var2;
    }

    /**
     * gets the entityID of a specific entity
     */
    public static int getEntityID(Entity p_75619_0_) {
        Class<? extends Entity> var1 = p_75619_0_.getClass();
        return classToIDMapping.containsKey(var1) ? classToIDMapping.getInt(var1) : 0;
    }

    /**
     * Return the class assigned to this entity ID.
     */
    public static Class<? extends Entity> getClassFromID(int p_90035_0_) {
        return iDtoClassMapping.get(p_90035_0_);
    }

    /**
     * Gets the string representation of a specific entity.
     */
    public static String getEntityString(Entity p_75621_0_) {
        return (String) classToStringMapping.get(p_75621_0_.getClass());
    }

    /**
     * Finds the class using IDtoClassMapping and classToStringMapping
     */
    public static String getStringFromID(int p_75617_0_) {
        Class<? extends Entity> var1 = getClassFromID(p_75617_0_);
        return var1 != null ? (String) classToStringMapping.get(var1) : null;
    }

    public static void func_151514_a() {
    }

    public static Set<String> func_151515_b() {
        return Collections.unmodifiableSet(stringToIDMapping.keySet());
    }

    static {
        addMapping(EntityItem.class, "Item", 1);
        addMapping(EntityXPOrb.class, "XPOrb", 2);
        addMapping(EntityLeashKnot.class, "LeashKnot", 8);
        addMapping(EntityPainting.class, "Painting", 9);
        addMapping(EntityArrow.class, "Arrow", 10);
        addMapping(EntitySnowball.class, "Snowball", 11);
        addMapping(EntityLargeFireball.class, "Fireball", 12);
        addMapping(EntitySmallFireball.class, "SmallFireball", 13);
        addMapping(EntityEnderPearl.class, "ThrownEnderpearl", 14);
        addMapping(EntityEnderEye.class, "EyeOfEnderSignal", 15);
        addMapping(EntityPotion.class, "ThrownPotion", 16);
        addMapping(EntityExpBottle.class, "ThrownExpBottle", 17);
        addMapping(EntityItemFrame.class, "ItemFrame", 18);
        addMapping(EntityWitherSkull.class, "WitherSkull", 19);
        addMapping(EntityTNTPrimed.class, "PrimedTnt", 20);
        addMapping(EntityFallingBlock.class, "FallingSand", 21);
        addMapping(EntityFireworkRocket.class, "FireworksRocketEntity", 22);
        addMapping(EntityBoat.class, "Boat", 41);
        addMapping(EntityMinecartEmpty.class, "MinecartRideable", 42);
        addMapping(EntityMinecartChest.class, "MinecartChest", 43);
        addMapping(EntityMinecartFurnace.class, "MinecartFurnace", 44);
        addMapping(EntityMinecartTNT.class, "MinecartTNT", 45);
        addMapping(EntityMinecartHopper.class, "MinecartHopper", 46);
        addMapping(EntityMinecartMobSpawner.class, "MinecartSpawner", 47);
        addMapping(EntityMinecartCommandBlock.class, "MinecartCommandBlock", 40);
        addMapping(EntityLiving.class, "Mob", 48);
        addMapping(EntityMob.class, "Monster", 49);
        addMapping(EntityCreeper.class, "Creeper", 50, 894731, 0);
        addMapping(EntitySkeleton.class, "Skeleton", 51, 12698049, 4802889);
        addMapping(EntitySpider.class, "Spider", 52, 3419431, 11013646);
        addMapping(EntityGiantZombie.class, "Giant", 53);
        addMapping(EntityZombie.class, "Zombie", 54, 44975, 7969893);
        addMapping(EntitySlime.class, "Slime", 55, 5349438, 8306542);
        addMapping(EntityGhast.class, "Ghast", 56, 16382457, 12369084);
        addMapping(EntityPigZombie.class, "PigZombie", 57, 15373203, 5009705);
        addMapping(EntityEnderman.class, "Enderman", 58, 1447446, 0);
        addMapping(EntityCaveSpider.class, "CaveSpider", 59, 803406, 11013646);
        addMapping(EntitySilverfish.class, "Silverfish", 60, 7237230, 3158064);
        addMapping(EntityBlaze.class, "Blaze", 61, 16167425, 16775294);
        addMapping(EntityMagmaCube.class, "LavaSlime", 62, 3407872, 16579584);
        addMapping(EntityDragon.class, "EnderDragon", 63);
        addMapping(EntityWither.class, "WitherBoss", 64);
        addMapping(EntityBat.class, "Bat", 65, 4996656, 986895);
        addMapping(EntityWitch.class, "Witch", 66, 3407872, 5349438);
        addMapping(EntityPig.class, "Pig", 90, 15771042, 14377823);
        addMapping(EntitySheep.class, "Sheep", 91, 15198183, 16758197);
        addMapping(EntityCow.class, "Cow", 92, 4470310, 10592673);
        addMapping(EntityChicken.class, "Chicken", 93, 10592673, 16711680);
        addMapping(EntitySquid.class, "Squid", 94, 2243405, 7375001);
        addMapping(EntityWolf.class, "Wolf", 95, 14144467, 13545366);
        addMapping(EntityMooshroom.class, "MushroomCow", 96, 10489616, 12040119);
        addMapping(EntitySnowman.class, "SnowMan", 97);
        addMapping(EntityOcelot.class, "Ozelot", 98, 15720061, 5653556);
        addMapping(EntityIronGolem.class, "VillagerGolem", 99);
        addMapping(EntityHorse.class, "EntityHorse", 100, 12623485, 15656192);
        addMapping(EntityVillager.class, "Villager", 120, 5651507, 12422002);
        addMapping(EntityEnderCrystal.class, "EnderCrystal", 200);
    }

    public static class EntityEggInfo {
        public final int spawnedID, primaryColor, secondaryColor;
        public final StatBase field_151512_d, field_151513_e;

        public EntityEggInfo(int p_i1583_1_, int p_i1583_2_, int p_i1583_3_) {
            this.spawnedID = p_i1583_1_;
            this.primaryColor = p_i1583_2_;
            this.secondaryColor = p_i1583_3_;
            this.field_151512_d = StatList.func_151182_a(this);
            this.field_151513_e = StatList.func_151176_b(this);
        }
    }
}
