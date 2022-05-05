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

package com.yyon.grapplinghook.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yyon.grapplinghook.client.ClientProxyInterface.McKeys;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.network.DetachSingleHookMessage;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import com.yyon.grapplinghook.network.KeypressMessage;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.yyon.grapplinghook.client.ClientSetup.clientProxy;
import static com.yyon.grapplinghook.client.ClientSetup.key_boththrow;
import static com.yyon.grapplinghook.client.ClientSetup.key_climb;
import static com.yyon.grapplinghook.client.ClientSetup.key_climbdown;
import static com.yyon.grapplinghook.client.ClientSetup.key_climbup;
import static com.yyon.grapplinghook.client.ClientSetup.key_enderlaunch;
import static com.yyon.grapplinghook.client.ClientSetup.key_jumpanddetach;
import static com.yyon.grapplinghook.client.ClientSetup.key_leftthrow;
import static com.yyon.grapplinghook.client.ClientSetup.key_motoronoff;
import static com.yyon.grapplinghook.client.ClientSetup.key_rightthrow;
import static com.yyon.grapplinghook.client.ClientSetup.key_rocket;
import static com.yyon.grapplinghook.client.ClientSetup.key_slow;
import static com.yyon.grapplinghook.common.CommonSetup.serverControllerManager;


public class GrapplehookItem extends Item implements KeypressItem {
	public static Map<Entity, GrapplehookEntity> grapplehookEntitiesLeft = new HashMap<>();
	public static Map<Entity, GrapplehookEntity> grapplehookEntitiesRight = new HashMap<>();

	public GrapplehookItem(Settings settings) {
		super(settings.maxCount(1).maxDamage(GrappleConfig.getConf().grapplinghook.other.default_durability));
	}

	public boolean hasHookEntity(Entity entity) {
		GrapplehookEntity hookLeft = getHookEntityLeft(entity);
		GrapplehookEntity hookRight = getHookEntityRight(entity);
		return (hookLeft != null) || (hookRight != null);
	}

	public void setHookEntityLeft(Entity entity, GrapplehookEntity hookEntity) {
		grapplehookEntitiesLeft.put(entity, hookEntity);
	}

	public void setHookEntityRight(Entity entity, GrapplehookEntity hookEntity) {
		grapplehookEntitiesRight.put(entity, hookEntity);
	}

