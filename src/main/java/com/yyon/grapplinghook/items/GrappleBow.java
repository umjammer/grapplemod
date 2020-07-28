
package com.yyon.grapplinghook.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import com.yyon.grapplinghook.ClientProxyClass;
import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.Vec;
import com.yyon.grapplinghook.entities.GrappleArrow;
import com.yyon.grapplinghook.network.DetachSingleHookMessage;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import com.yyon.grapplinghook.network.KeypressMessage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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


public class GrappleBow extends Item implements KeypressItem {
    public static Map<Entity, GrappleArrow> grappleArrows1 = new HashMap<>();
    public static Map<Entity, GrappleArrow> grappleArrows2 = new HashMap<>();

    public GrappleBow() {
        super(new Settings().maxCount(1).maxDamage(500).group(GrappleMod.tabGrapplemod));
//		setUnlocalizedName("grapplinghook");
    }

//    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 72000;
    }

    public boolean hasArrow(Entity entity) {
        GrappleArrow arrow1 = getArrowLeft(entity);
        GrappleArrow arrow2 = getArrowRight(entity);
        return (arrow1 != null) || (arrow2 != null);
    }

//	@Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack mat = new ItemStack(Items.LEATHER, 1);
        if (mat != null && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false))
            return true;
        return super.canRepair(toRepair, repair);
    }

    public void setArrowLeft(Entity entity, GrappleArrow arrow) {
        GrappleBow.grappleArrows1.put(entity, arrow);
    }

    public void setArrowRight(Entity entity, GrappleArrow arrow) {
        GrappleBow.grappleArrows2.put(entity, arrow);
    }

    public GrappleArrow getArrowLeft(Entity entity) {
        if (GrappleBow.grappleArrows1.containsKey(entity)) {
            GrappleArrow arrow = GrappleBow.grappleArrows1.get(entity);
            if (arrow != null && arrow.isAlive()) {
                return arrow;
            }
        }
        return null;
    }

    public GrappleArrow getArrowRight(Entity entity) {
        if (GrappleBow.grappleArrows2.containsKey(entity)) {
            GrappleArrow arrow = GrappleBow.grappleArrows2.get(entity);
            if (arrow != null && arrow.isAlive()) {
                return arrow;
            }
        }
        return null;
    }

    public void dorightclick(ItemStack stack, World worldIn, LivingEntity livingEntity, boolean righthand) {
        if (worldIn.isClient) {
        }
    }

    public void throwBoth(ItemStack stack, World worldIn, LivingEntity livingEntity, boolean righthand) {
        GrappleArrow arrow_left = getArrowLeft(livingEntity);
        GrappleArrow arrow_right = getArrowRight(livingEntity);

        if (arrow_left != null || arrow_right != null) {
            detachBoth(livingEntity);
            return;
        }

        GrappleCustomization custom = this.getCustomization(stack);
        double angle = custom.angle;
        double verticalangle = custom.verticalThrowAngle;
        if (livingEntity.isSneaking()) {
            angle = custom.sneakingAngle;
            verticalangle = custom.sneakingVerticalThrowAngle;
        }

        if (!(!custom.doublehook || angle == 0)) {
            throwLeft(stack, worldIn, livingEntity, righthand);
        }
        throwRight(stack, worldIn, livingEntity, righthand);

        stack.damage(1, livingEntity, x -> {}); // TODO
        worldIn.playSound((PlayerEntity) null,
                          livingEntity.getX(),
                          livingEntity.getY(),
                          livingEntity.getZ(),
                          SoundEvents.ENTITY_ARROW_SHOOT,
                          SoundCategory.NEUTRAL,
                          1.0F,
                          1.0F / (RANDOM.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
    }

    public boolean throwLeft(ItemStack stack, World worldIn, LivingEntity livingEntity, boolean rightHand) {
        GrappleCustomization custom = this.getCustomization(stack);

        double angle = custom.angle;
        double verticalAngle = custom.verticalThrowAngle;

        if (livingEntity.isSneaking()) {
            angle = custom.sneakingAngle;
            verticalAngle = custom.sneakingVerticalThrowAngle;
        }

        LivingEntity player = livingEntity;

        Vec angleVec = new Vec(0, 0, 1).rotateYaw(Math.toRadians(-angle)).rotatePitch(Math.toRadians(verticalAngle));
        angleVec = angleVec.rotatePitch(Math.toRadians(-player.pitch));
        angleVec = angleVec.rotateYaw(Math.toRadians(player.yaw));
        float velx = -MathHelper.sin((float) angleVec.getYaw() * 0.017453292F) *
                     MathHelper.cos((float) angleVec.getPitch() * 0.017453292F);
        float vely = -MathHelper.sin((float) angleVec.getPitch() * 0.017453292F);
        float velz = MathHelper.cos((float) angleVec.getYaw() * 0.017453292F) *
                     MathHelper.cos((float) angleVec.getPitch() * 0.017453292F);
        GrappleArrow arrowEntity = this.createArrow(stack, worldIn, livingEntity, false, true);
        // new GrappleArrow(worldIn, player, false);
        float extraVelocity = (float) Vec.motionVec(livingEntity).distAlong(new Vec(velx, vely, velz));
        if (extraVelocity < 0) {
            extraVelocity = 0;
        }
        arrowEntity.shoot((double) velx, (double) vely, (double) velz, arrowEntity.getVelocity_() + extraVelocity, 0.0F);

        worldIn.spawnEntity(arrowEntity);
        setArrowLeft(livingEntity, arrowEntity);

        return true;
    }

    public void throwRight(ItemStack stack, World worldIn, LivingEntity livingEntity, boolean rightHand) {
        GrappleCustomization custom = this.getCustomization(stack);

        double angle = custom.angle;
        double verticalAngle = custom.verticalThrowAngle;
        if (livingEntity.isSneaking()) {
            angle = custom.sneakingAngle;
            verticalAngle = custom.sneakingVerticalThrowAngle;
        }

        if (!custom.doublehook || angle == 0) {
            GrappleArrow arrowEntity = this.createArrow(stack, worldIn, livingEntity, rightHand, false);
            Vec angleVec = new Vec(0, 0, 1).rotatePitch(Math.toRadians(verticalAngle));
            angleVec = angleVec.rotatePitch(Math.toRadians(-livingEntity.pitch));
            angleVec = angleVec.rotateYaw(Math.toRadians(livingEntity.yaw));
            float velx = -MathHelper.sin((float) angleVec.getYaw() * 0.017453292F) *
                         MathHelper.cos((float) angleVec.getPitch() * 0.017453292F);
            float vely = -MathHelper.sin((float) angleVec.getPitch() * 0.017453292F);
            float velz = MathHelper.cos((float) angleVec.getYaw() * 0.017453292F) *
                         MathHelper.cos((float) angleVec.getPitch() * 0.017453292F);
            float extravelocity = (float) Vec.motionVec(livingEntity).distAlong(new Vec(velx, vely, velz));
            if (extravelocity < 0) {
                extravelocity = 0;
            }
            arrowEntity.shoot((double) velx, (double) vely, (double) velz, arrowEntity.getVelocity_() + extravelocity, 0.0F);
            setArrowRight(livingEntity, arrowEntity);
            worldIn.spawnEntity(arrowEntity);
        } else {
            LivingEntity player = livingEntity;

            Vec angleVec = new Vec(0, 0, 1).rotateYaw(Math.toRadians(angle)).rotatePitch(Math.toRadians(verticalAngle));
            angleVec = angleVec.rotatePitch(Math.toRadians(-player.pitch));
            angleVec = angleVec.rotateYaw(Math.toRadians(player.yaw));
            float velx = -MathHelper.sin((float) angleVec.getYaw() * 0.017453292F) *
                         MathHelper.cos((float) angleVec.getPitch() * 0.017453292F);
            float vely = -MathHelper.sin((float) angleVec.getPitch() * 0.017453292F);
            float velz = MathHelper.cos((float) angleVec.getYaw() * 0.017453292F) *
                         MathHelper.cos((float) angleVec.getPitch() * 0.017453292F);
            GrappleArrow arrowEntity = this.createArrow(stack, worldIn, livingEntity, true, true);
//            new GrappleArrow(worldIn, player, true);
//            arrowEntity.shoot(player, (float) anglevec.getPitch(), (float)anglevec.getYaw(), 0.0F, arrowEntity.getVelocity(), 0.0F);
            float extravelocity = (float) Vec.motionVec(livingEntity).distAlong(new Vec(velx, vely, velz));
            if (extravelocity < 0) {
                extravelocity = 0;
            }
            arrowEntity.shoot((double) velx, (double) vely, (double) velz, arrowEntity.getVelocity_() + extravelocity, 0.0F);

            worldIn.spawnEntity(arrowEntity);
            setArrowRight(livingEntity, arrowEntity);
        }
    }

    public void detachBoth(LivingEntity livingEntity) {
        GrappleArrow arrow1 = getArrowLeft(livingEntity);
        GrappleArrow arrow2 = getArrowRight(livingEntity);

        setArrowLeft(livingEntity, null);
        setArrowRight(livingEntity, null);

        if (arrow1 != null) {
            arrow1.removeServer();
        }
        if (arrow2 != null) {
            arrow2.removeServer();
        }

        int id = livingEntity.getEntityId();
        GrappleMod.sendToCorrectClient(new GrappleDetachMessage(id), id, livingEntity.world);

        if (GrappleMod.attached.contains(id)) {
            GrappleMod.attached.remove(new Integer(id));
        }
    }

    public void detachLeft(LivingEntity livingEntity) {
        GrappleArrow arrow1 = getArrowLeft(livingEntity);

        setArrowLeft(livingEntity, null);

        if (arrow1 != null) {
            arrow1.removeServer();
        }

        int id = livingEntity.getEntityId();

        // remove controller if hook is attached
        if (getArrowRight(livingEntity) == null) {
            GrappleMod.sendToCorrectClient(new GrappleDetachMessage(id), id, livingEntity.world);
        } else {
            GrappleMod.sendToCorrectClient(new DetachSingleHookMessage(id, arrow1.getEntityId()), id, livingEntity.world);
        }

        if (GrappleMod.attached.contains(id)) {
            GrappleMod.attached.remove(new Integer(id));
        }
    }

    public void detachRight(LivingEntity livingEntity) {
        GrappleArrow arrow2 = getArrowRight(livingEntity);

        setArrowRight(livingEntity, null);

        if (arrow2 != null) {
            arrow2.removeServer();
        }

        int id = livingEntity.getEntityId();

        // remove controller if hook is attached
        if (getArrowLeft(livingEntity) == null) {
            GrappleMod.sendToCorrectClient(new GrappleDetachMessage(id), id, livingEntity.world);
        } else {
            GrappleMod.sendToCorrectClient(new DetachSingleHookMessage(id, arrow2.getEntityId()), id, livingEntity.world);
        }

        if (GrappleMod.attached.contains(id)) {
            GrappleMod.attached.remove(new Integer(id));
        }
    }

    public double getAngle(LivingEntity entity, ItemStack stack) {
        GrappleCustomization custom = this.getCustomization(stack);
        if (entity.isSneaking()) {
            return custom.sneakingAngle;
        } else {
            return custom.angle;
        }
    }

    public GrappleArrow createArrow(ItemStack stack,
                                    World worldIn,
                                    LivingEntity entityLiving,
                                    boolean righthand,
                                    boolean isdouble) {
        GrappleArrow arrow = new GrappleArrow(worldIn, entityLiving, righthand, this.getCustomization(stack), isdouble);
        GrappleMod.addarrow(entityLiving.getEntityId(), arrow);
        return arrow;
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity entityLiving, Hand hand) {
        ItemStack stack = entityLiving.getActiveItem();
        if (worldIn.isClient) {
            this.dorightclick(stack, worldIn, entityLiving, hand == Hand.MAIN_HAND);
        }
        entityLiving.setCurrentHand(hand);
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (worldIn.isClient) {
//			stack.getSubCompound("grapplemod", true).setBoolean("extended", (this.getArrow(entityLiving, worldIn) != null));
        }
        super.onStoppedUsing(stack, worldIn, entityLiving, timeLeft);
    }

    /**
     * returns the action that specifies what animation to play when the items
     * is being used
     */
    @Override
    public UseAction getUseAction(ItemStack par1ItemStack) {
        return UseAction.NONE;
    }

    @Override
    public ActionResult useOnEntity(ItemStack item, PlayerEntity player, LivingEntity livingEntity, Hand hand) {
        return ActionResult.PASS;
    }

    @Override
    public void onCustomKeyDown(ItemStack stack, PlayerEntity player, KeypressItem.Keys key, boolean ismainhand) {
        if (!player.world.isClient) {
            if (key == KeypressItem.Keys.LAUNCHER) {
                if (this.getCustomization(stack).enderstaff) {
                    GrappleMod.proxy.launchplayer(player);
                }
            } else if (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT ||
                       key == KeypressItem.Keys.THROWBOTH) {
                new KeypressMessage(key, true).send();
            } else if (key == KeypressItem.Keys.ROCKET) {
                GrappleCustomization custom = this.getCustomization(stack);
                if (custom.rocket) {
                    GrappleMod.proxy.startRocket(player, custom);
                }
            }
        } else {
            if (key == KeypressItem.Keys.THROWBOTH) {
                throwBoth(stack, player.world, player, ismainhand);
            } else if (key == KeypressItem.Keys.THROWLEFT) {
                GrappleArrow arrow1 = getArrowLeft(player);

                if (arrow1 != null) {
                    detachLeft(player);
                    return;
                }

                boolean threw = throwLeft(stack, player.world, player, ismainhand);

                if (threw) {
                    stack.damage(1, player, x -> {}); // TODO
                    player.world.playSound((PlayerEntity) null,
                                           player.getPos().x,
                                           player.getPos().y,
                                           player.getPos().z,
                                           SoundEvents.ENTITY_ARROW_SHOOT,
                                           SoundCategory.NEUTRAL,
                                           1.0F,
                                           1.0F / (RANDOM.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
                }
            } else if (key == KeypressItem.Keys.THROWRIGHT) {
                GrappleArrow arrow2 = getArrowRight(player);

                if (arrow2 != null) {
                    detachRight(player);
                    return;
                }

                throwRight(stack, player.world, player, ismainhand);

                stack.damage(1, player, x -> {}); // TODO
                player.world.playSound((PlayerEntity) null,
                                       player.getPos().x,
                                       player.getPos().y,
                                       player.getPos().z,
                                       SoundEvents.ENTITY_ARROW_SHOOT,
                                       SoundCategory.NEUTRAL,
                                       1.0F,
                                       1.0F / (RANDOM.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
            }
        }
    }

    @Override
    public void onCustomKeyUp(ItemStack stack, PlayerEntity player, KeypressItem.Keys key, boolean ismainhand) {
        if (!player.world.isClient) {
            if (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT ||
                key == KeypressItem.Keys.THROWBOTH) {
                new KeypressMessage(key, false).send();
            }
        } else {
            GrappleCustomization custom = this.getCustomization(stack);

            if (custom.detachonkeyrelease) {
                GrappleArrow arrow_left = getArrowLeft(player);
                GrappleArrow arrow_right = getArrowRight(player);

                if (key == KeypressItem.Keys.THROWBOTH) {
                    detachBoth(player);
                } else if (key == KeypressItem.Keys.THROWLEFT) {
                    if (arrow_left != null)
                        detachLeft(player);
                } else if (key == KeypressItem.Keys.THROWRIGHT) {
                    if (arrow_right != null)
                        detachRight(player);
                }
            }
        }
    }

//    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

//    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos k, PlayerEntity player) {
        return true;
    }

    public GrappleCustomization getCustomization(ItemStack itemstack) {
        if (itemstack.hasTag()) {
            GrappleCustomization custom = new GrappleCustomization();
            custom.loadNBT(itemstack.getTag());
            return custom;
        } else {
            GrappleCustomization custom = this.getDefaultCustomization();

            CompoundTag nbt = custom.toCompoundTag();

            itemstack.setTag(nbt);

            return custom;
        }
    }

    public GrappleCustomization getDefaultCustomization() {
        return new GrappleCustomization();
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> list, TooltipContext par4) {
        GrappleCustomization custom = getCustomization(stack);

        if (MinecraftClient.getInstance().options.keySneak.isPressed()) { // GLFW.GLFW_KEY_LEFT_SHIFT
            if (!custom.detachonkeyrelease) {
                list.add(new LiteralText(ClientProxyClass.keyBothThrow.getBoundKeyLocalizedText() + " " +
                                         GrappleMod.proxy.localize("grappletooltip.throw.desc")));
                list.add(new LiteralText(ClientProxyClass.keyBothThrow.getBoundKeyLocalizedText() + " " +
                                         GrappleMod.proxy.localize("grappletooltip.release.desc")));
                list.add(new LiteralText(GrappleMod.proxy.localize("grappletooltip.double.desc") +
                                         ClientProxyClass.keyBothThrow.getBoundKeyLocalizedText() + " " +
                                         GrappleMod.proxy.localize("grappletooltip.releaseandthrow.desc")));
            } else {
                list.add(new LiteralText(ClientProxyClass.keyBothThrow.getBoundKeyLocalizedText() + " " +
                                         GrappleMod.proxy.localize("grappletooltip.throwhold.desc")));
            }
            list.add(new LiteralText(GrappleMod.proxy.getKeyName(CommonProxyClass.Keys.keyBindForward) + ", " +
                                     GrappleMod.proxy.getKeyName(CommonProxyClass.Keys.keyBindLeft) + ", " +
                                     GrappleMod.proxy.getKeyName(CommonProxyClass.Keys.keyBindBack) + ", " +
                                     GrappleMod.proxy.getKeyName(CommonProxyClass.Keys.keyBindRight) + " " +
                                     GrappleMod.proxy.localize("grappletooltip.swing.desc")));
            list.add(new LiteralText(ClientProxyClass.keyJumpAndDetach.getBoundKeyLocalizedText() + " " +
                                     GrappleMod.proxy.localize("grappletooltip.jump.desc")));
            list.add(new LiteralText(ClientProxyClass.keySlow.getBoundKeyLocalizedText() + " " +
                                     GrappleMod.proxy.localize("grappletooltip.slow.desc")));
            list.add(new LiteralText((custom.climbkey ? ClientProxyClass.keyClimb.getBoundKeyLocalizedText() + " + " : "") +
                                     ClientProxyClass.keyClimbUp.getBoundKeyLocalizedText() + " " +
                                     GrappleMod.proxy.localize("grappletooltip.climbup.desc")));
            list.add(new LiteralText((custom.climbkey ? ClientProxyClass.keyClimb.getBoundKeyLocalizedText() + " + " : "") +
                                     ClientProxyClass.keyClimbDown.getBoundKeyLocalizedText() + " " +
                                     GrappleMod.proxy.localize("grappletooltip.climbdown.desc")));
            if (custom.enderstaff) {
                list.add(new LiteralText(ClientProxyClass.keyEnderLaunch.getBoundKeyLocalizedText() + " " +
                                         GrappleMod.proxy.localize("grappletooltip.enderlaunch.desc")));
            }
            if (custom.rocket) {
                list.add(new LiteralText(ClientProxyClass.keyRocket.getBoundKeyLocalizedText() + " " +
                                         GrappleMod.proxy.localize("grappletooltip.rocket.desc")));
            }
            if (custom.motor) {
                if (custom.motorwhencrouching && !custom.motorwhennotcrouching) {
                    list.add(new LiteralText(ClientProxyClass.keyMotorOnOff.getBoundKeyLocalizedText() + " " +
                                             GrappleMod.proxy.localize("grappletooltip.motoron.desc")));
                } else if (!custom.motorwhencrouching && custom.motorwhennotcrouching) {
                    list.add(new LiteralText(ClientProxyClass.keyMotorOnOff.getBoundKeyLocalizedText() + " " +
                                             GrappleMod.proxy.localize("grappletooltip.motoroff.desc")));
                }
            }
            if (custom.doublehook) {
                if (!custom.detachonkeyrelease) {
                    list.add(new LiteralText(ClientProxyClass.keyLeftThrow.getBoundKeyLocalizedText() + " " +
                                             GrappleMod.proxy.localize("grappletooltip.throwleft.desc")));
                    list.add(new LiteralText(ClientProxyClass.keyRightThrow.getBoundKeyLocalizedText() + " " +
                                             GrappleMod.proxy.localize("grappletooltip.throwright.desc")));
                } else {
                    list.add(new LiteralText(ClientProxyClass.keyLeftThrow.getBoundKeyLocalizedText() + " " +
                                             GrappleMod.proxy.localize("grappletooltip.throwlefthold.desc")));
                    list.add(new LiteralText(ClientProxyClass.keyRightThrow.getBoundKeyLocalizedText() + " " +
                                             GrappleMod.proxy.localize("grappletooltip.throwrighthold.desc")));
                }
            } else {
                list.add(new LiteralText(ClientProxyClass.keyRightThrow.getBoundKeyLocalizedText() + " " +
                                         GrappleMod.proxy.localize("grappletooltip.throwalt.desc")));
            }
        } else {
            if (MinecraftClient.getInstance().options.keyAdvancements.isPressed()) { // GLFW.GLFW_KEY_LEFT_CONTROL
                for (String option : GrappleCustomization.booleanoptions) {
                    if (custom.isoptionvalid(option) && custom.getBoolean(option)) {
                        list.add(new LiteralText(GrappleMod.proxy.localize(custom.getName(option))));
                    }
                }
                for (String option : GrappleCustomization.doubleoptions) {
                    if (custom.isoptionvalid(option)) {
                        list.add(new LiteralText(GrappleMod.proxy.localize(custom.getName(option)) + ": " +
                                                 Math.floor(custom.getDouble(option) * 100) / 100));
                    }
                }
            } else {
                if (custom.doublehook) {
                    list.add(new LiteralText(GrappleMod.proxy.localize(custom.getName("doublehook"))));
                }
                if (custom.motor) {
                    if (custom.smartmotor) {
                        list.add(new LiteralText(GrappleMod.proxy.localize(custom.getName("smartmotor"))));
                    } else {
                        list.add(new LiteralText(GrappleMod.proxy.localize(custom.getName("motor"))));
                    }
                }
                if (custom.enderstaff) {
                    list.add(new LiteralText(GrappleMod.proxy.localize(custom.getName("enderstaff"))));
                }
                if (custom.rocket) {
                    list.add(new LiteralText(GrappleMod.proxy.localize(custom.getName("rocket"))));
                }
                if (custom.attract) {
                    list.add(new LiteralText(GrappleMod.proxy.localize(custom.getName("attract"))));
                }
                if (custom.repel) {
                    list.add(new LiteralText(GrappleMod.proxy.localize(custom.getName("repel"))));
                }

                list.add(new LiteralText(""));
                list.add(new LiteralText(GrappleMod.proxy.localize("grappletooltip.shiftcontrols.desc")));
                list.add(new LiteralText(GrappleMod.proxy.localize("grappletooltip.controlconfiguration.desc")));
            }
        }

    }

//	@Override
    @Environment(value = EnvType.CLIENT)
    public ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        this.getCustomization(stack);
        return stack;
    }

    @Override
    public void appendStacks(ItemGroup tab, DefaultedList<ItemStack> items) {
        if (this.isIn(tab)) {
            ItemStack stack = new ItemStack(this);
            this.getCustomization(stack);
            items.add(stack);
        }
    }
}
