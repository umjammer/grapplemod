package com.yyon.grapplinghook.mixins;

import java.util.Set;

import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.items.LongFallBoots;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.yyon.grapplinghook.common.CommonSetup.serverControllerManager;


@Mixin(PlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    /**
     * @forge.event LivingDeathEvent
     */
    @Inject(at = @At(value = "TAIL"), method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V")
    void onDeath(DamageSource source, CallbackInfo ci) {
        PlayerEntity entity = (PlayerEntity) (Object) this;
        if (!entity.world.isClient()) {
            int id = entity.getId();
            boolean isconnected = serverControllerManager.allGrapplehookEntities.containsKey(id);
            if (isconnected) {
                Set<GrapplehookEntity> grapplehookEntities = serverControllerManager.allGrapplehookEntities.get(id);
                for (GrapplehookEntity hookEntity: grapplehookEntities) {
                    hookEntity.removeServer();
                }
                grapplehookEntities.clear();

                serverControllerManager.attached.remove(id);

                GrapplehookItem.grapplehookEntitiesLeft.remove(entity);
                GrapplehookItem.grapplehookEntitiesRight.remove(entity);

                new GrappleDetachMessage(id).send((ServerPlayerEntity) entity);
            }
        }
    }

    /**
     * @forge.event LivingHurtEvent
     */
    @Inject(at = @At(value = "HEAD"), method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", cancellable = true)
    void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        for (ItemStack armor : player.getArmorItems()) {
            if (armor != null && armor.getItem() instanceof LongFallBoots) {
                if (source == DamageSource.FLY_INTO_WALL) {
                    // this cancels the fall event so you take no damage
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }

    /**
     * @forge.event LivingFallEvent
     */
    @Inject(at = @At(value = "HEAD"), method = "handleFallDamage", cancellable = true)
    void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        for (ItemStack armor : player.getArmorItems()) {
            if (armor != null && armor.getItem() instanceof LongFallBoots) {
                // this cancels the fall event so you take no damage
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}
