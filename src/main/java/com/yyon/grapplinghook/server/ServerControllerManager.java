package com.yyon.grapplinghook.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ServerControllerManager {
	public Set<Integer> attached = new HashSet<>(); // server side
	public Map<Integer, Set<GrapplehookEntity>> allGrapplehookEntities = new HashMap<>(); // server side

	public void addGrapplehookEntity(int id, GrapplehookEntity hookEntity) {
		if (!allGrapplehookEntities.containsKey(id)) {
			allGrapplehookEntities.put(id, new HashSet<>());
		}
		allGrapplehookEntities.get(id).add(hookEntity);
	}

	public void removeAllMultiHookGrapplehookEntities(int id) {
		if (!allGrapplehookEntities.containsKey(id)) {
			allGrapplehookEntities.put(id, new HashSet<>());
		}
		for (GrapplehookEntity hookEntity : allGrapplehookEntities.get(id)) {
			if (hookEntity != null && hookEntity.isAlive()) {
				hookEntity.removeServer();
			}
		}
		allGrapplehookEntities.put(id, new HashSet<>());
	}

	public void receiveGrappleEnd(int id, World world, Set<Integer> hookEntityIds) {
		if (attached.contains(id)) {
			attached.remove(id);
		}

		for (int hookEntityId : hookEntityIds) {
	      	Entity grapple = world.getEntityById(hookEntityId);
	  		if (grapple instanceof GrapplehookEntity) {
	  			((GrapplehookEntity) grapple).removeServer();
	  		}
		}

  		Entity entity = world.getEntityById(id);
  		if (entity != null) {
      		entity.fallDistance = 0;
  		}

  		removeAllMultiHookGrapplehookEntities(id);
	}
}
