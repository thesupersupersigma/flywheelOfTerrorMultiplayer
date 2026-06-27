package com.example.flywheel_of_terror;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class first_entry {
   @SubscribeEvent
   public static void first(PlayerLoggedInEvent event) {
      Player player = event.getEntity();
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
      if (!tag.getBoolean("first_entry")) {
         paranoia.set_seconds_to_call(player, 299);
         // Seed the per-player intruder-event cooldown (was a static initialised to 4000).
         state.putInt(player, "tics_cooldown", 4000);
         // Seed the per-player paranoia scheduler countdown (was a static initialised randomly).
         paranoia.seed_time_to_event(player);
         // Seed the per-player "something wrong" terrain-reshape countdown.
         state.putInt(player, "seconds_to_change", something_wrong.random.nextInt(180, 360));
      }
   }

   @SubscribeEvent
   public static void ticc(PlayerTickEvent event) {
      CompoundTag global_tag = event.player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putBoolean("first_entry", true);
   }
}
