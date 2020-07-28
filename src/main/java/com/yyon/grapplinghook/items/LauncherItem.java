
package com.yyon.grapplinghook.items;

import java.util.List;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.GrappleMod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
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


public class LauncherItem extends Item {

    public LauncherItem() {
        super(new Settings().maxCount(1).maxDamage(500).group(GrappleMod.tabGrapplemod));
//		setUnlocalizedName("launcheritem");
    }

    @Override
    public int getMaxUseTime(ItemStack par1ItemStack) {
        return 72000;
    }

    public void dorightclick(ItemStack stack, World worldIn, PlayerEntity player) {
        if (!worldIn.isClient()) {
            GrappleMod.proxy.launchplayer(player);
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getPlayer().getActiveItem();
        this.dorightclick(stack, context.getWorld(), context.getPlayer());

        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity entityLiving, Hand hand) {
        ItemStack stack = entityLiving.getActiveItem();
        this.dorightclick(stack, worldIn, entityLiving);

        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }

    @Override
    public UseAction getUseAction(ItemStack par1ItemStack) {
        return UseAction.NONE;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> list, TooltipContext par4) {
        list.add(new LiteralText("Launches player"));
        list.add(new LiteralText(""));
        list.add(new LiteralText("Use crosshairs to aim"));
        list.add(new LiteralText(GrappleMod.proxy.getKeyName(CommonProxyClass.Keys.keyBindUseItem) + " - Launch player"));
    }
}
