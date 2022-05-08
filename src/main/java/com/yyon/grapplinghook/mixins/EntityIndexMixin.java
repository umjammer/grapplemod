package com.yyon.grapplinghook.mixins;

import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.entity.EntityIndex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.yyon.grapplinghook.grapplemod.LOGGER;


@Mixin(EntityIndex.class)
public abstract class EntityIndexMixin {

    @Inject(at = @At(value = "HEAD"), method = "add")
    public void add(@Coerce Object entity, CallbackInfo ci) {
if (entity instanceof GrapplehookEntity) {
 LOGGER.info("EntityIndexMixin::add: " + entity);
}
    }

    @Inject(at = @At(value = "HEAD"), method = "remove")
     void remove(@Coerce Object entity, CallbackInfo ci) {
if (entity instanceof GrapplehookEntity) {
 LOGGER.info("EntityIndexMixin::remove: " + entity);
 new Exception().printStackTrace();
}
    }
}