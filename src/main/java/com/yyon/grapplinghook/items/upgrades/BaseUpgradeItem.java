package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.utils.GrappleCustomization;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BaseUpgradeItem extends Item {

	public GrappleCustomization.upgradeCategories category = null;
	boolean craftingRemaining = false;

	public BaseUpgradeItem(Settings settings, int maxStackSize, GrappleCustomization.upgradeCategories theCategory) {
		super(settings.maxCount(maxStackSize));

		this.category = theCategory;

		if (theCategory != null) {
			this.setCraftingRemainingItem();
		}
	}

	public void setCraftingRemainingItem() {
		craftingRemaining = true;
	}

	@Override
	public ItemStack getDefaultStack() {
        if (!this.craftingRemaining) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(this);
    }

	@Override
	public boolean hasRecipeRemainder() {
		return this.craftingRemaining;
	}

	public BaseUpgradeItem(Settings settings) {
		this(settings, 64, null);
	}
}
