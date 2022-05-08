package com.yyon.grapplinghook.common;

import com.yyon.grapplinghook.blocks.modifierblock.BlockGrappleModifier;
import com.yyon.grapplinghook.blocks.modifierblock.TileEntityGrappleModifier;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.enchantments.DoublejumpEnchantment;
import com.yyon.grapplinghook.enchantments.SlidingEnchantment;
import com.yyon.grapplinghook.enchantments.WallrunEnchantment;
import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.items.EnderStaffItem;
import com.yyon.grapplinghook.items.ForcefieldItem;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.items.LongFallBoots;
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
import com.yyon.grapplinghook.network.GrappleModifierMessage;
import com.yyon.grapplinghook.network.KeypressMessage;
import com.yyon.grapplinghook.network.PlayerMovementMessage;
import com.yyon.grapplinghook.server.ServerControllerManager;
import com.yyon.grapplinghook.server.ServerPlayerLoginEvent;
import com.yyon.grapplinghook.server.ServerStartEvent;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.yyon.grapplinghook.grapplemod.MODID;


public class CommonSetup implements ModInitializer {
    public static GrapplehookItem grapplingHookItem;
    public static EnderStaffItem enderStaffItem;
    public static ForcefieldItem forcefieldItem;

    public static BaseUpgradeItem baseUpgradeItem;
    public static DoubleUpgradeItem doubleUpgradeItem;
    public static ForcefieldUpgradeItem forcefieldUpgradeItem;
    public static MagnetUpgradeItem magnetUpgradeItem;
    public static MotorUpgradeItem motorUpgradeItem;
    public static RopeUpgradeItem ropeUpgradeItem;
    public static StaffUpgradeItem staffUpgradeItem;
    public static SwingUpgradeItem swingUpgradeItem;
    public static ThrowUpgradeItem throwUpgradeItem;
    public static LimitsUpgradeItem limitsUpgradeItem;
    public static RocketUpgradeItem rocketUpgradeItem;

    public static Item longFallBootsItem;
    
    public static WallrunEnchantment wallrunEnchantment;
    public static DoublejumpEnchantment doubleJumpEnchantment;
    public static SlidingEnchantment slidingEnchantment;

	public static Block grappleModifierBlock;
	public static BlockItem grappleModifierBlockItem;

	public static final ItemGroup tabGrapplemod =
			FabricItemGroupBuilder.build(
					new Identifier(MODID, "grapplemod"),
					() -> new ItemStack(grapplingHookItem));

	public static EntityType<GrapplehookEntity> grapplehookEntityType;

	public static BlockEntityType<TileEntityGrappleModifier> grappleModifierTileEntityType;

	public static final String HOOK_ENTITY_TYPE_ID = "grapplemod:grapplehook";

