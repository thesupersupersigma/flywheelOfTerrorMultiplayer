package com.example.flywheel_of_terror;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class hunger {
   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      setHalfHunger(event.getEntity());
   }

   @SubscribeEvent
   public static void onPlayerRespawn(PlayerRespawnEvent event) {
      if (!event.isEndConquered()) {
         setHalfHunger(event.getEntity());
      }
   }

   private static void setHalfHunger(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      if (!player.level().isClientSide) {
         if (tag.getInt("state_of_lore") != 0) {
            return;
         }

         player.getFoodData().setFoodLevel(10);
         player.getFoodData().setSaturation(10.0F);
         player.getFoodData().setExhaustion(0.0F);
      }
   }
}
