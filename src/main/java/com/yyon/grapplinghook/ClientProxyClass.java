package com.yyon.grapplinghook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import com.yyon.grapplinghook.blocks.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.controllers.AirFrictionController;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.entities.GrappleArrowRenderer;
import com.yyon.grapplinghook.items.GrappleBow;
import com.yyon.grapplinghook.items.KeypressItem;
import com.yyon.grapplinghook.items.LauncherItem;
import com.yyon.grapplinghook.items.Repeller;
import com.yyon.grapplinghook.network.DetachSingleHookMessage;
import com.yyon.grapplinghook.network.GrappleAttachMessage;
import com.yyon.grapplinghook.network.GrappleAttachPosMessage;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import com.yyon.grapplinghook.network.LoggedInMessage;
import com.yyon.grapplinghook.network.SegmentMessage;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;


public class ClientProxyClass implements ClientModInitializer {

    public boolean prevKeys[] = {false, false, false, false, false};
	
	public Map<Integer, Long> enderLaunchTimer = new HashMap<>();
	
	public double rocketFuel = 1.0;
	public double rocketIncreaseTick = 0.0;
	public double rocketDecreaseTick = 0.0;
	
	@Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE
                .register(GrappleMod.GRAPPLE_ARROW,
                          (dispatcher, context) -> new GrappleArrowRenderer(dispatcher,
                                                                            Items.IRON_PICKAXE,
                                                                            MinecraftClient.getInstance().getItemRenderer()));
		
	    Identifier itemIdentifier = new Identifier("grapplemod:block_grapple_modifier", "inventory");
//	    final int DEFAULT_ITEM_SUBTYPE = 0;
//		ModelLoader.setCustomIdentifier(GrappleMod.itemBlockGrappleModifier, DEFAULT_ITEM_SUBTYPE, itemIdentifier);
	}
	
	public Identifier grapplinghookloc = new Identifier("grapplemod:grapplinghook", "inventory");
	public Identifier hookshotloc = new Identifier("grapplemod:hookshot", "inventory");
	public Identifier smarthookloc = new Identifier("grapplemod:smarthook", "inventory");
	public Identifier smarthookropeloc = new Identifier("grapplemod:smarthookrope", "inventory");
	public Identifier enderhookloc = new Identifier("grapplemod:enderhook", "inventory");
	public Identifier magnetbowloc = new Identifier("grapplemod:magnetbow", "inventory");
	public Identifier ropeloc = new Identifier("grapplemod:rope", "inventory");
	public Identifier hookshotropeloc = new Identifier("grapplemod:hookshotrope", "inventory");
	public Identifier repellerloc = new Identifier("grapplemod:repeller", "inventory");
	public Identifier repelleronloc = new Identifier("grapplemod:repelleron", "inventory");
	public Identifier multihookloc = new Identifier("grapplemod:multihook", "inventory");
	public Identifier multihookropeloc = new Identifier("grapplemod:multihookrope", "inventory");
	public Identifier odmloc = new Identifier("grapplemod:odm", "inventory");
	public Identifier odmropeloc = new Identifier("grapplemod:odmrope", "inventory");
	public Identifier rocketloc = new Identifier("grapplemod:rocket", "inventory");
	public Identifier rocketropeloc = new Identifier("grapplemod:rocketrope", "inventory");
	
