package com.example.flywheel_of_terror.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {LivingEntity.class},
   priority = 10000
)
public class scarecrow2 {
   // SRG m_6153_ = LivingEntity#tickDeath. The original @Overwrite replaced the whole method
   // body, so we inject at HEAD, reproduce that body, and cancel vanilla tickDeath(). This
   // keeps scarecrow-tagged (value 5) entities frozen in their death animation instead of being
   // removed. remap = false because the SRG name is written directly (no mixin refmap here).
   @Inject(method = "m_6153_", at = @At("HEAD"), cancellable = true, remap = false)
   private void flywheel$scarecrowTickDeath(CallbackInfo ci) {
      LivingEntity bebra = (LivingEntity)(Object)this;
      if (bebra.deathTime <= 20) {
         bebra.deathTime++;
      }

      if (bebra.deathTime >= 20 && !bebra.level().isClientSide() && !bebra.isRemoved() && bebra.getPersistentData().getInt("scarecrow") != 5) {
         bebra.level().broadcastEntityEvent(bebra, (byte)60);
         bebra.remove(RemovalReason.KILLED);
      }

      ci.cancel();
   }
}
