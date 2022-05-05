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

import com.yyon.grapplinghook.client.ClientProxyInterface.McKeys;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.yyon.grapplinghook.client.ClientSetup.clientProxy;


public class EnderStaffItem extends Item {

	public EnderStaffItem(Settings settings) {
		super(settings.maxCount(1));
	}

	public void doRightClick(ItemStack stack, World worldIn, PlayerEntity player) {
		if (worldIn.isClient()) {
			clientProxy.launchPlayer(player);
		}
	}

    @Override
	public ActionResult useOnBlock(ItemUsageContext context) {
    	ItemStack stack = context.getPlayer().getMainHandStack();
        this.doRightClick(stack, context.getWorld(), context.getPlayer());

    	return ActionResult.SUCCESS;
	}
    
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(new TranslatableText("grappletooltip.launcheritem.desc"));
		tooltip.add(Text.of(""));
		tooltip.add(new TranslatableText("grappletooltip.launcheritemaim.desc"));
		tooltip.add(new LiteralText(clientProxy.getKeyname(McKeys.keyBindUseItem)).append(new TranslatableText("grappletooltip.launcheritemcontrols.desc")));
	}
}