//	private void setgrapplebowtextures(Item item, final Identifier notinusetexture, final Identifier inusetexture) {
//		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
//			@Override
//			public Identifier getModelLocation(ItemStack stack) {
//		    	if (ClientProxyClass.isActive(stack)) {
//		    		return inusetexture;
//		    	}
//		    	return notinusetexture;
//			}
//		});
//		BakedModel.registerItemVariants(item, notinusetexture);
//		BakedModel.registerItemVariants(item, inusetexture);
//	}
	
	private void registerItemModels() {
//		setgrapplebowtextures(grapplemod.grapplebowitem, grapplinghookloc, ropeloc);
//		registerItemModel(GrappleMod.launcheritem);
//		registerItemModel(GrappleMod.longfallboots);
//		setgrapplebowtextures(GrappleMod.repelleritem, repellerloc, repelleronloc);
//		registerItemModel(GrappleMod.baseupgradeitem);
//		registerItemModel(GrappleMod.doubleupgradeitem);
//		registerItemModel(GrappleMod.forcefieldupgradeitem);
//		registerItemModel(GrappleMod.magnetupgradeitem);
//		registerItemModel(GrappleMod.motorupgradeitem);
//		registerItemModel(GrappleMod.ropeupgradeitem);
//		registerItemModel(GrappleMod.staffupgradeitem);
//		registerItemModel(GrappleMod.swingupgradeitem);
//		registerItemModel(GrappleMod.throwupgradeitem);
//		registerItemModel(GrappleMod.limitsupgradeitem);
//		registerItemModel(GrappleMod.rocketupgradeitem);
		
//		ItemMeshDefinition itemmeshdefinition = new ItemMeshDefinition() {
//			@Override
//			public Identifier getModelLocation(ItemStack stack) {
//				boolean active = !ClientProxyClass.isActive(stack);
//		    	if (stack.hasTag()) {
//		    		CompoundTag compound = stack.getTag();
//		    		if (compound.getBoolean("rocket")) {
//		    			if (compound.getBoolean("doublehook")) {
//		    				return active ? odmloc : odmropeloc;
//		    			} else {
//		    				return active ? rocketloc : rocketropeloc;
//		    			}
//		    		}
//		    		if (compound.getBoolean("motor")) {
//		    			if (compound.getBoolean("doublehook")) {
//		    				return active ? multihookloc : multihookropeloc;
//		    			}
//		    			if (compound.getBoolean("smartmotor")) {
//		    				return active ? smarthookloc : smarthookropeloc;
//		    			}
//		    			return active ? hookshotloc : hookshotropeloc;
//		    		}
//		    		if (compound.getBoolean("enderstaff")) {
//		    			return active ? enderhookloc : ropeloc;
//		    		}
//		    		if (compound.getBoolean("repel") || compound.getBoolean("attract")) {
//		    			return active ? magnetbowloc : ropeloc;
//		    		}
//		    	}
//
//		    	return active ? grapplinghookloc : ropeloc;
//			}
//		};
		
//		ModelLoader.setCustomMeshDefinition(GrappleMod.grapplebowitem, itemmeshdefinition);
//		ModelLoader.setCustomMeshDefinition(GrappleMod.motorhookitem, itemmeshdefinition);
//		ModelLoader.setCustomMeshDefinition(GrappleMod.smarthookitem, itemmeshdefinition);
//		ModelLoader.setCustomMeshDefinition(GrappleMod.doublemotorhookitem, itemmeshdefinition);
//		ModelLoader.setCustomMeshDefinition(GrappleMod.rocketdoublemotorhookitem, itemmeshdefinition);
//		ModelLoader.setCustomMeshDefinition(GrappleMod.enderhookitem, itemmeshdefinition);
//		ModelLoader.setCustomMeshDefinition(GrappleMod.magnethookitem, itemmeshdefinition);
//		ModelLoader.setCustomMeshDefinition(GrappleMod.rockethookitem, itemmeshdefinition);
//		for (Identifier loc : new Identifier[] {multihookloc, multihookropeloc, smarthookloc, smarthookropeloc, hookshotloc, hookshotropeloc, enderhookloc, magnetbowloc, grapplinghookloc, ropeloc, odmloc, odmropeloc, rocketloc, rocketropeloc}) {
//			BakedModel.registerItemVariants(GrappleMod.grapplebowitem, loc);
//			BakedModel.registerItemVariants(GrappleMod.motorhookitem, loc);
//			BakedModel.registerItemVariants(GrappleMod.smarthookitem, loc);
//			BakedModel.registerItemVariants(GrappleMod.doublemotorhookitem, loc);
//			BakedModel.registerItemVariants(GrappleMod.rocketdoublemotorhookitem, loc);
//			BakedModel.registerItemVariants(GrappleMod.enderhookitem, loc);
//			BakedModel.registerItemVariants(GrappleMod.magnethookitem, loc);
//			BakedModel.registerItemVariants(GrappleMod.rockethookitem, loc);
//		}
	}

