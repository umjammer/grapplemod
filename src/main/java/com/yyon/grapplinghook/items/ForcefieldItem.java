package com.yyon.grapplinghook.items;

import java.util.List;

import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.utils.GrapplemodUtils;
import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.yyon.grapplinghook.client.ClientProxy.McKeys;
import static com.yyon.grapplinghook.client.ClientSetup.clientProxy;


public class ForcefieldItem extends Item {
	public ForcefieldItem(Settings settings) {
		super(settings.maxCount(1));
	}

	public void doRightClick(ItemStack stack, World worldIn, PlayerEntity player) {
		if (worldIn.isClient()) {
			int playerid = player.getId();
			GrappleController oldController = clientProxy.unregisterController(playerid);
			if (oldController == null || oldController.controllerId == GrapplemodUtils.AIRID) {
				clientProxy.createControl(GrapplemodUtils.REPELID, -1, playerid, worldIn, new Vec(0,0,0), null, null);
			}
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
		tooltip.add(new TranslatableText("grappletooltip.repelleritem.desc"));
		tooltip.add(new TranslatableText("grappletooltip.repelleritem2.desc"));
		tooltip.add(Text.of(""));
		tooltip.add(new TranslatableText(clientProxy.getKeyname(McKeys.keyBindUseItem)).append(new TranslatableText("grappletooltip.repelleritemon.desc")));
		tooltip.add(new TranslatableText(clientProxy.getKeyname(McKeys.keyBindUseItem)).append(new TranslatableText("grappletooltip.repelleritemoff.desc")));
		tooltip.add(new TranslatableText(clientProxy.getKeyname(McKeys.keyBindSneak)).append(new TranslatableText("grappletooltip.repelleritemslow.desc")));
		tooltip.add(new TranslatableText(clientProxy.getKeyname(McKeys.keyBindForward)).append(", ").append(
				new TranslatableText(clientProxy.getKeyname(McKeys.keyBindLeft)).append(", ")).append(
				new TranslatableText(clientProxy.getKeyname(McKeys.keyBindBack)).append(", ")).append(
				new TranslatableText(clientProxy.getKeyname(McKeys.keyBindRight)).append(" ")).append(
				new TranslatableText("grappletooltip.repelleritemmove.desc")));
	}
}
