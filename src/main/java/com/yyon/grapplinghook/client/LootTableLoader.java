package com.yyon.grapplinghook.client;

import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.LootManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;


/**
 * LootTableLoader.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-05-05 nsano initial version <br>
 */
public class LootTableLoader {

    public void onLootTableLoading(ResourceManager resourceManager, LootManager manager, Identifier id, FabricLootSupplierBuilder supplier, LootTableLoadingCallback.LootTableSetter setter) {
//		List<ItemStack> drops = new ArrayList<>();
//		drops.add(new ItemStack(this.asItem()));
//		BlockEntity ent = manager.get(LootContextParameters.BLOCK_ENTITY);
//		if (!(ent instanceof TileEntityGrappleModifier tileent)) {
//			return drops;
//		}
//
//		for (GrappleCustomization.upgradeCategories category : GrappleCustomization.upgradeCategories.values()) {
//			if (tileent.unlockedCategories.containsKey(category) && tileent.unlockedCategories.get(category)) {
//				drops.add(new ItemStack(category.getItem()));
//			}
//		}
//		return drops;
    }
}
