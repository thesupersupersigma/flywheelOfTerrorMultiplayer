package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.terror_cow;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class terror_cow_renderer extends MobRenderer<terror_cow, CowModel<terror_cow>> {
   private static final ResourceLocation texture1 = new ResourceLocation("flywheel_of_terror", "textures/entity/terror_cow.png");

   public terror_cow_renderer(Context context) {
      super(context, new CowModel(context.bakeLayer(ModelLayers.COW)), 0.5F);
   }

   public ResourceLocation getTextureLocation(terror_cow entity) {
      return texture1;
   }
}