//	@SubscribeEvent
//	public void registerAllModels(/*final ModelRegistryEvent event*/) {
//		System.out.println("REGISTERING ALL MODELS!!!!!!!!!!!!!");
//		this.registerItemModels();
//	}
	
//	private void registerItemModel(Item item) {
//		registerItemModel(item, item.getName().toString());
//	}

//	private void registerItemModel(Item item, String modelLocation) {
//		final Identifier fullModelLocation = new Identifier(modelLocation, "inventory");
//		BakeryModel.registerItemVariants(item, fullModelLocation); // Ensure the custom model is loaded and prevent the default model from being loaded
//		ModelLoader.setCustomIdentifier(item, 0, fullModelLocation);
//	}
	
	
	public static List<KeyBinding> keyBindings = new ArrayList<>();
	
	public static KeyBinding createKeyBinding(String desc, int key, String category) {
		KeyBinding k = new NonConflictingKeyBinding(desc, key, category);
		keyBindings.add(k);
		return k;
	}
	
	public static KeyBinding keyBothThrow = createKeyBinding("key.boththrow.desc", -99, "key.grapplemod.category");
	public static KeyBinding keyLeftThrow = createKeyBinding("key.leftthrow.desc", 0, "key.grapplemod.category");
	public static KeyBinding keyRightThrow = createKeyBinding("key.rightthrow.desc", 0, "key.grapplemod.category");
	public static KeyBinding keyMotorOnOff = createKeyBinding("key.motoronoff.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category");
	public static KeyBinding keyJumpAndDetach = createKeyBinding("key.jumpanddetach.desc", GLFW.GLFW_KEY_SPACE, "key.grapplemod.category");
	public static KeyBinding keySlow = createKeyBinding("key.slow.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category");
	public static KeyBinding keyClimb = createKeyBinding("key.climb.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category");
	public static KeyBinding keyClimbUp = createKeyBinding("key.climbup.desc", GLFW.GLFW_KEY_W, "key.grapplemod.category");
	public static KeyBinding keyClimbDown = createKeyBinding("key.climbdown.desc", GLFW.GLFW_KEY_S, "key.grapplemod.category");
	public static KeyBinding keyEnderLaunch = createKeyBinding("key.enderlaunch.desc", -100, "key.grapplemod.category");
	public static KeyBinding keyRocket = createKeyBinding("key.rocket.desc", -100, "key.grapplemod.category");
	public static KeyBinding keySlide = createKeyBinding("key.slide.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category");


