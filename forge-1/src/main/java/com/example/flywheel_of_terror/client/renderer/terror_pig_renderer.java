package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.terror_pig;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class terror_pig_renderer extends MobRenderer<terror_pig, PigModel<terror_pig>> {
   private static final ResourceLocation texture1 = new ResourceLocation("flywheel_of_terror", "textures/entity/terror_pig.png");

   public terror_pig_renderer(Context context) {
      super(context, new PigModel(context.bakeLayer(ModelLayers.PIG)), 0.5F);
   }

   public ResourceLocation getTextureLocation(terror_pig entity) {
      return texture1;
   }
}
