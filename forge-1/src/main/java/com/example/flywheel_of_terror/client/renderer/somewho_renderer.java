package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.somewho;
import net.minecraft.client.Minecraft;
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
public class somewho_renderer extends HumanoidMobRenderer<somewho, PlayerModel<somewho>> {
   public somewho_renderer(Context context) {
      super(context, new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
   }

   public ResourceLocation getTextureLocation(somewho entity) {
      return Minecraft.getInstance().player != null
         ? Minecraft.getInstance().getConnection().getPlayerInfo(Minecraft.getInstance().player.getUUID()).getSkinLocation()
         : new ResourceLocation("textures/entity/steve.png");
   }
}
