
package com.yyon.grapplinghook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.chocohead.mm.api.ClassTinkerers;
import com.yyon.grapplinghook.blocks.GrappleModifierBlock;
import com.yyon.grapplinghook.controllers.AirFrictionController;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.controllers.RepelController;
import com.yyon.grapplinghook.enchantments.DoublejumpEnchantment;
import com.yyon.grapplinghook.enchantments.SlidingEnchantment;
import com.yyon.grapplinghook.enchantments.WallrunEnchantment;
import com.yyon.grapplinghook.entities.GrappleArrow;
import com.yyon.grapplinghook.items.GrappleBow;
import com.yyon.grapplinghook.items.KeypressItem;
import com.yyon.grapplinghook.items.KeypressItem.Keys;
import com.yyon.grapplinghook.items.LauncherItem;
import com.yyon.grapplinghook.items.LongFallBoots;
import com.yyon.grapplinghook.items.Repeller;
import com.yyon.grapplinghook.items.alternategrapple.DoubleMotorHook;
import com.yyon.grapplinghook.items.alternategrapple.EnderHook;
import com.yyon.grapplinghook.items.alternategrapple.MagnetHook;
import com.yyon.grapplinghook.items.alternategrapple.MotorHook;
import com.yyon.grapplinghook.items.alternategrapple.RocketDoubleMotorHook;
import com.yyon.grapplinghook.items.alternategrapple.RocketHook;
import com.yyon.grapplinghook.items.alternategrapple.SmartHook;
import com.yyon.grapplinghook.items.upgrades.BaseUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.DoubleUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.ForcefieldUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.LimitsUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.MagnetUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.MotorUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.RocketUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.RopeUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.StaffUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.SwingUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.ThrowUpgradeItem;
import com.yyon.grapplinghook.network.GrappleEndMessage;
import com.yyon.grapplinghook.network.Packet;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

//TODO
// Pull mobs
// Attach 2 things together
// wallrun on diagonal walls
// smart motor acts erratically when aiming above hook
// key events


public class GrappleMod {

    public static final String MODID = "grapplemod";

    public static final String VERSION = "1.12.2-v12";

    public static Item grapplebowitem;
    public static Item motorhookitem;
    public static Item smarthookitem;
    public static Item doublemotorhookitem;
    public static Item rocketdoublemotorhookitem;
    public static Item enderhookitem;
    public static Item magnethookitem;
    public static Item rockethookitem;
    public static Item launcheritem;
    public static Item repelleritem;
    public static Item baseupgradeitem;
    public static Item doubleupgradeitem;
    public static Item forcefieldupgradeitem;
    public static Item magnetupgradeitem;
    public static Item motorupgradeitem;
    public static Item ropeupgradeitem;
    public static Item staffupgradeitem;
    public static Item swingupgradeitem;
    public static Item throwupgradeitem;
    public static Item limitsupgradeitem;
    public static Item rocketupgradeitem;
    public static Item longfallboots;

    public static final EnchantmentTarget GRAPPLEENCHANTS_FEET = ClassTinkerers.getEnum(EnchantmentTarget.class,
                                                                                        "GRAPPLEENCHANTS_FEET");

    public static final EntityType<GrappleArrow> GRAPPLE_ARROW = Registry
            .register(Registry.ENTITY_TYPE,
                      new Identifier(MODID, "grappleArrow"),
                      FabricEntityTypeBuilder.<GrappleArrow> create(SpawnGroup.MISC, GrappleArrow::new).build());

    public static WallrunEnchantment wallrunEnchantment;
    public static DoublejumpEnchantment doubleJumpEnchantment;
    public static SlidingEnchantment slidingEnchantment;

    public static Object instance;

    public static Map<Integer, GrappleController> controllers = new HashMap<>(); // client side
    public static Map<BlockPos, GrappleController> controllerPos = new HashMap<>();
    public static Set<Integer> attached = new HashSet<>(); // server side
    public static Map<Integer, HashSet<GrappleArrow>> allarrows = new HashMap<>(); // server side

    private static int controllerid = 0;

