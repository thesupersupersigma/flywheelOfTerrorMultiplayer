package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.true_me;
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
public class true_me_renderer extends HumanoidMobRenderer<true_me, PlayerModel<true_me>> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("flywheel_of_terror", "textures/entity/true_me.png");

   public true_me_renderer(Context context) {
      super(context, new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
   }

   public ResourceLocation getTextureLocation(true_me entity) {
      return TEXTURE;
   }
}