//	@Override
	public void init(GrappleMod grappleModInst) {
//		super.init(grappleModInst);
//		MinecraftClient.getInstance().getRenderItem().getItemModelMesher().register(grapplemod.grapplebowitem, 0, new Identifier("grapplemod:grapplinghook", "inventory"));
//		MinecraftClient.getInstance().getRenderItem().getItemModelMesher().register(grapplemod.hookshotitem, 0, new Identifier("grapplemod:hookshot", "inventory"));
//		MinecraftClient.getInstance().getRenderItem().getItemModelMesher().register(grapplemod.launcheritem, 0, new Identifier("grapplemod:launcheritem", "inventory"));
//		MinecraftClient.getInstance().getRenderItem().getItemModelMesher().register(grapplemod.longfallboots, 0, new Identifier("grapplemod:longfallboots", "inventory"));
//		MinecraftClient.getInstance().getRenderItem().getItemModelMesher().register(grapplemod.enderhookitem, 0, new Identifier("grapplemod:enderhook", "inventory"));
		
        ClientSidePacketRegistry.INSTANCE.register(GrappleAttachMessage.IDENTIFIER, (packetContext, packetByteBuf) -> {
            GrappleAttachMessage.run(packetContext.getPlayer(), packetByteBuf);
        });
        ClientSidePacketRegistry.INSTANCE.register(GrappleDetachMessage.IDENTIFIER, (packetContext, packetByteBuf) -> {
            GrappleDetachMessage.run(packetContext.getPlayer(), packetByteBuf);
        });
        ClientSidePacketRegistry.INSTANCE.register(DetachSingleHookMessage.IDENTIFIER, (packetContext, packetByteBuf) -> {
            DetachSingleHookMessage.run(packetContext.getPlayer(), packetByteBuf);
        });
        ClientSidePacketRegistry.INSTANCE.register(GrappleAttachPosMessage.IDENTIFIER, (packetContext, packetByteBuf) -> {
            GrappleAttachPosMessage.run(packetContext.getPlayer(), packetByteBuf);
        });
        ClientSidePacketRegistry.INSTANCE.register(SegmentMessage.IDENTIFIER, (packetContext, packetByteBuf) -> {
            SegmentMessage.run(packetContext.getPlayer(), packetByteBuf);
        });
        ClientSidePacketRegistry.INSTANCE.register(LoggedInMessage.IDENTIFIER, (packetContext, packetByteBuf) -> {
            LoggedInMessage.run(packetContext.getPlayer(), packetByteBuf);
        });

		// register all the key bindings
		for (KeyBinding keyBinding : keyBindings) 
		{
	        KeyBindingHelper.registerKeyBinding(keyBinding);
		}

		CrosshairRenderer crosshairRenderer = new CrosshairRenderer();

		HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
		    crosshairRenderer.onRenderGameOverlayPost(matrixStack, tickDelta);
		});

//		BlockEntityUpdateS2CPacket
        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
            blockEntity.fromTag(blockEntity.getCachedState(), null); // TODO
        });

		
//		UseItemCallback.EVENT.register((player, world, hand) -> {
//            if (player.isSpectator()) {
//                return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
//            }
//
//            if (player.getItemCooldownManager().isCoolingDown(player.getStackInHand(hand).getItem())) {
//                return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
//            }
//
//            PlayerInteractEvent.RightClickItem event = new PlayerInteractEvent.RightClickItem(player, hand);
//
//            MinecraftForge.EVENT_BUS.post(event);
//
//            if (event.isCanceled() && event.getCancellationResult() == ActionResult.PASS) {
//                // TODO: Fabric API doesn't have a way to express "cancelled, but return PASS"
//
//                return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
//            }
//
//            return event.getCancellationResult();
//        });

	}
	
	public CrosshairRenderer crosshairrenderer;
	
//	@Override
	public void postInit() {
//		super.postInit(event);
		
		crosshairrenderer = new CrosshairRenderer();
	}
	
//	@Override
	public void getplayermovement(GrappleController control, int playerid) {
		Entity entity = control.entity;
		if (entity instanceof PlayerEntity) {
		    PlayerEntity player = (PlayerEntity) entity;
			control.receivePlayerMovementMessage(player.moveStrafing, player.moveForward, player.movementInput.jump);
		}
	}
	
	public ItemStack getKeypressStack(PlayerEntity player) {
		if (player != null) {
           ItemStack stack = player.getMainHandStack();
           if (stack != null) {
               Item item = stack.getItem();
               if (item instanceof KeypressItem) {
            	   return stack;
               }
           }
           
           stack = player.getOffHandStack();
           if (stack != null) {
        	   Item item = stack.getItem();
        	   if (item instanceof KeypressItem) {
        		   return stack;
        	   }
           }
		}
		return null;
	}
	
	public boolean isLookingAtModifierBlock(PlayerEntity player) {
		HitResult raytraceResult = MinecraftClient.getInstance().crosshairTarget;
		if (raytraceResult != null && raytraceResult.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = ((BlockHitResult) raytraceResult).getBlockPos();
			BlockState state = player.world.getBlockState(pos);
			return (state.getBlock() == GrappleMod.grappleModifierBlock);
		}
		return false;
	}
	
