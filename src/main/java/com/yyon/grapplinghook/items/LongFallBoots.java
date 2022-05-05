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

import java.util.List;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LongFallBoots extends ArmorItem {

	public LongFallBoots(ArmorMaterials material, int type, Settings settings) {
	    super(material, EquipmentSlot.FEET, settings.maxCount(1));
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (stack.isEnchantable()) {
			if (GrappleConfig.getConf().longfallboots.longfallbootsrecipe) {
				tooltip.add(new TranslatableText("grappletooltip.longfallbootsrecipe.desc"));
			}
		}
		tooltip.add(new TranslatableText("grappletooltip.longfallboots.desc"));
	}

	@Override
	public void appendStacks(ItemGroup tab, DefaultedList<ItemStack> items) {
		if (this.isIn(tab)) {
			ItemStack stack = new ItemStack(this);
			items.add(stack);

			stack = new ItemStack(this);
			stack.addEnchantment(CommonSetup.wallrunEnchantment, 1);
			stack.addEnchantment(CommonSetup.doubleJumpEnchantment, 1);
			stack.addEnchantment(CommonSetup.slidingEnchantment, 1);
			items.add(stack);
		}
	}
}
