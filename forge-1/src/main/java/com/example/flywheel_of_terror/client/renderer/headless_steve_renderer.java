package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.headless_steve;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class headless_steve_renderer extends HumanoidMobRenderer<headless_steve, PlayerModel<headless_steve>> {
   public headless_steve_renderer(Context context) {
      super(context, new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
   }

   public ResourceLocation getTextureLocation(headless_steve entity) {
      return new ResourceLocation("flywheel_of_terror", "textures/entity/steve.png");
   }
}