//	@SubscribeEvent
	public void onClientTick(/*TickEvent.ClientTickEvent event*/) {
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null) {
			if (!MinecraftClient.getInstance().isPaused() || !MinecraftClient.getInstance().isInSingleplayer()) {
				if (this.isWallRunning(player)) {
					if (!GrappleMod.controllers.containsKey(player.getEntityId())) {
						GrappleController controller = GrappleMod.createControl(GrappleMod.AIRID, -1, player.getEntityId(), player.world, new Vec(0,0,0), null, null);
						if (controller.getWallDirection() == null) {
							controller.unattach();
						}
					}
					
					if (GrappleMod.controllers.containsKey(player.getEntityId())) {
						ticksSinceLastOnGround = 0;
						alreadyUsedDoubleJump = false;
					}
				}

				this.checkDoubleJump();
				
				this.checkslide(player);
				
				this.rocketFuel += this.rocketIncreaseTick;
				
				try {
					Collection<GrappleController> controllers = GrappleMod.controllers.values();
					for (GrappleController controller : controllers) {
						controller.doClientTick();
					}
				} catch (ConcurrentModificationException e) {
					System.out.println("ConcurrentModificationException caught");
				}

				if (this.rocketFuel > 1) {this.rocketFuel = 1;}
				
				if (MinecraftClient.getInstance().currentScreen == null) {
					// keep in same order as enum from KeypressItem
					boolean keys[] = {keyEnderLaunch.isPressed(), keyLeftThrow.isPressed(), keyRightThrow.isPressed(), keyBothThrow.isPressed(), keyRocket.isPressed()};
					
					for (int i = 0; i < keys.length; i++) {
						boolean isKeyDown = keys[i];
						boolean prevKey = prevKeys[i];
						
						if (isKeyDown != prevKey) {
							KeypressItem.Keys key = KeypressItem.Keys.values()[i];
							
							ItemStack stack = getKeypressStack(player);
							if (stack != null) {
								if (!isLookingAtModifierBlock(player)) {
									if (isKeyDown) {
										((KeypressItem) stack.getItem()).onCustomKeyDown(stack, player, key, true);
									} else {
										((KeypressItem) stack.getItem()).onCustomKeyUp(stack, player, key, true);
									}
								}
							}
						}
						
						prevKeys[i] = isKeyDown;
					}
				}
				
				if (player.isOnGround()) {
					if (enderLaunchTimer.containsKey(player.getEntityId())) {
						long timer = Util.getMeasuringTimeMs() - enderLaunchTimer.get(player.getEntityId());
						if (timer > 10) {
							this.resetLauncherTime(player.getEntityId());
						}
					}
				}
			}
		}
	}
	
	private void checkslide(PlayerEntity player) {
		if (keySlide.isPressed() && !GrappleMod.controllers.containsKey(player.getEntityId()) && this.isSliding(player)) {
			GrappleMod.createControl(GrappleMod.AIRID, -1, player.getEntityId(), player.world, new Vec(0,0,0), null, null);
		}
	}

//	@Override
	public void startrocket(PlayerEntity player, GrappleCustomization custom) {
		if (!custom.rocket) return;
		
		if (!GrappleMod.controllers.containsKey(player.getEntityId())) {
			GrappleMod.createControl(GrappleMod.AIRID, -1, player.getEntityId(), player.world, new Vec(0,0,0), null, custom);
		} else {
			GrappleController controller = GrappleMod.controllers.get(player.getEntityId());
			if (controller.custom == null || !controller.custom.rocket) {
				if (controller.custom == null) {controller.custom = custom;}
				controller.custom.rocket = true;
				controller.custom.rocketActiveTime = custom.rocketActiveTime;
				controller.custom.rocketForce = custom.rocketForce;
				controller.custom.rocketRefuelRatio = custom.rocketRefuelRatio;
				this.updateRocketRegen(custom.rocketActiveTime, custom.rocketRefuelRatio);
			}
		}
	}

	