    public static int GRAPPLEID = controllerid++;
    public static int REPELID = controllerid++;
    public static int AIRID = controllerid++;

    public static boolean anyBlocks = true;
    public static List<Block> grapplingblocks;
    public static boolean removeBlocks = false;
    public static Block grappleModifierBlock;
    public static BlockItem itemBlockGrappleModifier;

//	public ResourceLocation resourceLocation;

    public enum UpgradeCategories {
        ROPE("Rope", ropeupgradeitem),
        THROW("Hook Thrower", throwupgradeitem),
        MOTOR("Motor", motorupgradeitem),
        SWING("Swing Speed", swingupgradeitem),
        STAFF("Ender Staff", staffupgradeitem),
        FORCEFIELD("Forcefield", forcefieldupgradeitem),
        MAGNET("Hook Magnet", magnetupgradeitem),
        DOUBLE("Double Hook", doubleupgradeitem),
        LIMITS("Limits", limitsupgradeitem),
        ROCKET("Rocket", rocketupgradeitem);

        public String description;

        public Item item;

        private UpgradeCategories(String desc, Item item) {
            this.description = desc;
            this.item = item;
        }
    }

    public static final ItemGroup tabGrapplemod = FabricItemGroupBuilder.create(new Identifier(MODID, "tabGrapplemod"))
            .appendItems(stacks -> {
                List<Item> items = Arrays.asList(getAllItems());
                Collections.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(Item arg0, Item arg1) {
                        return getIndex(arg0) - getIndex(arg1);
                    }

                    int getIndex(Item item) {
                        int i = 0;
                        for (Item item2 : getAllItems()) {
                            if (item == item2) {
                                return i;
                            }
                            i++;
                        }
                        return i;
                    }
                });
                items.forEach(i -> stacks.add(new ItemStack(i)));
            })
            .build();

//	@SidedProxy(clientSide="com.yyon.grapplinghook.ClientProxyClass", serverSide="com.yyon.grapplinghook.ServerProxyClass")
    public static CommonProxyClass proxy;

    public void registerRenderers() {
    }

    public void generateNether(World world, Random random, int chunkX, int chunkZ) {
    }

    public void generateSurface(World world, Random random, int chunkX, int chunkZ) {
    }

    public int addFuel(ItemStack fuel) {
        return 0;
    }

//	public void serverLoad(FMLServerStartingEvent event){
//	}

    public static void updateGrapplingBlocks() {
        String s = GrappleConfig.getconf().grapplingBlocks;
        if (s.equals("any") || s.equals("")) {
            s = GrappleConfig.getconf().grapplingNonBlocks;
            if (s.equals("none") || s.equals("")) {
                anyBlocks = true;
            } else {
                anyBlocks = false;
                removeBlocks = true;
            }
        } else {
            anyBlocks = false;
            removeBlocks = false;
        }

        if (!anyBlocks) {
            String[] blockstr = s.split(",");

            grapplingblocks = new ArrayList<>();

            for (String str : blockstr) {
                str = str.trim();
                String modid;
                String name;
                if (str.contains(":")) {
                    String[] splitstr = str.split(":");
                    modid = splitstr[0];
                    name = splitstr[1];
                } else {
                    modid = "minecraft";
                    name = str;
                }

                Block b = new Block(FabricBlockSettings.of(Material.METAL).hardness(4.0f)); // TODO
                Registry.register(Registry.BLOCK, new Identifier(modid, name), b);

                grapplingblocks.add(b);
            }
        }
    }

//	@SubscribeEvent
//    public void registerItems(/* RegistryEvent.Register<Item> event */) {
//		System.out.println("REGISTERING ITEMS");
//		System.out.println(grapplebowitem);
//    }

    public static Item[] getAllItems() {
        return new Item[] {
            grapplebowitem, itemBlockGrappleModifier, enderhookitem, magnethookitem, rockethookitem, motorhookitem,
            smarthookitem, doublemotorhookitem, rocketdoublemotorhookitem, launcheritem, repelleritem, longfallboots,
            baseupgradeitem, ropeupgradeitem, throwupgradeitem, motorupgradeitem, swingupgradeitem, staffupgradeitem,
            forcefieldupgradeitem, magnetupgradeitem, doubleupgradeitem, rocketupgradeitem, limitsupgradeitem,
        };
    }

