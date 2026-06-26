package com.example.flywheel_of_terror.mixin;

import java.util.Random;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
   value = {LivingEntity.class},
   priority = 2000
)
public abstract class wrong_die {
   public Random random = new Random();
   @Shadow
   public int hurtDuration;
   @Shadow
   public int hurtTime;

   @Shadow
   protected abstract void dropAllDeathLoot(DamageSource var1);

   @Shadow
   protected abstract void createWitherRose(LivingEntity var1);
}