//	@Override
	public void launchplayer(PlayerEntity player) {
		long prevtime;
		if (enderLaunchTimer.containsKey(player.getEntityId())) {
			prevtime = enderLaunchTimer.get(player.getEntityId());
		} else {
			prevtime = 0;
		}
		long timer = Util.getMeasuringTimeMs() - prevtime;
		if (timer > GrappleConfig.getconf().enderStaffRecharge) {
			if ((player.getMainHandStack()!=null && (player.getMainHandStack().getItem() instanceof LauncherItem || player.getMainHandStack().getItem() instanceof GrappleBow)) || (player.getOffHandStack()!=null && (player.getOffHandStack().getItem() instanceof LauncherItem || player.getOffHandStack().getItem() instanceof GrappleBow))) {
				enderLaunchTimer.put(player.getEntityId(), Util.getMeasuringTimeMs());
				
	        	Vec facing = new Vec(player.getLookVec());
//				vec playermotion = vec.motionvec(player);
//				vec newvec = playermotion.add(facing.mult(3));
				
				/*
				if (!grapplemod.controllers.containsKey(player.getEntityId())) {
					player.motionX = newvec.x;
					player.motionY = newvec.y;
					player.motionZ = newvec.z;
					
					if (player instanceof PlayerEntityMP) {
						((PlayerEntityMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
					} else {
						grapplemod.network.sendToServer(new PlayerMovementMessage(player.getEntityId(), player.posX, player.posY, player.posZ, player.motionX, player.motionY, player.motionZ));
					}
				} else {
					facing.mult_ip(3);
					grapplemod.receiveEnderLaunch(player.getEntityId(), facing.x, facing.y, facing.z);
				}
				*/
	        	
	        	GrappleCustomization custom = null;
	        	if (player.getMainHandStack().getItem() instanceof GrappleBow) {
	        		custom = ((GrappleBow) player.getMainHandStack().getItem()).getCustomization(player.getMainHandStack());
	        	} else if (player.getOffHandStack().getItem() instanceof GrappleBow) {
	        		custom = ((GrappleBow) player.getOffHandStack().getItem()).getCustomization(player.getOffHandStack());
	        	}
	        	
				if (!GrappleMod.controllers.containsKey(player.getEntityId())) {
					player.setOnGround(false);
					GrappleMod.createControl(GrappleMod.AIRID, -1, player.getEntityId(), player.world, new Vec(0,0,0), null, custom);
				}
				facing.multIp(GrappleConfig.getconf().enderStaffStrength);
				GrappleMod.receiveEnderLaunch(player.getEntityId(), facing.x, facing.y, facing.z);
			}
		}
	}
	
//	@Override
	public void resetLauncherTime(int playerid) {
		if (enderLaunchTimer.containsKey(playerid)) {
			enderLaunchTimer.put(playerid, (long) 0);
		}
	}
	
//	@Override
	public boolean isSneaking(Entity entity) {
		if (entity == MinecraftClient.getInstance().player) {
			return MinecraftClient.getInstance().options.keySneak.isPressed();
		} else {
			return entity.isSneaking();
		}
	}
	
//	@Override
    public void blockBreak(/*BreakEvent event*/) {
    }
	
//	@Override
	public void handleDeath(Entity entity) {
		int id = entity.getEntityId();
		if (GrappleMod.controllers.containsKey(id)) {
			GrappleController controller = GrappleMod.controllers.get(id);
			controller.unattach();
		}
	}
	
