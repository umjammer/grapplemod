
package com.yyon.grapplinghook.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;


public interface KeypressItem {
    enum Keys {
        LAUNCHER,
        THROWLEFT,
        THROWRIGHT,
        THROWBOTH,
        ROCKET
    }

    void onCustomKeyDown(ItemStack stack, PlayerEntity player, Keys key, boolean ismainhand);

    void onCustomKeyUp(ItemStack stack, PlayerEntity player, Keys key, boolean ismainhand);
}
