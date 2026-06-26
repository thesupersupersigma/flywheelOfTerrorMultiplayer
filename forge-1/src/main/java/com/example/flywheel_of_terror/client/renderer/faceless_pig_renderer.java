package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.faceless_pig;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class faceless_pig_renderer extends MobRenderer<faceless_pig, PigModel<faceless_pig>> {
   private static final ResourceLocation texture1 = new ResourceLocation("flywheel_of_terror", "textures/entity/faceless_pig.png");

   public faceless_pig_renderer(Context context) {
      super(context, new PigModel(context.bakeLayer(ModelLayers.PIG)), 0.5F);
   }

   public ResourceLocation getTextureLocation(faceless_pig entity) {
      return texture1;
   }
}
