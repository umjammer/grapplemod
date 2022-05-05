package com.yyon.grapplinghook.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class GrapplemodUtils {

	// TODO entity
	public static BlockHitResult rayTraceBlocks(World world, Vec from, Vec to, Entity entity) {
		HitResult result = world.raycast(new RaycastContext(from.toVec3d(), to.toVec3d(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
		if (result instanceof BlockHitResult blockhit) {
			if (blockhit.getType() != HitResult.Type.BLOCK) {
				return null;
			}
			return blockhit;
		}
		return null;
	}

	public static long getTime(World w) {
		return w.getTime();
	}

	private static int controllerid = 0;
	public static int GRAPPLEID = controllerid++;
	public static int REPELID = controllerid++;
	public static int AIRID = controllerid++;
}
