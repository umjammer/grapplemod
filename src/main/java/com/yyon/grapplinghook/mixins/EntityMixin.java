package com.yyon.grapplinghook.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.yyon.grapplinghook.grapplemod.LOGGER;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(at = @At(value = "HEAD"), method = "onSpawnPacket")
    public void onSpawnPacket(EntitySpawnS2CPacket packet, CallbackInfo ci) {
LOGGER.info("EntityMixin::onSpawnPacket: id: " + packet.getEntityTypeId() + ", " + packet.getId());
    }

    @Inject(at = @At(value = "TAIL"), method = "onSpawnPacket")
    void inject(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        Entity entity = ((Entity) (Object) this);
LOGGER.info("EntityMixin::onSpawnPacket: entity: " + entity.getId());
    }
}