//	@Override
	public String getKeyName(CommonProxyClass.Keys keyenum) {
		KeyBinding binding = null;
		
		GameOptions gs = MinecraftClient.getInstance().options;
		
		switch (keyenum) {
		case keyBindAttack:
			binding = gs.keyAttack; break;
		case keyBindBack:
			binding = gs.keyBack; break;
		case keyBindForward:
			binding = gs.keyForward; break;
		case keyBindJump:
			binding = gs.keyJump; break;
		case keyBindLeft:
			binding = gs.keyLeft; break;
		case keyBindRight:
			binding = gs.keyRight; break;
		case keyBindSneak:
			binding = gs.keySneak; break;
		case keyBindUseItem:
			binding = gs.keyUse; break;
		}
		
		if (binding == null) {
			return "";
		}
		
		String displayName = binding.getBoundKeyLocalizedText().asString();
		if (displayName.equals("Button 1")) {
			return "Left Click";
		} else if (displayName.equals("Button 2")) {
			return "Right Click";
		} else {
			return displayName;
		}
	}

	public static boolean isActive(ItemStack stack) {
		PlayerEntity p = MinecraftClient.getInstance().player;
//		if (p.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) == stack || p.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND) == stack) {
			int entityid = p.getEntityId();
			if (GrappleMod.controllers.containsKey(entityid)) {
				Item item = stack.getItem();
				GrappleController controller = GrappleMod.controllers.get(entityid);
				if (item instanceof GrappleBow && controller.controllerId == GrappleMod.GRAPPLEID) {
					return true;
				} else if (item.getClass() == Repeller.class && controller.controllerId == GrappleMod.REPELID) {
					return true;
				}
			}
//		}
		return false;
	}
	
	//	@Override
	public void openModifierScreen(GrappleModifierBlockEntity tileent) {
	    ScreenHandlerType<ModifireScreenHandler> MODIFIRE = ScreenHandlerRegistry
	            .registerExtended(ModifireScreenHandler.ID, (syncId, inventory, buf) -> new ModifireScreenHandler(MODIFIRE, syncId));
	    ScreenRegistry.register(MODIFIRE, GuiModifier::new);
	}
	
//	@SubscribeEvent
	public void onPlayerLoggedOutEvent(/*ClientDisconnectionFromServerEvent e*/) {
	    ClientTickEvents.END_CLIENT_TICK.register(client -> {
	        System.out.println("deleting server options");
	        GrappleConfig.setServerOptions(null);
	    });
	}


//	@SubscribeEvent
	public void clientTick(/*ClientTickEvent event*/) {
//		if (event.phase == TickEvent.Phase.END) {
//			if (keyBindings[0].isKeyDown()) {
//				System.out.println("Down");
//			}
//		}
	}

//	@Override
	public String localize(String string) {
		return I18n.translate(string);
	}
	
//	@Override
	public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio) {
		this.rocketDecreaseTick = 0.05 / 2.0 / rocket_active_time;
		this.rocketIncreaseTick = 0.05 / 2.0 / rocket_active_time / rocket_refuel_ratio;
	}
	
//	@Override
	public double getRocketFunctioning() {
		this.rocketFuel -= this.rocketIncreaseTick;
		this.rocketFuel -= this.rocketDecreaseTick;
		if (this.rocketFuel >= 0) {
			return 1;
		} else {
			this.rocketFuel = 0;
			return this.rocketIncreaseTick / this.rocketDecreaseTick / 2.0;
		}
	}
	
