
package com.yyon.grapplinghook.items;

import java.util.List;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleMod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
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


public class LongFallBoots extends ArmorItem {
    public LongFallBoots(ArmorMaterial material, int type) {
        super(material, EquipmentSlot.FEET, new Settings().group(GrappleMod.tabGrapplemod));
//	    this.setUnlocalizedName("longfallboots");
    }

//	@SubscribeEvent
    public void onLivingHurtEvent(/* LivingHurtEvent event */) {
        if (event.getEntity() != null && event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();

            for (ItemStack armor : player.getArmorItems()) {
                if (armor != null && armor.getItem() instanceof LongFallBoots) {
                    if (event.getSource() == DamageSource.FLY_INTO_WALL) {
                        System.out.println("Flew into wall");
                        // this cancels the fall event so you take no damage
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

//	@SubscribeEvent
    public void onLivingFallEvent(/* LivingFallEvent event */) {
        if (event.getEntity() != null && event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();

            for (ItemStack armor : player.getArmorItems()) {
                if (armor != null && armor.getItem() instanceof LongFallBoots) {
                    // this cancels the fall event so you take no damage
                    event.setCanceled(true);
                }
            }
        }
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> list, TooltipContext par4) {
        if (!stack.isEnchantable()) {
            if (GrappleConfig.getconf().longfallbootsrecipe) {
                list.add(new LiteralText("Right click a Grappling Hook Modifier block with Feather Falling IV Diamond Boots to obtain"));
            }
        }
        list.add(new LiteralText("Cancels fall damage when worn"));
    }

    @Override
    public void appendStacks(ItemGroup tab, DefaultedList<ItemStack> items) {
        if (this.isIn(tab)) {
            ItemStack stack = new ItemStack(this);
            items.add(stack);

            stack = new ItemStack(this);
            stack.addEnchantment(GrappleMod.wallrunEnchantment, 1);
            stack.addEnchantment(GrappleMod.doubleJumpEnchantment, 1);
            stack.addEnchantment(GrappleMod.slidingEnchantment, 1);
            items.add(stack);
        }
    }
}
