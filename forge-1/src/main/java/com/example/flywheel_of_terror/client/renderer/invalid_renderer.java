package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.invalid;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class invalid_renderer extends MobRenderer<invalid, ZombieModel<invalid>> {
   private static final ResourceLocation texture1 = new ResourceLocation("flywheel_of_terror", "textures/entity/invalid.png");

   public invalid_renderer(Context context) {
      super(context, new ZombieModel(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
   }

   public ResourceLocation getTextureLocation(invalid entity) {
      return texture1;
   }
}
