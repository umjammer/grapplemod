
package com.yyon.grapplinghook.items;

import java.util.List;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.Vec;
import com.yyon.grapplinghook.controllers.GrappleController;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;


public class Repeller extends Item {

    public Repeller() {
        super(new Settings().maxCount(1).maxDamage(500).group(GrappleMod.tabGrapplemod));
//        setUnlocalizedName("repeller");
    }

    @Override
    public int getMaxUseTime(ItemStack par1ItemStack) {
        return 72000;
    }

    public void dorightclick(ItemStack stack, World worldIn, PlayerEntity player) {
        if (!worldIn.isClient) {
            int playerid = player.getEntityId();
            if (GrappleMod.controllers.containsKey(playerid) &&
                GrappleMod.controllers.get(playerid).controllerId != GrappleMod.AIRID) {
                GrappleController controller = GrappleMod.controllers.get(playerid);
                controller.unattach();
            } else {
                GrappleMod.createControl(GrappleMod.REPELID, -1, playerid, worldIn, new Vec(0, 0, 0), null, null);
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getActiveItem();
        this.dorightclick(itemStackIn, worldIn, playerIn);

        return new TypedActionResult<>(ActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public UseAction getUseAction(ItemStack par1ItemStack) {
        return UseAction.NONE;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> list, TooltipContext par4) {
        list.add(new LiteralText("Player is repelled by nearby blocks"));
        list.add(new LiteralText("Can be used with ender staff"));
        list.add(new LiteralText(""));
        list.add(new LiteralText(GrappleMod.proxy.getKeyName(CommonProxyClass.Keys.keyBindUseItem) + " - Turn on"));
        list.add(new LiteralText(GrappleMod.proxy.getKeyName(CommonProxyClass.Keys.keyBindUseItem) + " again - Turn off"));
        list.add(new LiteralText(GrappleMod.proxy.getKeyName(CommonProxyClass.Keys.keyBindSneak) + " - Slow down"));
        list.add(new LiteralText(GrappleMod.proxy.getKeyName(CommonProxyClass.Keys.keyBindForward) + ", " +
                                 GrappleMod.proxy.getKeyName(CommonProxyClass.Keys.keyBindLeft) + ", " +
                                 GrappleMod.proxy.getKeyName(CommonProxyClass.Keys.keyBindBack) + ", " +
                                 GrappleMod.proxy.getKeyName(CommonProxyClass.Keys.keyBindRight) + " - Move"));
    }
}
