package com.yyon.grapplinghook.config;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class GrappleConfigUtils {
	private static boolean anyBlocks = true;
	private static Set<Block> grapplingBlocks;
	private static boolean removeBlocks = false;
	private static Set<Block> grapplingBreaksBlocks;
	private static boolean anyBreakBlocks = false;

	public static Set<Block> stringToBlocks(String s) {
		Set<Block> blocks = new HashSet<>();

		if (s.equals("") || s.equals("none") || s.equals("any")) {
			return blocks;
		}

		String[] blockstr = s.split(",");

	    for (String str: blockstr){
	    	str = str.trim();
	    	String modid;
	    	String name;
	    	if (str.contains(":")) {
	    		String[] splitstr = str.split(":");
	    		modid = splitstr[0];
	    		name = splitstr[1];
	    	} else {
	    		modid = "minecraft";
	    		name = str;
	    	}

	    	Block b = Registry.BLOCK.get(new Identifier(modid, name));

	    	blocks.add(b);
	    }

	    return blocks;
	}

	public static void updateGrapplingBlocks() {
		String s = GrappleConfig.getConf().grapplinghook.blocks.grapplingBlocks;
		if (s.equals("any") || s.equals("")) {
			s = GrappleConfig.getConf().grapplinghook.blocks.grapplingNonBlocks;
			if (s.equals("none") || s.equals("")) {
				anyBlocks = true;
			} else {
				anyBlocks = false;
				removeBlocks = true;
			}
		} else {
			anyBlocks = false;
			removeBlocks = false;
		}

		if (!anyBlocks) {
			grapplingBlocks = stringToBlocks(s);
		}

		grapplingBreaksBlocks = stringToBlocks(GrappleConfig.getConf().grapplinghook.blocks.grappleBreakBlocks);
		anyBreakBlocks = grapplingBreaksBlocks.size() != 0;
	}

	private static String prevGrapplingBlocks = null;
	private static String prevGrapplingNonBlocks = null;
	public static boolean attachesBlock(Block block) {
		if (!GrappleConfig.getConf().grapplinghook.blocks.grapplingBlocks.equals(prevGrapplingBlocks) || !GrappleConfig.getConf().grapplinghook.blocks.grapplingNonBlocks.equals(prevGrapplingNonBlocks)) {
			updateGrapplingBlocks();
		}

		if (anyBlocks) {
			return true;
		}

		boolean inlist = grapplingBlocks.contains(block);

		if (removeBlocks) {
			return !inlist;
		} else {
			return inlist;
		}
	}

	private static String prevGrapplingBreakBlocks = null;
	public static boolean breaksBlock(Block block) {
		if (!GrappleConfig.getConf().grapplinghook.blocks.grappleBreakBlocks.equals(prevGrapplingBreakBlocks)) {
			updateGrapplingBlocks();
		}

		if (!anyBreakBlocks) {
			return false;
		}

		return grapplingBreaksBlocks.contains(block);
	}

	public static Rarity getRarityFromInt(int rarity_int) {
		Rarity[] rarities = new Rarity[] {Rarity.VERY_RARE, Rarity.RARE, Rarity.UNCOMMON, Rarity.COMMON};
		if (rarity_int < 0) {rarity_int = 0;}
		if (rarity_int >= rarities.length) {rarity_int = rarities.length - 1;}
		return rarities[rarity_int];
	}
}
