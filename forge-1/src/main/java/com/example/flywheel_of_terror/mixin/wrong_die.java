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
   // SRG names written directly with remap = false: this workspace generates no mixin refmap, so the
   // official names (hurtDuration/hurtTime/dropAllDeathLoot/createWitherRose) never get translated and
   // fail to bind against the SRG-named runtime members. f_20917_ = hurtDuration, f_20916_ = hurtTime,
   // m_6668_ = dropAllDeathLoot, m_21268_ = createWitherRose.
   @Shadow(remap = false)
   public int f_20917_;
   @Shadow(remap = false)
   public int f_20916_;

   @Shadow(remap = false)
   protected abstract void m_6668_(DamageSource var1);

   @Shadow(remap = false)
   protected abstract void m_21268_(LivingEntity var1);
}
