package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.headless_villager;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class headless_villager_renderer extends MobRenderer<headless_villager, VillagerModel<headless_villager>> {
   private static final ResourceLocation texture1 = new ResourceLocation("flywheel_of_terror", "textures/entity/headless_villager.png");

   public headless_villager_renderer(Context context) {
      super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
   }

   public ResourceLocation getTextureLocation(headless_villager entity) {
      return texture1;
   }
}
