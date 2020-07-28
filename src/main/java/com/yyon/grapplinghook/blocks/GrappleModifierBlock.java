
package com.yyon.grapplinghook.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.items.GrappleBow;
import com.yyon.grapplinghook.items.upgrades.BaseUpgradeItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Property;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;


public class GrappleModifierBlock extends Block implements BlockEntityProvider {

    public GrappleModifierBlock(Settings properties) {
        super(Settings.of(Material.STONE));
//        setCreativeTab(GrappleMod.tabGrapplemod);
    }

    // Called when the block is placed or loaded client side to get the tile
    // entity
    // for the block
    // Should return a new instance of the tile entity for the block
    @Override
    public BlockEntity createBlockEntity(BlockView state) {
        return new GrappleModifierBlockEntity();
    }

    class MyBlockState extends AbstractBlockState {

        protected MyBlockState(Block block,
                ImmutableMap<Property<?>, Comparable<?>> immutableMap,
                MapCodec<BlockState> mapCodec) {
            super(block, immutableMap, mapCodec);
        }

        @Override
        protected BlockState asBlockState() {
            return null;
        }

        // the block will render in the SOLID layer. See
        // http://greyminecraftcoder.blogspot.co.at/2014/12/block-rendering-18.html
        // for
        // more information.
//        @Environment(value = EnvType.CLIENT)
//        public BlockRenderLayer getBlockLayer() {
//            return BlockRenderLayer.SOLID;
//        }

        @Override
        public boolean isOpaqueFullCube(BlockView view, BlockPos pos) {
            return true;
        }

        @Override
        public boolean isFullCube(BlockView view, BlockPos pos) {
            return true;
        }

        // render using a BakedModel
        // not required because the default (super method) is MODEL
        @Override
        public BlockRenderType getRenderType() {
            return BlockRenderType.MODEL;
        }

        @Override
        public List<ItemStack> getDroppedStacks(LootContext.Builder builder) {
            List<ItemStack> drops = new ArrayList<>();
            BlockEntity entity = builder.getWorld().getBlockEntity(builder.get(LootContextParameters.POSITION));
            GrappleModifierBlockEntity blockEntity = (GrappleModifierBlockEntity) entity;

            for (GrappleMod.UpgradeCategories category : GrappleMod.UpgradeCategories.values()) {
                if (blockEntity.unlockedCategories.containsKey(category) && blockEntity.unlockedCategories.get(category)) {
                    drops.add(new ItemStack(category.item));
                }
            }

            return drops;
        }

        @Override
        public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
            super.onBlockBreakStart(world, pos, player);
        }

        /**
         * Spawns the block's drops in the world. By the time this is called the
         * Block has possibly been set to air via Block.removedByPlayer
         */
//    @Override
//        public void harvestBlock(World world,
//                                 PlayerEntity player,
//                                 BlockPos pos,
//                                 BlockState state,
//                                 @Nullable Entity te,
//                                 ItemStack tool) {
//            super.harvestBlock(world, player, pos, state, te, tool);
//            world.setBlockToAir(pos);
//        }

        @Override
        public ActionResult onUse(World worldIn, PlayerEntity playerIn, Hand hand, BlockHitResult hitResult) {
            ItemStack helditemstack = playerIn.getMainHandStack();
            Item helditem = helditemstack.getItem();

            if (helditem instanceof BaseUpgradeItem) {
                if (worldIn.isClient) {
                    BlockEntity ent = worldIn.getBlockEntity(hitResult.getBlockPos());
                    GrappleModifierBlockEntity tileent = (GrappleModifierBlockEntity) ent;

                    GrappleMod.UpgradeCategories category = ((BaseUpgradeItem) helditem).category;

                    if (tileent.isUnlocked(category)) {
                        playerIn.sendMessage(new LiteralText("Already has upgrade: " + category.description), true);
                    } else {
                        if (!playerIn.isCreative()) {
                            playerIn.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                        }

                        tileent.unlockCategory(category);

                        playerIn.sendMessage(new LiteralText("Applied upgrade: " + category.description), true);
                    }
                }
            } else if (helditem instanceof GrappleBow) {
                if (worldIn.isClient) {
                    BlockEntity ent = worldIn.getBlockEntity(hitResult.getBlockPos());
                    GrappleModifierBlockEntity tileent = (GrappleModifierBlockEntity) ent;

                    GrappleCustomization custom = tileent.customization;
                    CompoundTag nbt = custom.toCompoundTag();

                    helditemstack.setTag(nbt);

                    playerIn.sendMessage(new LiteralText("Applied configuration"), true);
                }
            } else if (helditem == Items.DIAMOND_BOOTS) {
                if (worldIn.isClient) {
                    if (GrappleConfig.getconf().longfallbootsrecipe) {
                        boolean gaveitem = false;
                        if (helditemstack.isEnchantable()) {
                            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(helditemstack);
                            if (enchantments.containsKey(Enchantments.FEATHER_FALLING)) {
                                if (enchantments.get(Enchantments.FEATHER_FALLING) >= 4) {
                                    ItemStack newitemstack = new ItemStack(GrappleMod.longfallboots);
                                    EnchantmentHelper.set(enchantments, newitemstack);
                                    playerIn.equipStack(EquipmentSlot.MAINHAND, newitemstack);
                                    gaveitem = true;
                                }
                            }
                        }
                        if (!gaveitem) {
                            playerIn.sendMessage(new LiteralText("Right click with diamond boots enchanted with feather falling IV to get long fall boots"),
                                                 true);
                        }
                    } else {
                        playerIn.sendMessage(new LiteralText("Making long fall boots this way was disabled in the config. It probably has been replaced by a crafting recipe."),
                                             true);
                    }
                }
            } else {
                BlockEntity ent = worldIn.getBlockEntity(hitResult.getBlockPos());
                GrappleModifierBlockEntity tileent = (GrappleModifierBlockEntity) ent;

                GrappleMod.proxy.openModifierScreen(tileent);
            }

            return ActionResult.SUCCESS;
        }
    }
}
