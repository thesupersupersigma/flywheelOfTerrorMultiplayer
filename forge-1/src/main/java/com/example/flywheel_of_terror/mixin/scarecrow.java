package com.example.flywheel_of_terror.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(
   value = {Entity.class},
   priority = 10000
)
public class scarecrow {
   @Overwrite
   public void tick() {
      Entity self = (Entity)(Object)this;
      if (self.getPersistentData().getInt("scarecrow") <= 5) {
         self.baseTick();
      }
   }
}
