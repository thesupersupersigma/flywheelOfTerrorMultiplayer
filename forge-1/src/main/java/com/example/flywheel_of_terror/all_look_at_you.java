package com.example.flywheel_of_terror;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class all_look_at_you {
   // Per-player "everyone stares" window → NBT ("tics_of_looking"), decremented server-authoritatively.
   public static void do_event(Player player) {
      state.putInt(player, "tics_of_looking", 1000);
      paralysis_event.start_paralysis(player, 13);
   }

   @SubscribeEvent
   public static void every(PlayerTickEvent event) {
      Player player = event.player;
      // Server-authoritative: decrement the window, and while it is open send an ALL_LOOK FxPacket
      // so this one player's client forces third-person and makes nearby mobs stare at the camera.
      if (!player.level().isClientSide()) {
         int looking = state.getInt(player, "tics_of_looking") - 1;
         state.putInt(player, "tics_of_looking", looking);
         if (looking > 0) {
            Network.fx(player, Network.ALL_LOOK);
         }
      }
   }

   @SubscribeEvent
   public static void god(LivingHurtEvent event) {
      if (event.getEntity() instanceof Player player && state.getInt(player, "tics_of_looking") > 0) {
         event.setCanceled(true);
      }
   }
}