//	@EventHandler
    public void preInit(/* FMLPreInitializationEvent event */) {
//		System.out.println("PREINIT!!!");
        grapplebowitem = new GrappleBow();
        motorhookitem = new MotorHook();
        smarthookitem = new SmartHook();
        doublemotorhookitem = new DoubleMotorHook();
        rocketdoublemotorhookitem = new RocketDoubleMotorHook();
        enderhookitem = new EnderHook();
        magnethookitem = new MagnetHook();
        rockethookitem = new RocketHook();
        launcheritem = new LauncherItem();
        longfallboots = new LongFallBoots(ArmorMaterials.DIAMOND, 3);
        repelleritem = new Repeller();
        baseupgradeitem = new BaseUpgradeItem();
        doubleupgradeitem = new DoubleUpgradeItem();
//	    doubleupgradeitem.setContainerItem(doubleupgradeitem);
        forcefieldupgradeitem = new ForcefieldUpgradeItem();
//	    forcefieldupgradeitem.setContainerItem(forcefieldupgradeitem);
        magnetupgradeitem = new MagnetUpgradeItem();
//	    magnetupgradeitem.setContainerItem(magnetupgradeitem);
        motorupgradeitem = new MotorUpgradeItem();
//	    motorupgradeitem.setContainerItem(motorupgradeitem);
        ropeupgradeitem = new RopeUpgradeItem();
//	    ropeupgradeitem.setContainerItem(ropeupgradeitem);
        staffupgradeitem = new StaffUpgradeItem();
//	    staffupgradeitem.setContainerItem(staffupgradeitem);
        swingupgradeitem = new SwingUpgradeItem();
//	    swingupgradeitem.setContainerItem(swingupgradeitem);
        throwupgradeitem = new ThrowUpgradeItem();
//	    throwupgradeitem.setContainerItem(throwupgradeitem);
        limitsupgradeitem = new LimitsUpgradeItem();
//	    limitsupgradeitem.setContainerItem(limitsupgradeitem);
        rocketupgradeitem = new RocketUpgradeItem();
//	    rocketupgradeitem.setContainerItem(rocketupgradeitem);

        Registry.register(Registry.ITEM, new Identifier(MODID, "grapplinghook"), grapplebowitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "motorhook"), motorhookitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "smarthook"), smarthookitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "doublemotorhook"), doublemotorhookitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "rocketdoublemotorhook"), rocketdoublemotorhookitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "enderhook"), enderhookitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "magnethook"), magnethookitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "rockethook"), rockethookitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "launcheritem"), launcheritem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "longfallboots"), longfallboots);
        Registry.register(Registry.ITEM, new Identifier(MODID, "repeller"), repelleritem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "baseupgradeitem"), baseupgradeitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "doubleupgradeitem"), doubleupgradeitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "forcefieldupgradeitem"), forcefieldupgradeitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "magnetupgradeitem"), magnetupgradeitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "motorupgradeitem"), motorupgradeitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "ropeupgradeitem"), ropeupgradeitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "staffupgradeitem"), staffupgradeitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "swingupgradeitem"), swingupgradeitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "throwupgradeitem"), throwupgradeitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "limitsupgradeitem"), limitsupgradeitem);
        Registry.register(Registry.ITEM, new Identifier(MODID, "rocketupgradeitem"), rocketupgradeitem);

        wallrunEnchantment = new WallrunEnchantment();
        doubleJumpEnchantment = new DoublejumpEnchantment();
        slidingEnchantment = new SlidingEnchantment();

        Registry.register(Registry.ENCHANTMENT, new Identifier(MODID, "wallrunenchantment"), wallrunEnchantment);
        Registry.register(Registry.ENCHANTMENT, new Identifier(MODID, "doublejumpenchantment"), doubleJumpEnchantment);
        Registry.register(Registry.ENCHANTMENT, new Identifier(MODID, "slidingenchantment"), slidingEnchantment);