	public GrapplehookEntity getHookEntityLeft(Entity entity) {
		if (grapplehookEntitiesLeft.containsKey(entity)) {
			GrapplehookEntity hookEntity = grapplehookEntitiesLeft.get(entity);
			if (hookEntity != null && hookEntity.isAlive()) {
				return hookEntity;
			}
		}
		return null;
	}
	public GrapplehookEntity getHookEntityRight(Entity entity) {
		if (grapplehookEntitiesRight.containsKey(entity)) {
			GrapplehookEntity hookEntity = grapplehookEntitiesRight.get(entity);
			if (hookEntity != null && hookEntity.isAlive()) {
				return hookEntity;
			}
		}
		return null;
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack repair) {
        if (repair != null && repair.getItem().equals(Items.LEATHER)) return true;
        return super.canRepair(stack, repair);
	}

	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
    	return true;
    }
    
	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		return true;
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return false;
	}

	@Override
	public void onCustomKeyDown(ItemStack stack, PlayerEntity player, KeypressItem.Keys key, boolean ismainhand) {
		if (player.world.isClient()) {
			if (key == KeypressItem.Keys.LAUNCHER) {
				if (this.getCustomization(stack).enderstaff) {
					clientProxy.launchPlayer(player);
				}
			} else if (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT || key == KeypressItem.Keys.THROWBOTH) {
				new KeypressMessage(key, true).send();
			} else if (key == KeypressItem.Keys.ROCKET) {
				GrappleCustomization custom = this.getCustomization(stack);
				if (custom.rocket) {
					clientProxy.startRocket(player, custom);
				}
			}
		} else {
			if (key == KeypressItem.Keys.THROWBOTH) {
	        	throwBoth(stack, player.world, player, ismainhand);
			} else if (key == KeypressItem.Keys.THROWLEFT) {
				GrapplehookEntity hookLeft = getHookEntityLeft(player);

	    		if (hookLeft != null) {
	    			detachLeft(player);
		    		return;
				}

				stack.damage(1, (ServerPlayerEntity) player, (p) -> {});
				if (stack.getCount() <= 0) {
					return;
				}

				boolean threw = throwLeft(stack, player.world, player, ismainhand);

				if (threw) {
			        player.world.playSound(null, player.getTrackedPosition().x, player.getTrackedPosition().y, player.getTrackedPosition().z, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
				}
			} else if (key == KeypressItem.Keys.THROWRIGHT) {
				GrapplehookEntity hookRight = getHookEntityRight(player);

	    		if (hookRight != null) {
	    			detachRight(player);
		    		return;
				}

				stack.damage(1, (ServerPlayerEntity) player, (p) -> {});
				if (stack.getCount() <= 0) {
					return;
				}

				throwRight(stack, player.world, player, ismainhand);

		        player.world.playSound(null, player.getTrackedPosition().x, player.getTrackedPosition().y, player.getTrackedPosition().z, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
			}
		}
	}

	@Override
	public void onCustomKeyUp(ItemStack stack, PlayerEntity player, KeypressItem.Keys key, boolean ismainhand) {
		if (player.world.isClient()) {
			if (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT || key == KeypressItem.Keys.THROWBOTH) {
				new KeypressMessage(key, false).send();
			}
		} else {
	    	GrappleCustomization custom = this.getCustomization(stack);

	    	if (custom.detachonkeyrelease) {
	    		GrapplehookEntity hookLeft = getHookEntityLeft(player);
	    		GrapplehookEntity hookRight = getHookEntityRight(player);

				if (key == KeypressItem.Keys.THROWBOTH) {
					detachBoth(player);
				} else if (key == KeypressItem.Keys.THROWLEFT) {
		    		if (hookLeft != null) detachLeft(player);
				} else if (key == KeypressItem.Keys.THROWRIGHT) {
		    		if (hookRight != null) detachRight(player);
				}
	    	}
		}
	}

	public void throwBoth(ItemStack stack, World worldIn, LivingEntity entityLiving, boolean righthand) {
		GrapplehookEntity hookLeft = getHookEntityLeft(entityLiving);
		GrapplehookEntity hookRight = getHookEntityRight(entityLiving);

		if (hookLeft != null || hookRight != null) {
			detachBoth(entityLiving);
    		return;
		}

		stack.damage(1, (ServerPlayerEntity) entityLiving, (p) -> {});
		if (stack.getCount() <= 0) {
			return;
		}

    	GrappleCustomization custom = this.getCustomization(stack);
  		double angle = custom.angle;
//  		double verticalangle = custom.verticalthrowangle;
  		if (entityLiving.isSneaking()) {
  			angle = custom.sneakingangle;
//  			verticalangle = custom.sneakingverticalthrowangle;
  		}

    	if (!(!custom.doublehook || angle == 0)) {
    		throwLeft(stack, worldIn, entityLiving, righthand);
    	}
		throwRight(stack, worldIn, entityLiving, righthand);

		entityLiving.world.playSound(null, entityLiving.getTrackedPosition().x, entityLiving.getTrackedPosition().y, entityLiving.getTrackedPosition().z, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (worldIn.random.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
	}

	public boolean throwLeft(ItemStack stack, World world, LivingEntity player, boolean righthand) {
    	GrappleCustomization custom = this.getCustomization(stack);

  		double angle = custom.angle;
  		double verticalangle = custom.verticalthrowangle;

  		if (player.isSneaking()) {
  			angle = custom.sneakingangle;
  			verticalangle = custom.sneakingverticalthrowangle;
  		}

		Vec anglevec = Vec.fromAngles(Math.toRadians(-angle), Math.toRadians(verticalangle));
  		anglevec = anglevec.rotatePitch(Math.toRadians(-player.getPitch(1.0F)));
  		anglevec = anglevec.rotateYaw(Math.toRadians(player.getYaw(1.0F)));
        float velx = (float) (-Math.sin(anglevec.getYaw() * 0.017453292) * Math.cos(anglevec.getPitch() * 0.017453292));
        float vely = (float) -Math.sin(anglevec.getPitch() * 0.017453292);
        float velz = (float) (Math.cos(anglevec.getYaw() * 0.017453292) * Math.cos(anglevec.getPitch() * 0.017453292));
		GrapplehookEntity hookEntity = this.createGrapplehookEntity(stack, world, player, false, true);
        float extravelocity = (float) Vec.motionVec(player).distAlong(new Vec(velx, vely, velz));
        if (extravelocity < 0) { extravelocity = 0; }
        hookEntity.setVelocity((double) velx, (double) vely, (double) velz, hookEntity.getVelocity_() + extravelocity, 0.0F);
        
		world.spawnEntity(hookEntity);
		setHookEntityLeft(player, hookEntity);

		return true;
	}

	public void throwRight(ItemStack stack, World world, LivingEntity entityLiving, boolean righthand) {
    	GrappleCustomization custom = this.getCustomization(stack);

  		double angle = custom.angle;
  		double verticalangle = custom.verticalthrowangle;
  		if (entityLiving.isSneaking()) {
  			angle = custom.sneakingangle;
  			verticalangle = custom.sneakingverticalthrowangle;
  		}

    	if (!custom.doublehook || angle == 0) {
			GrapplehookEntity hookEntity = this.createGrapplehookEntity(stack, world, entityLiving, righthand, false);
      		Vec anglevec = new Vec(0,0,1).rotatePitch(Math.toRadians(verticalangle));
      		anglevec = anglevec.rotatePitch(Math.toRadians(-entityLiving.getPitch(1.0F)));
      		anglevec = anglevec.rotateYaw(Math.toRadians(entityLiving.getYaw(1.0F)));
	        float velx = (float) (-Math.sin((float) anglevec.getYaw() * 0.017453292F) * Math.cos((float) anglevec.getPitch() * 0.017453292F));
	        float vely = (float) -Math.sin((float) anglevec.getPitch() * 0.017453292F);
	        float velz = (float) (Math.cos((float) anglevec.getYaw() * 0.017453292F) * Math.cos((float) anglevec.getPitch() * 0.017453292F));
	        float extravelocity = (float) Vec.motionVec(entityLiving).distAlong(new Vec(velx, vely, velz));
	        if (extravelocity < 0) { extravelocity = 0; }
	        hookEntity.setVelocity((double) velx, (double) vely, (double) velz, hookEntity.getVelocity_() + extravelocity, 0.0F);
			setHookEntityRight(entityLiving, hookEntity);
			world.spawnEntity(hookEntity);
    	} else {
      		LivingEntity player = entityLiving;

      		Vec anglevec = Vec.fromAngles(Math.toRadians(angle), Math.toRadians(verticalangle));
      		anglevec = anglevec.rotatePitch(Math.toRadians(-player.getPitch(1.0F)));
      		anglevec = anglevec.rotateYaw(Math.toRadians(player.getYaw(1.0F)));
	        float velx = (float) (-Math.sin((float) anglevec.getYaw() * 0.017453292F) * Math.cos((float) anglevec.getPitch() * 0.017453292F));
	        float vely = (float) -Math.sin((float) anglevec.getPitch() * 0.017453292F);
	        float velz = (float) (Math.cos((float) anglevec.getYaw() * 0.017453292F) * Math.cos((float) anglevec.getPitch() * 0.017453292F));
			GrapplehookEntity hookEntity = this.createGrapplehookEntity(stack, world, entityLiving, true, true);
	        float extravelocity = (float) Vec.motionVec(entityLiving).distAlong(new Vec(velx, vely, velz));
	        if (extravelocity < 0) { extravelocity = 0; }
	        hookEntity.setVelocity((double) velx, (double) vely, (double) velz, hookEntity.getVelocity_() + extravelocity, 0.0F);
            
			world.spawnEntity(hookEntity);
			setHookEntityRight(entityLiving, hookEntity);
		}
	}

	public void detachBoth(LivingEntity entityLiving) {
		GrapplehookEntity hookLeft = getHookEntityLeft(entityLiving);
		GrapplehookEntity hookRight = getHookEntityRight(entityLiving);

		setHookEntityLeft(entityLiving, null);
		setHookEntityRight(entityLiving, null);

		if (hookLeft != null) {
			hookLeft.removeServer();
		}
		if (hookRight != null) {
			hookRight.removeServer();
		}

		int id = entityLiving.getId();
		new GrappleDetachMessage(id).send((ServerPlayerEntity) entityLiving);

		serverControllerManager.attached.remove(id);
	}

	public void detachLeft(LivingEntity entityLiving) {
		GrapplehookEntity hookLeft = getHookEntityLeft(entityLiving);

		setHookEntityLeft(entityLiving, null);

		if (hookLeft != null) {
			hookLeft.removeServer();
		}

		int id = entityLiving.getId();

		// remove controller if hook is attached
		if (entityLiving instanceof ServerPlayerEntity playerEntity) {
			if (getHookEntityRight(entityLiving) == null) {
				new GrappleDetachMessage(id).send(playerEntity);
			} else {
				new DetachSingleHookMessage(id, hookLeft.getControlId()).send(playerEntity);
			}
		}

		serverControllerManager.attached.remove(id);
	}

	public void detachRight(LivingEntity entityLiving) {
		GrapplehookEntity hookRight = getHookEntityRight(entityLiving);

		setHookEntityRight(entityLiving, null);

		if (hookRight != null) {
			hookRight.removeServer();
		}

		int id = entityLiving.getId();

		// remove controller if hook is attached
		if (entityLiving instanceof ServerPlayerEntity playerEntity) {
			if (getHookEntityLeft(entityLiving) == null) {
				new GrappleDetachMessage(id).send(playerEntity);
			} else {
				new DetachSingleHookMessage(id, hookRight.getControlId()).send(playerEntity);
			}
		}

		serverControllerManager.attached.remove(id);
	}

    public double getAngle(LivingEntity entity, ItemStack stack) {
    	GrappleCustomization custom = this.getCustomization(stack);
    	if (entity.isSneaking()) {
    		return custom.sneakingangle;
    	} else {
    		return custom.angle;
    	}
    }

	public GrapplehookEntity createGrapplehookEntity(ItemStack stack, World world, LivingEntity entityLiving, boolean righthand, boolean isdouble) {
		GrapplehookEntity hookEntity = new GrapplehookEntity(world, entityLiving, righthand, this.getCustomization(stack), isdouble);
		serverControllerManager.addGrapplehookEntity(entityLiving.getId(), hookEntity);
		return hookEntity;
	}

    public GrappleCustomization getCustomization(ItemStack itemstack) {
		NbtCompound tag = itemstack.getOrCreateNbt();

    	if (tag.contains("custom")) {
        	GrappleCustomization custom = new GrappleCustomization();
    		custom.loadNBT(tag.getCompound("custom"));
        	return custom;
    	} else {
    		GrappleCustomization custom = this.getDefaultCustomization();

			NbtCompound nbt = custom.writeNBT();

			tag.put("custom", nbt);
			itemstack.setNbt(tag);

    		return custom;
    	}
    }
    
    public GrappleCustomization getDefaultCustomization() {
    	return new GrappleCustomization();
    }

	// TO:MOJANG
	// TranslatableText specification is very bad.
	// it should be like,
	// Text.of("this is an {i18n:item.name.key}");
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		GrappleCustomization custom = getCustomization(stack);

		if (Screen.hasShiftDown()) {
			if (!custom.detachonkeyrelease) {
				tooltip.add(new TranslatableText(key_boththrow.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.throw.desc")));
				tooltip.add(new TranslatableText(key_boththrow.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.release.desc")));
				tooltip.add(new TranslatableText("grappletooltip.double.desc").append(new TranslatableText(key_boththrow.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.releaseandthrow.desc"))));
			} else {
				tooltip.add(new TranslatableText(key_boththrow.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.throwhold.desc")));
			}
			tooltip.add(new TranslatableText(clientProxy.getKeyname(McKeys.keyBindForward)).append(", ").append(
					new TranslatableText(clientProxy.getKeyname(McKeys.keyBindLeft))).append(", ").append(
					new TranslatableText(clientProxy.getKeyname(McKeys.keyBindBack))).append(", ").append(
					new TranslatableText(clientProxy.getKeyname(McKeys.keyBindRight))).append(" ").append(new TranslatableText("grappletooltip.swing.desc")));
			tooltip.add(new TranslatableText(key_jumpanddetach.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.jump.desc")));
			tooltip.add(new TranslatableText(key_slow.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.slow.desc")));
			tooltip.add(new TranslatableText(key_climb.getTranslationKey()).append(" + ").append(new TranslatableText(clientProxy.getKeyname(McKeys.keyBindForward)).append(" / ").append
							(new TranslatableText(key_climbup.getTranslationKey()))
					.append(" ").append(new TranslatableText("grappletooltip.climbup.desc"))));
			tooltip.add(new TranslatableText(key_climb.getTranslationKey()).append(" + ").append(new TranslatableText(clientProxy.getKeyname(McKeys.keyBindBack)).append(" / ").append
							(new TranslatableText(key_climbdown.getTranslationKey()))
					.append(" ").append(new TranslatableText("grappletooltip.climbdown.desc"))));
			if (custom.enderstaff) {
				tooltip.add(new TranslatableText(key_enderlaunch.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.enderlaunch.desc")));
			}
			if (custom.rocket) {
				tooltip.add(new TranslatableText(key_rocket.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.rocket.desc")));
			}
			if (custom.motor) {
				if (custom.motorwhencrouching && !custom.motorwhennotcrouching) {
					tooltip.add(new TranslatableText(key_motoronoff.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.motoron.desc")));
				}
				else if (!custom.motorwhencrouching && custom.motorwhennotcrouching) {
					tooltip.add(new TranslatableText(key_motoronoff.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.motoroff.desc")));
				}
			}
			if (custom.doublehook) {
				if (!custom.detachonkeyrelease) {
					tooltip.add(new TranslatableText(key_leftthrow.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.throwleft.desc")));
					tooltip.add(new TranslatableText(key_rightthrow.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.throwright.desc")));
				} else {
					tooltip.add(new TranslatableText(key_leftthrow.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.throwlefthold.desc")));
					tooltip.add(new TranslatableText(key_rightthrow.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.throwrighthold.desc")));
				}
			} else {
				tooltip.add(new TranslatableText(key_rightthrow.getTranslationKey()).append(" ").append(new TranslatableText("grappletooltip.throwalt.desc")));
			}
			if (custom.reelin) {
				tooltip.add(new TranslatableText(clientProxy.getKeyname(McKeys.keyBindSneak)).append(" ").append(new TranslatableText("grappletooltip.reelin.desc")));
			}
		} else {
			if (Screen.hasControlDown()) {
				for (String option : GrappleCustomization.booleanoptions) {
					if (custom.isOptionValid(option) && custom.getBoolean(option) != GrappleCustomization.DEFAULT.getBoolean(option)) {
						tooltip.add((custom.getBoolean(option) ? new LiteralText("") : new TranslatableText("grappletooltip.negate.desc")).append(" ").append(new TranslatableText(custom.getName(option))));
					}
				}
				for (String option : GrappleCustomization.doubleoptions) {
					if (custom.isOptionValid(option) && (custom.getDouble(option) != GrappleCustomization.DEFAULT.getDouble(option))) {
						tooltip.add(new TranslatableText(custom.getName(option)).append(": " + Math.floor(custom.getDouble(option) * 100) / 100));
					}
				}
			} else {
				if (custom.doublehook) {
					tooltip.add(new TranslatableText(custom.getName("doublehook")));
				}
				if (custom.motor) {
					if (custom.smartmotor) {
						tooltip.add(new TranslatableText(custom.getName("smartmotor")));
					} else {
						tooltip.add(new TranslatableText(custom.getName("motor")));
					}
				}
				if (custom.enderstaff) {
					tooltip.add(new TranslatableText(custom.getName("enderstaff")));
				}
				if (custom.rocket) {
					tooltip.add(new TranslatableText(custom.getName("rocket")));
				}
				if (custom.attract) {
					tooltip.add(new TranslatableText(custom.getName("attract")));
				}
				if (custom.repel) {
					tooltip.add(new TranslatableText(custom.getName("repel")));
				}

				tooltip.add(Text.of(""));
				tooltip.add(new TranslatableText("grappletooltip.shiftcontrols.desc"));
				tooltip.add(new TranslatableText("grappletooltip.controlconfiguration.desc"));
			}
		}
	}

	public void setCustomOnServer(ItemStack helditemstack, GrappleCustomization custom, PlayerEntity player) {
		NbtCompound tag = helditemstack.getOrCreateNbt();
		NbtCompound nbt = custom.writeNBT();

		tag.put("custom", nbt);

		helditemstack.setNbt(tag);
	}

	public void onDroppedByPlayer(ItemStack item, PlayerEntity player) {
		int id = player.getId();
		new GrappleDetachMessage(id).send((ServerPlayerEntity) player);

		if (!player.world.isClient()) {
			serverControllerManager.attached.remove(id);
		}

		if (grapplehookEntitiesLeft.containsKey(player)) {
			GrapplehookEntity hookLeft = grapplehookEntitiesLeft.get(player);
			setHookEntityLeft(player, null);
			if (hookLeft != null) {
				hookLeft.removeServer();
			}
		}

		if (grapplehookEntitiesRight.containsKey(player)) {
			GrapplehookEntity hookRight = grapplehookEntitiesRight.get(player);
			setHookEntityLeft(player, null);
			if (hookRight != null) {
				hookRight.removeServer();
			}
		}
	}

	public boolean getPropertyRocket(ItemStack stack, World world, LivingEntity entity) {
		return this.getCustomization(stack).rocket;
	}

	public boolean getPropertyDouble(ItemStack stack, World world, LivingEntity entity) {
		return this.getCustomization(stack).doublehook;
	}

	public boolean getPropertyMotor(ItemStack stack, World world, LivingEntity entity) {
		return this.getCustomization(stack).motor;
	}

	public boolean getPropertySmart(ItemStack stack, World world, LivingEntity entity) {
		return this.getCustomization(stack).smartmotor;
	}

	public boolean getPropertyEnderstaff(ItemStack stack, World world, LivingEntity entity) {
		return this.getCustomization(stack).enderstaff;
	}

	public boolean getPropertyMagnet(ItemStack stack, World world, LivingEntity entity) {
		return this.getCustomization(stack).attract || this.getCustomization(stack).repel;
	}

	@Override
	public void appendStacks(ItemGroup tab, DefaultedList<ItemStack> items) {
		if (this.isIn(tab)) {
			ItemStack stack = new ItemStack(this);
			items.add(stack);
			if (clientProxy != null) {
				clientProxy.fillGrappleVariants(tab, items);
			}
		}
	}
}
