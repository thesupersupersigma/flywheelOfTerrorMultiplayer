package com.example.flywheel_of_terror;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.Entity;
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
      // Server-authoritative: decrement the window, and while it is open (a) send an ALL_LOOK
      // FxPacket so this player's client forces third-person, and (b) rotate every nearby entity to
      // stare at the player here on the server so every client sees the same rotation. The camera is
      // client-only, so we use the player's eye position as the look target instead.
      if (!player.level().isClientSide()) {
         int looking = state.getInt(player, "tics_of_looking") - 1;
         state.putInt(player, "tics_of_looking", looking);
         if (looking > 0) {
            Network.fx(player, Network.ALL_LOOK);

            for (Entity mob : player.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(300.0))) {
               mob.lookAt(Anchor.EYES, player.getEyePosition());
            }
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
