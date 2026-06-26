package com.example.flywheel_of_terror.mixin;

import com.example.flywheel_of_terror.information;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(
   value = {InventoryScreen.class},
   priority = 2000
)
public class fake_invent {
   private static Random random2 = new Random();

   @Overwrite
   public static void renderEntityInInventoryFollowsMouse(GuiGraphics p_282802_, int p_275688_, int p_275245_, int p_275535_, float p_275604_, float p_275546_, LivingEntity p_275689_) {
      float f = 0.0F;
      float f1 = 0.0F;
      InventoryScreen.renderEntityInInventoryFollowsAngle(p_282802_, p_275688_, p_275245_, p_275535_, f, f1, p_275689_);
   }

   @Overwrite
   public static void renderEntityInInventory(
      GuiGraphics p_282665_, int p_283622_, int p_283401_, int p_281360_, Quaternionf p_281880_, @Nullable Quaternionf p_282882_, LivingEntity p_282466_
   ) {
      if (information.show_model) {
         p_282665_.pose().pushPose();
         p_282665_.pose().translate((double)p_283622_, (double)p_283401_, 50.0);
         p_282665_.pose().mulPoseMatrix(new Matrix4f().scaling((float)p_281360_, (float)p_281360_, (float)(-p_281360_)));
         p_282665_.pose().mulPose(p_281880_);
         Lighting.setupForEntityInInventory();
         EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
         if (p_282882_ != null) {
            p_282882_.conjugate();
            entityrenderdispatcher.overrideCameraOrientation(p_282882_);
         }

         entityrenderdispatcher.setRenderShadow(false);
         RenderSystem.runAsFancy(
            () -> entityrenderdispatcher.render(p_282466_, 0.0, 0.0, 0.0, 0.0F, 1.0F, p_282665_.pose(), p_282665_.bufferSource(), 15728880)
         );
         p_282665_.flush();
         entityrenderdispatcher.setRenderShadow(true);
         p_282665_.pose().popPose();
         Lighting.setupFor3DItems();
      }
   }
}
