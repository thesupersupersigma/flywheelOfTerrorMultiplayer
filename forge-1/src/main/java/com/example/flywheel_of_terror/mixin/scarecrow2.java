package com.example.flywheel_of_terror.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(
   value = {LivingEntity.class},
   priority = 10000
)
public class scarecrow2 {
   @Overwrite
   protected void tickDeath() {
      LivingEntity bebra = (LivingEntity)(Object)this;
      if (bebra.deathTime <= 20) {
         bebra.deathTime++;
      }

      if (bebra.deathTime >= 20 && !bebra.level().isClientSide() && !bebra.isRemoved() && bebra.getPersistentData().getInt("scarecrow") != 5) {
         bebra.level().broadcastEntityEvent(bebra, (byte)60);
         bebra.remove(RemovalReason.KILLED);
      }
   }
}
