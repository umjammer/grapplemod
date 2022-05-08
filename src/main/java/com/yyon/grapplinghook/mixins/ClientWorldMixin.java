package com.yyon.grapplinghook.mixins;

import java.util.HashMap;
import java.util.Map;

import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.yyon.grapplinghook.grapplemod.LOGGER;


@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    Map<Integer, Entity> entities = new HashMap<>();

    @Inject(at = @At(value = "TAIL"), method = "addEntity")
    public void addEntity(int id, Entity entity, CallbackInfo ci) {
if (entity instanceof GrapplehookEntity) {
 ClientWorld world = (ClientWorld) (Object) this;
 LOGGER.info("ClientWorldMixin::addEntity: id: " + id + ", " + entity + ", world: " + world.hashCode());
 world.getEntities().forEach(e -> {
  if (e.getId() == id) {
   LOGGER.info("ClientWorldMixin::addEntity: " + e + ", world: " + world.hashCode());
  }
 });
 LOGGER.info("ClientWorldMixin::addEntity: " + world.getEntityById(id) + ", world: " + world.hashCode() + ", lookup: " + world.getEntityLookup().hashCode());
 entities.put(id, entity);
}
    }

    @Inject(at = @At(value = "HEAD"), method = "removeEntity")
     void removeEntity(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci) {
if (entities.containsKey(entityId)) {
 LOGGER.info("ClientWorldMixin::removeEntity: " + entities.get(entityId));
 new Exception().printStackTrace();
 entities.remove(entityId);
}
    }
}