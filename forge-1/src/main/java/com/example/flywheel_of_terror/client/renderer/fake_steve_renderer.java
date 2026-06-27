package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.fake_steve;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.api.distmarker.Dist;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class fake_steve_renderer extends HumanoidMobRenderer<fake_steve, PlayerModel<fake_steve>> {
   public fake_steve_renderer(Context context) {
      super(context, new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
   }

   public ResourceLocation getTextureLocation(fake_steve entity) {
      return new ResourceLocation("flywheel_of_terror", "textures/entity/steve.png");
   }
}
