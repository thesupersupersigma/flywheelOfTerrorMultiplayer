package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.terror_sheep;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class terror_sheep_renderer extends MobRenderer<terror_sheep, SheepModel<terror_sheep>> {
   private static final ResourceLocation texture1 = new ResourceLocation("flywheel_of_terror", "textures/entity/terror_sheep.png");

   public terror_sheep_renderer(Context context) {
      super(context, new SheepModel(context.bakeLayer(ModelLayers.SHEEP)), 0.5F);
   }

   public ResourceLocation getTextureLocation(terror_sheep entity) {
      return texture1;
   }
}
