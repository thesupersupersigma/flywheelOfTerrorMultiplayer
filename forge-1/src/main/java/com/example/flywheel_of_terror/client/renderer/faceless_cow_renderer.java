package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.faceless_cow;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class faceless_cow_renderer extends MobRenderer<faceless_cow, CowModel<faceless_cow>> {
   private static final ResourceLocation texture1 = new ResourceLocation("flywheel_of_terror", "textures/entity/faceless_cow.png");

   public faceless_cow_renderer(Context context) {
      super(context, new CowModel(context.bakeLayer(ModelLayers.COW)), 0.5F);
   }

   public ResourceLocation getTextureLocation(faceless_cow entity) {
      return texture1;
   }
}
