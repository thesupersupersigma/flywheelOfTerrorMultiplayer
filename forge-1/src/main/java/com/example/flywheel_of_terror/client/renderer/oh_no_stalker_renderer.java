package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.oh_no_stalker;
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
public class oh_no_stalker_renderer extends HumanoidMobRenderer<oh_no_stalker, PlayerModel<oh_no_stalker>> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("flywheel_of_terror", "textures/entity/oh_no_behind.png");

   public oh_no_stalker_renderer(Context context) {
      super(context, new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
   }

   public ResourceLocation getTextureLocation(oh_no_stalker entity) {
      return TEXTURE;
   }
}
