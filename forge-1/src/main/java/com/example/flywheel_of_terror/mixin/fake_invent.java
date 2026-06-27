package com.example.flywheel_of_terror.mixin;

import com.example.flywheel_of_terror.information;
import java.util.Random;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {InventoryScreen.class},
   priority = 2000
)
public class fake_invent {
   private static Random random2 = new Random();

   // SRG m_274545_ = InventoryScreen#renderEntityInInventoryFollowsMouse. The original @Overwrite
   // forced a fixed view angle (0,0) instead of following the mouse. Reproduce that and cancel the
   // vanilla method. remap = false: SRG name written directly (no mixin refmap in this workspace).
   // renderEntityInInventoryFollowsAngle is a Forge-added (unobfuscated) method, so it keeps its name.
   @Inject(method = "m_274545_", at = @At("HEAD"), cancellable = true, remap = false)
   private static void flywheel$followsMouse(
      GuiGraphics p_282802_, int p_275688_, int p_275245_, int p_275535_, float p_275604_, float p_275546_, LivingEntity p_275689_, CallbackInfo ci
   ) {
      float f = 0.0F;
      float f1 = 0.0F;
      InventoryScreen.renderEntityInInventoryFollowsAngle(p_282802_, p_275688_, p_275245_, p_275535_, f, f1, p_275689_);
      ci.cancel();
   }

   // SRG m_280432_ = InventoryScreen#renderEntityInInventory. The original @Overwrite gated the whole
   // (vanilla-identical) render body behind information.show_model. Equivalent behaviour: cancel the
   // vanilla render when show_model is false; otherwise let it run unchanged.
   @Inject(method = "m_280432_", at = @At("HEAD"), cancellable = true, remap = false)
   private static void flywheel$hideModel(CallbackInfo ci) {
      if (!information.show_model) {
         ci.cancel();
      }
   }
}