//	@Override
	public boolean isWallRunning(Entity entity) {
		if (entity.horizontalCollision && !entity.isOnGround() && !entity.isSneaking()) {
			for (ItemStack stack : entity.getArmorItems()) {
				if (stack != null) {
					Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
					if (enchantments.containsKey(GrappleMod.wallrunEnchantment)) {
						if (enchantments.get(GrappleMod.wallrunEnchantment) >= 1) {
							if (!keyJumpAndDetach.isPressed() && !MinecraftClient.getInstance().options.keyJump.isPressed()) {
								HitResult raytraceresult = entity.world.rayTraceBlock(entity.getPos(), Vec.positionVec(entity).add(new Vec(0, -1, 0)).toVec3d(), null, null, null);
								if (raytraceresult == null || raytraceresult.getType() != HitResult.Type.BLOCK) {
									return true;
								}
							}
						}
						break;
					}
				}
			}
		}
		return false;
	}

	boolean prevjumpbutton = false;
	int ticksSinceLastOnGround = 0;
	boolean alreadyUsedDoubleJump = false;
	
	public void checkDoubleJump() {
		PlayerEntity player = MinecraftClient.getInstance().player;
		
		if (player.isOnGround()) {
			ticksSinceLastOnGround = 0;
			alreadyUsedDoubleJump = false;
		} else {
			ticksSinceLastOnGround++;
		}
		
//		if (grapplemod.controllers.containsKey(player.getEntityId()) && !(grapplemod.controllers.get(player.getEntityId()) instanceof airfrictionController)) {
//			tickssincelastonground = 0;
//			alreadyuseddoublejump = false;
//		}
		
		if (player.isInsideWaterOrBubbleColumn()) {return;}
		
		boolean isjumpbuttondown = MinecraftClient.getInstance().options.keyJump.isPressed();
		
		if (isjumpbuttondown && !prevjumpbutton) {
			
			if (ticksSinceLastOnGround > 3) {
				if (!alreadyUsedDoubleJump) {
					if (wearingDoubleJumpeEchant(player)) {
						if (!GrappleMod.controllers.containsKey(player.getEntityId())) {
							GrappleMod.createControl(GrappleMod.AIRID, -1, player.getEntityId(), player.world, new Vec(0,0,0), null, null);
						}
						GrappleController controller = GrappleMod.controllers.get(player.getEntityId());
						if (controller instanceof AirFrictionController) {
							alreadyUsedDoubleJump = true;
							controller.doubleJump();
						}
					}
				}
			}
		}
		
		prevjumpbutton = isjumpbuttondown;
		
	}

	public boolean wearingDoubleJumpeEchant(Entity entity) {
		if (entity instanceof PlayerEntity && ((PlayerEntity) entity).canFly()) {
			return false;
		}
//		if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()) {
//			return false;
//		}
		
		for (ItemStack stack : entity.getArmorItems()) {
			if (stack != null) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
				if (enchantments.containsKey(GrappleMod.doubleJumpEnchantment)) {
					if (enchantments.get(GrappleMod.doubleJumpEnchantment) >= 1) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean isWearingSlidingEnchant(Entity entity) {
		for (ItemStack stack : entity.getArmorItems()) {
			if (stack != null) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
				if (enchantments.containsKey(GrappleMod.slidingEnchantment)) {
					if (enchantments.get(GrappleMod.slidingEnchantment) >= 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

//	@Override
	public boolean isSliding(Entity entity) {
		if (entity.isInsideWaterOrBubbleColumn()) {return false;}
		
		if (entity.isOnGround() && keySlide.isPressed()) {
			if (ClientProxyClass.isWearingSlidingEnchant(entity)) {
				return true;
			}
		}
		return false;
	}

//	@SubscribeEvent
	public void onKeyInputEvent(/*KeyInputEvent event*/) {
		PlayerEntity player = MinecraftClient.getInstance().player;
		
		GrappleController controller = null;
		if (GrappleMod.controllers.containsKey(player.getEntityId())) {
			controller = GrappleMod.controllers.get(player.getEntityId());
		}
		
		if (MinecraftClient.getInstance().options.keyJump.isPressed()) {
			if (controller != null) {
				if (controller instanceof AirFrictionController && isSliding(player)) {
					controller.slidingJump();
				}
			}
		}
		
	}
}