//		System.out.println(grapplebowitem);
//		resourceLocation = new ResourceLocation(GrappleMod.MODID, "grapplemod");
//		network = NetworkRegistry.INSTANCE.newSimpleChannel("grapplemodchannel");
//        byte id = 0;

        grappleModifierBlock = new GrappleModifierBlock(FabricBlockSettings.of(Material.METAL).hardness(10F));
        Registry.register(Registry.BLOCK, new Identifier(MODID, "block_grapple_modifier"), grappleModifierBlock);

        itemBlockGrappleModifier = new BlockItem(grappleModifierBlock, null);

        // Each of your tile entities needs to be registered with a name that is
        // unique to your mod.
        Registry.register(Registry.ITEM, new Identifier(MODID, "tile_entity_grapple_modifier"), itemBlockGrappleModifier);

//	    MinecraftForge.EVENT_BUS.register(this);
//		proxy.preInit(event);

        tabGrapplemod.setEnchantments(GRAPPLEENCHANTS_FEET);
    }

//	@EventHandler
//	public void Init(FMLInitializationEvent event) {
//		proxy.init(event, this);
//	}

//	@EventHandler
    public void postInit(/* FMLPostInitializationEvent event */) {
//		proxy.postInit(event);

        GrappleMod.updateGrapplingBlocks();
    }

