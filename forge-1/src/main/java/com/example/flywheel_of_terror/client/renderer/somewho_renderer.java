package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.information;
import com.example.flywheel_of_terror.somewho;
import net.minecraft.client.Minecraft;
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
public class somewho_renderer extends HumanoidMobRenderer<somewho, PlayerModel<somewho>> {
   public somewho_renderer(Context context) {
      super(context, new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
   }

   public ResourceLocation getTextureLocation(somewho entity) {
      return information.igrok != null
         ? Minecraft.getInstance().getConnection().getPlayerInfo(information.igrok.getUUID()).getSkinLocation()
         : new ResourceLocation("textures/entity/steve.png");
   }
}
