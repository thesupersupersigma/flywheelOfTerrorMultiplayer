package com.example.flywheel_of_terror.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {Entity.class},
   priority = 10000
)
public class scarecrow {
   // SRG m_8119_ = Entity#tick. The original @Overwrite replaced the whole method body,
   // so we inject at HEAD, reproduce that body, and cancel vanilla tick(). remap = false
   // because the SRG name is written directly (this workspace generates no mixin refmap).
   @Inject(method = "m_8119_", at = @At("HEAD"), cancellable = true, remap = false)
   private void flywheel$scarecrowFreeze(CallbackInfo ci) {
      Entity self = (Entity)(Object)this;
      if (self.getPersistentData().getInt("scarecrow") <= 5) {
         self.baseTick();
      }

      ci.cancel();
   }
}
