package com.yyon.grapplinghook.client;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.items.KeypressItem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import static com.yyon.grapplinghook.client.ClientSetup.clientControllerManager;


/**
 * @forge.event TickEvent.ClientTickEvent
 */
public class ClientTickEvent {

    boolean[] prevKeys = {false, false, false, false, false};

    public void onEndTick(MinecraftClient client) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (!MinecraftClient.getInstance().isPaused()) {
                clientControllerManager.onClientTick(player);

                if (MinecraftClient.getInstance().currentScreen == null) {
                    // keep in same order as enum from KeypressItem
                    boolean[] keys = {ClientSetup.key_enderlaunch.isPressed(), ClientSetup.key_leftthrow.isPressed(), ClientSetup.key_rightthrow.isPressed(), ClientSetup.key_boththrow.isPressed(), ClientSetup.key_rocket.isPressed()};

                    for (int i = 0; i < keys.length; i++) {
                        boolean iskeydown = keys[i];
                        boolean prevkey = prevKeys[i];

                        if (iskeydown != prevkey) {
                            KeypressItem.Keys key = KeypressItem.Keys.values()[i];

                            ItemStack stack = getKeypressStack(player);
                            if (stack != null) {
                                if (!isLookingAtModifierBlock(player)) {
                                    if (iskeydown) {
                                        ((KeypressItem) stack.getItem()).onCustomKeyDown(stack, player, key, true);
                                    } else {
                                        ((KeypressItem) stack.getItem()).onCustomKeyUp(stack, player, key, true);
                                    }
                                }
                            }
                        }

                        prevKeys[i] = iskeydown;
                    }
                }
            }
        }
    }

    private ItemStack getKeypressStack(PlayerEntity player) {
        if (player != null) {
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
            if (stack != null) {
                Item item = stack.getItem();
                if (item instanceof KeypressItem) {
                    return stack;
                }
            }

            stack = player.getStackInHand(Hand.OFF_HAND);
            if (stack != null) {
                Item item = stack.getItem();
                if (item instanceof KeypressItem) {
                    return stack;
                }
            }
        }
        return null;
    }

    private boolean isLookingAtModifierBlock(PlayerEntity player) {
        HitResult raytraceresult = MinecraftClient.getInstance().crosshairTarget;
        if (raytraceresult != null && raytraceresult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bray = (BlockHitResult) raytraceresult;
            BlockPos pos = bray.getBlockPos();
            BlockState state = player.world.getBlockState(pos);

            return (state.getBlock() == CommonSetup.grappleModifierBlock);
        }
        return false;
    }
}