	@Override
	public void onInitialize() {
		// server network
		ServerPlayNetworking.registerGlobalReceiver(PlayerMovementMessage.IDENTIFIER, (server, player, handler, buf, responseSender) -> {
			PlayerMovementMessage m = new PlayerMovementMessage(buf);
			server.execute(() -> { m.processMessage(player); });
		});
		ServerPlayNetworking.registerGlobalReceiver(GrappleEndMessage.IDENTIFIER, (server, player, handler, buf, responseSender) -> {
			GrappleEndMessage m = new GrappleEndMessage(buf);
			server.execute(() -> { m.processMessage(player); });
		});
		ServerPlayNetworking.registerGlobalReceiver(GrappleModifierMessage.IDENTIFIER, (server, player, handler, buf, responseSender) -> {
			GrappleModifierMessage m = new GrappleModifierMessage(buf);
			server.execute(() -> { m.processMessage(player.getWorld()); });
		});
		ServerPlayNetworking.registerGlobalReceiver(KeypressMessage.IDENTIFIER, (server, player, handler, buf, responseSender) -> {
			KeypressMessage m = new KeypressMessage(buf);
			server.execute(() -> { m.processMessage(player); });
		});

		// block
		grappleModifierBlock = new BlockGrappleModifier(FabricBlockSettings.of(Material.STONE).strength(1.5f));
		Registry.register(Registry.BLOCK, new Identifier(MODID, "block_grapple_modifier"), grappleModifierBlock);

		// entity
		grappleModifierTileEntityType =
				FabricBlockEntityTypeBuilder.create(TileEntityGrappleModifier::new, grappleModifierBlock).build(null);  // you probably don't need a datafixer --> null should be fine

		grapplehookEntityType = Registry.register(
				Registry.ENTITY_TYPE,
				HOOK_ENTITY_TYPE_ID,
				EntityType.Builder.create((EntityType.EntityFactory<GrapplehookEntity>) GrapplehookEntity::new, SpawnGroup.MISC)
						.setDimensions(0.25F, 0.25F)
						.maxTrackingRange(4).trackingTickInterval(10)
						.build(HOOK_ENTITY_TYPE_ID));

		// config
		AutoConfig.register(GrappleConfig.class, Toml4jConfigSerializer::new);

		// enchantment
		wallrunEnchantment = Registry.register(
				Registry.ENCHANTMENT,
				new Identifier(MODID, "wallrunenchantment"),
				new WallrunEnchantment());
		doubleJumpEnchantment = Registry.register(
				Registry.ENCHANTMENT,
				new Identifier(MODID, "doublejumpenchantment"),
				new DoublejumpEnchantment());
		slidingEnchantment = Registry.register(
				Registry.ENCHANTMENT,
				new Identifier(MODID, "slidingenchantment"),
				new SlidingEnchantment());

		// item
		grapplingHookItem = new GrapplehookItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "grapplinghook"), grapplingHookItem);

		enderStaffItem = new EnderStaffItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "launcheritem"), enderStaffItem);
		longFallBootsItem = new LongFallBoots(ArmorMaterials.DIAMOND, 3, new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "longfallboots"), longFallBootsItem);
		forcefieldItem = new ForcefieldItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "repeller"), forcefieldItem);
		baseUpgradeItem = new BaseUpgradeItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "baseupgradeitem"), baseUpgradeItem);
		doubleUpgradeItem = new DoubleUpgradeItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "doubleupgradeitem"), doubleUpgradeItem);
		forcefieldUpgradeItem = new ForcefieldUpgradeItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "forcefieldupgradeitem"), forcefieldUpgradeItem);
		magnetUpgradeItem = new MagnetUpgradeItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "magnetupgradeitem"), magnetUpgradeItem);
		motorUpgradeItem = new MotorUpgradeItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "motorupgradeitem"), motorUpgradeItem);
		ropeUpgradeItem = new RopeUpgradeItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "ropeupgradeitem"), ropeUpgradeItem);
		staffUpgradeItem = new StaffUpgradeItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "staffupgradeitem"), staffUpgradeItem);
		swingUpgradeItem = new SwingUpgradeItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "swingupgradeitem"), swingUpgradeItem);
		throwUpgradeItem = new ThrowUpgradeItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "throwupgradeitem"), throwUpgradeItem);
		limitsUpgradeItem = new LimitsUpgradeItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "limitsupgradeitem"), limitsUpgradeItem);
		rocketUpgradeItem = new RocketUpgradeItem(new Item.Settings().group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "rocketupgradeitem"), rocketUpgradeItem);

		// We need to create a BlockItem so the player can carry this block in their hand and it can appear in the inventory
		grappleModifierBlockItem = new BlockItem(grappleModifierBlock, new Item.Settings().maxCount(64).group(tabGrapplemod));
		Registry.register(Registry.ITEM, new Identifier(MODID, "block_grapple_modifier"), grappleModifierBlockItem);

		// predefined events
		ServerLifecycleEvents.SERVER_STARTED.register(new ServerStartEvent()::onServerStarted);
		ServerPlayConnectionEvents.JOIN.register(new ServerPlayerLoginEvent()::onPlayReady);

		// server model
		serverControllerManager = new ServerControllerManager();
	}

	public static ServerControllerManager serverControllerManager;

	public static Item[] getAllItems() {
		return new Item[] {
				grapplingHookItem,
				enderStaffItem,
				forcefieldItem,
				longFallBootsItem,
				baseUpgradeItem,
				ropeUpgradeItem,
				throwUpgradeItem,
				motorUpgradeItem,
				swingUpgradeItem,
				staffUpgradeItem,
				forcefieldUpgradeItem,
				magnetUpgradeItem,
				doubleUpgradeItem,
				rocketUpgradeItem,
				limitsUpgradeItem,
		};
	}
}
