package com.yyon.grapplinghook.blocks.modifierblock;

import java.util.Map;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.items.upgrades.BaseUpgradeItem;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.yyon.grapplinghook.client.ClientSetup.clientProxy;


public class BlockGrappleModifier extends Block implements BlockEntityProvider {

	public BlockGrappleModifier(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityGrappleModifier(pos,state);
	}

    @Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack helditemstack = player.getMainHandStack();
		Item helditem = helditemstack.getItem();

		if (helditem instanceof BaseUpgradeItem) {
			if (!world.isClient()) {
				BlockEntity ent = world.getBlockEntity(pos);
				TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;

				GrappleCustomization.upgradeCategories category = ((BaseUpgradeItem) helditem).category;
				if (category != null) {
					if (tileent.isUnlocked(category)) {
						player.sendMessage(Text.of("Already has upgrade: " + category.getName()), false);
					} else {
						if (!player.isCreative()) {
							player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
						}

						tileent.unlockCategory(category);

						player.sendMessage(Text.of("Applied upgrade: " + category.getName()), false);
					}
				}
			}
		} else if (helditem instanceof GrapplehookItem) {
			if (!world.isClient()) {
				BlockEntity ent = world.getBlockEntity(pos);
				TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;

				GrappleCustomization custom = tileent.customization;
				((GrapplehookItem) CommonSetup.grapplingHookItem).setCustomOnServer(helditemstack, custom, player);

				player.sendMessage(Text.of("Applied configuration"), false);
			}
		} else if (helditem == Items.DIAMOND_BOOTS) {
			if (!world.isClient()) {
				if (GrappleConfig.getConf().longfallboots.longfallbootsrecipe) {
					boolean gaveitem = false;
					if (!helditemstack.isEnchantable()) {
						Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(helditemstack);
						if (enchantments.containsKey(Enchantments.FEATHER_FALLING)) {
							if (enchantments.get(Enchantments.FEATHER_FALLING) >= 4) {
								ItemStack newitemstack = new ItemStack(CommonSetup.longFallBootsItem);
								EnchantmentHelper.set(enchantments, newitemstack);
								player.setStackInHand(Hand.MAIN_HAND, newitemstack);
								gaveitem = true;
							}
						}
					}
					if (!gaveitem) {
						player.sendMessage(Text.of("Right click with diamond boots enchanted with feather falling IV to get long fall boots"), false);
					}
				} else {
					player.sendMessage(Text.of("Making long fall boots this way was disabled in the config. It probably has been replaced by a crafting recipe."), false);
				}
			}
		} else if (helditem == Items.DIAMOND) {
			this.easterEgg(state, world, pos, player, hand, hit);
		} else {
			if (world.isClient()) {
				BlockEntity ent = world.getBlockEntity(pos);
				TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;

				clientProxy.openModifierScreen(tileent);
			}
		}
		return ActionResult.SUCCESS;
	}
    
    @Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.MODEL;
	}

	public void easterEgg(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand,
			BlockHitResult raytraceresult) {
		int spacing = 3;
		Vec[] positions = new Vec[] {new Vec(-spacing*2, 0, 0), new Vec(-spacing, 0, 0), new Vec(0, 0, 0), new Vec(spacing, 0, 0), new Vec(2*spacing, 0, 0)};
		int[] colors = new int[] {0x5bcffa, 0xf5abb9, 0xffffff, 0xf5abb9, 0x5bcffa};

		for (int i = 0; i < positions.length; i++) {
			Vec newpos = new Vec(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			Vec toPlayer = Vec.positionVec(playerIn).sub(newpos);
			double angle = toPlayer.length() == 0 ? 0 : toPlayer.getYaw();
			newpos = newpos.add(positions[i].rotateYaw(Math.toRadians(angle)));

			NbtCompound explosion = new NbtCompound();
	        explosion.putByte("Type", (byte) FireworkRocketItem.Type.SMALL_BALL.getId());
	        explosion.putBoolean("Trail", true);
	        explosion.putBoolean("Flicker", false);
	        explosion.putIntArray("Colors", new int[] {colors[i]});
	        explosion.putIntArray("FadeColors", new int[] {});
	        NbtList list = new NbtList();
	        list.add(explosion);
			NbtCompound fireworks = new NbtCompound();
	        fireworks.put("Explosions", list);
			NbtCompound nbt = new NbtCompound();
	        nbt.put("Fireworks", fireworks);
	        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
	        stack.setNbt(nbt);
			FireworkRocketEntity firework = new FireworkRocketEntity(worldIn, playerIn, newpos.x, newpos.y, newpos.z, stack);
			NbtCompound fireworksave = new NbtCompound();
			firework.readCustomDataFromNbt(fireworksave);
			fireworksave.putInt("LifeTime", 15);
			firework.readCustomDataFromNbt(fireworksave);
			worldIn.spawnEntity(firework);
		}
	}
}
