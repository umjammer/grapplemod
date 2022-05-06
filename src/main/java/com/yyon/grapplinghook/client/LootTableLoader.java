package com.yyon.grapplinghook.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.text.html.parser.Entity;

import com.yyon.grapplinghook.blocks.modifierblock.TileEntityGrappleModifier;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import static com.yyon.grapplinghook.common.CommonSetup.grappleModifierTileEntityType;


/**
 * LootTableLoader.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-05-05 nsano initial version <br>
 */
public class LootTableLoader {

    public void onLootTableLoading(ResourceManager resourceManager, LootManager manager, Identifier id, FabricLootSupplierBuilder supplier, LootTableLoadingCallback.LootTableSetter setter) {
//		if (BlockEntityType.getId(grappleModifierTileEntityType).equals(id)) {
//			BlockEntity ent = manager.get(LootContextParameters.BLOCK_ENTITY);
//			if (ent instanceof TileEntityGrappleModifier tileent) {
//				for (GrappleCustomization.upgradeCategories category : GrappleCustomization.upgradeCategories.values()) {
//					if (tileent.unlockedCategories.containsKey(category) && tileent.unlockedCategories.get(category)) {
//						FabricLootPoolBuilder builder = FabricLootPoolBuilder.builder()
//								.rolls(UniformLootNumberProvider.create(0, 2))
//								.with(ItemEntry.builder(category.getItem()));
//						supplier.pool(builder);
//					}
//				}
//			}
//		}
    }
}