//	int entityID = 0;
//	public void registerEntity(Class<? extends Entity> entityClass, String name)
//	{
//		Registry.register(Registry.ENTITY_TYPE, new Identifier(MODID, name), entityClass);
//	}

    public static void registerController(int entityId, GrappleController controller) {
        if (controllers.containsKey(entityId)) {
            controllers.get(entityId).unattach();
        }

        controllers.put(entityId, controller);
    }

    public static void unregisterController(int entityId) {
        controllers.remove(entityId);
    }

    public static void receiveGrappleDetach(int id) {
        GrappleController controller = controllers.get(id);
        if (controller != null) {
            controller.receiveGrappleDetach();
        } else {
            System.out.println("Couldn't find controller");
        }
    }

    public static void receiveGrappleDetachHook(int id, int hookid) {
        GrappleController controller = controllers.get(id);
        if (controller != null) {
            controller.receiveGrappleDetachHook(hookid);
        } else {
            System.out.println("Couldn't find controller");
        }
    }

    public static void receiveEnderLaunch(int id, double x, double y, double z) {
        GrappleController controller = controllers.get(id);
        if (controller != null) {
            controller.receiveEnderLaunch(x, y, z);
        } else {
            System.out.println("Couldn't find controller");
        }
    }

    public static void sendToCorrectClient(Packet message, int playerid, World w) {
        Entity entity = w.getEntityById(playerid);
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            player.networkHandler.sendPacket(new CustomPayloadS2CPacket(message.getIdentifier(), message.toBytes()));
        } else {
            System.out.println("ERROR! couldn't find player");
        }
    }

    public static GrappleController createControl(int id,
                                                  int arrowid,
                                                  int entityid,
                                                  World world,
                                                  Vec pos,
                                                  BlockPos blockpos,
                                                  GrappleCustomization custom) {

        GrappleArrow arrow = null;
        Entity arrowentity = world.getEntityById(arrowid);
        if (arrowentity != null && arrowentity instanceof GrappleArrow) {
            arrow = (GrappleArrow) arrowentity;
        }

        boolean multi = (custom != null) && (custom.doublehook);

        GrappleController currentcontroller = controllers.get(entityid);
        if (currentcontroller != null && !(multi && currentcontroller.custom != null && currentcontroller.custom.doublehook)) {
            currentcontroller.unattach();
        }

//        System.out.println(blockpos);

        GrappleController control = null;
        if (id == GRAPPLEID) {
            if (!multi) {
                control = new GrappleController(arrowid, entityid, world, pos, id, custom);
            } else {
                control = GrappleMod.controllers.get(entityid);
                boolean created = false;
                if (control != null && control.getClass().equals(GrappleController.class)) {
                    GrappleController c = control;
                    if (control.custom.doublehook) {
                        if (arrow != null) {
                            GrappleArrow multiarrow = (GrappleArrow) arrowentity;
                            created = true;
                            c.addArrow(multiarrow);
                        }
                    }
                }
                if (!created) {
// System.out.println("Couldn't create"); grapplemod.removesubarrow(arrowid);
                    control = new GrappleController(arrowid, entityid, world, pos, id, custom);
                }
            }
        } else if (id == REPELID) {
            control = new RepelController(arrowid, entityid, world, pos, id);
        } else if (id == AIRID) {
            control = new AirFrictionController(arrowid, entityid, world, pos, id, custom);
        }
        if (blockpos != null && control != null) {
            GrappleMod.controllerPos.put(blockpos, control);
        }

        return control;
    }

    public static void removesubarrow(int id) {
        HashSet<Integer> arrowIds = new HashSet<>();
        arrowIds.add(id);
        new GrappleEndMessage(-1, arrowIds).send();
    }

    public static void receiveGrappleEnd(int id, World world, Set<Integer> arrowIds) {
        if (GrappleMod.attached.contains(id)) {
            GrappleMod.attached.remove(new Integer(id));
        } else {
        }

        for (int arrowid : arrowIds) {
            Entity grapple = world.getEntityById(arrowid);
            if (grapple instanceof GrappleArrow) {
                ((GrappleArrow) grapple).removeServer();
            } else {

            }
        }

        Entity entity = world.getEntityById(id);
        if (entity != null) {
            entity.fallDistance = 0;
        }

        GrappleMod.removeallmultihookarrows(id);
    }

    public static void addarrow(int id, GrappleArrow arrow) {
        if (!allarrows.containsKey(id)) {
            allarrows.put(id, new HashSet<GrappleArrow>());
        }
        allarrows.get(id).add(arrow);
    }

    public static void removeallmultihookarrows(int id) {
        if (!allarrows.containsKey(id)) {
            allarrows.put(id, new HashSet<GrappleArrow>());
        }
        for (GrappleArrow arrow : allarrows.get(id)) {
            if (arrow != null && arrow.isAlive()) {
                arrow.removeServer();
            }
        }
        allarrows.put(id, new HashSet<GrappleArrow>());
    }

    public static CompoundTag getstackcompound(ItemStack stack, String key) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }
        CompoundTag basecompound = stack.getTag();
        if (basecompound.contains(key, 10)) {
            return basecompound.getCompound(key);
        } else {
            CompoundTag nbttagcompound = new CompoundTag();
            stack.putSubTag(key, nbttagcompound);
            return nbttagcompound;
        }
    }

//	@SubscribeEvent
    public void onConfigChanged(/*ConfigChangedEvent.OnConfigChangedEvent eventArgs*/) {
//	    if (eventArgs.getModID().equals("grapplemod")) {
        System.out.println("grapplemod config updated");
//			ConfigManager.sync("grapplemod", Type.INSTANCE);

        GrappleMod.updateGrapplingBlocks();
//		}
    }

    public static void receiveKeypress(PlayerEntity player, Keys key, boolean isDown) {
        if (player != null) {
            ItemStack stack = player.getMainHandStack();
            if (stack != null) {
                Item item = stack.getItem();
                if (item instanceof KeypressItem) {
                    if (isDown) {
                        ((KeypressItem) item).onCustomKeyDown(stack, player, key, true);
                    } else {
                        ((KeypressItem) item).onCustomKeyUp(stack, player, key, true);
                    }
                    return;
                }
            }

            stack = player.getOffHandStack();
            if (stack != null) {
                Item item = stack.getItem();
                if (item instanceof KeypressItem) {
                    if (isDown) {
                        ((KeypressItem) item).onCustomKeyDown(stack, player, key, false);
                    } else {
                        ((KeypressItem) item).onCustomKeyUp(stack, player, key, false);
                    }
                    return;
                }
            }
        }
    }
}
