
package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.GrappleMod;

import net.minecraft.item.Item;


public class BaseUpgradeItem extends Item {
    public String unlocalizedname;

    public GrappleMod.UpgradeCategories category;

    public BaseUpgradeItem() {
        super(new Settings().group(GrappleMod.tabGrapplemod));
        setvars();
    }

    public void setvars() {
        unlocalizedname = "baseupgradeitem";
        category = null;
    }
}
