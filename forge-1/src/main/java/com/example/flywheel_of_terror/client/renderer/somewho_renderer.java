package com.example.flywheel_of_terror.client.renderer;

import com.example.flywheel_of_terror.information;
import com.example.flywheel_of_terror.somewho;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.nbt.CompoundTag;
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
      // Wear the skin of the player this clone is actually haunting (its stored target UUID), so the
      // skin is correct for every viewer rather than always matching the local client's own skin.
      ClientPacketListener connection = Minecraft.getInstance().getConnection();
      CompoundTag tag = entity.getPersistentData();
      if (connection != null && tag.hasUUID(information.target_nbt)) {
         UUID target = tag.getUUID(information.target_nbt);
         PlayerInfo info = connection.getPlayerInfo(target);
         if (info != null) {
            return info.getSkinLocation();
         }
      }

      return new ResourceLocation("textures/entity/steve.png");
   }
}
