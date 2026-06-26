package com.example.flywheel_of_terror;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class shipwrecked {
   public static boolean red_water = false;
   public static final String shipwrecked_nbt = "shipwrecked_was";
   public static final String may_drown_nbt = "may_drown";
   public static boolean sound_must_be = false;

   public static boolean get_may_drown_player(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return tag.getBoolean("may_drown");
   }

   public static boolean get_shipwrecked(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return tag.getBoolean("shipwrecked_was");
   }

   public static void set_may_drown_player(Player player, boolean state) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putBoolean("may_drown", state);
      global_tag.put("flywheel_of_terror", tag);
   }

   public static void set_shipwrecked(Player player, boolean state) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putBoolean("shipwrecked_was", state);
      global_tag.put("flywheel_of_terror", tag);
   }

   public static void drown_player(Player player, int delay_tics) {
      if (player.tickCount % delay_tics == 0) {
         player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         player.teleportTo(player.getX(), player.getY() - 0.1F, player.getZ());
         player.hurt(player.damageSources().generic(), 0.55F);
      }
   }

   public static boolean player_in_ocean(Player player, int required_radius_of_water) {
      Boolean only_water = true;

      for (Block current_block : information.get_blocks_around_player(player, required_radius_of_water, 0, required_radius_of_water)) {
         if (current_block != Blocks.WATER.defaultBlockState().getBlock()) {
            only_water = false;
         }
      }

      return only_water;
   }

   public static boolean may_shipwrecked(Player player, int area_of_ocean) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return player_in_ocean(player, area_of_ocean) && !get_shipwrecked(player);
   }

   public static void do_ship_wrecked(Player player) {
      paranoia.time_to_event += 300;
      set_may_drown_player(player, true);
      set_shipwrecked(player, true);
      red_water = true;
      sound_must_be = true;
   }

   @SubscribeEvent
   public static void do_event(PlayerTickEvent event) {
      Player player = event.player;
      if (player.level().isClientSide()) {
         if (sound_must_be) {
            sound_must_be = false;
            player.playSound((SoundEvent)register_sounds.shipwrecked.get(), 1.0F, 1.0F);
         }
      } else {
         if (may_shipwrecked(player, 10)) {
            event.player.displayClientMessage(Component.literal("Get back to land immediately"), true);
         }

         if (may_shipwrecked(player, 20)) {
            do_ship_wrecked(player);
         }

         if (get_may_drown_player(player)) {
            for (Boat boat : player.level().getEntitiesOfClass(Boat.class, player.getBoundingBox().inflate(10.0))) {
               boat.remove(RemovalReason.DISCARDED);
            }

            drown_player(player, 4);
         }
      }
   }

   @SubscribeEvent
   public static void return_normal(LivingDeathEvent event) {
      if (event.getEntity() instanceof Player player && get_shipwrecked(player)) {
         red_water = false;
         set_may_drown_player(player, false);
         information.do_a_silence(player);
      }
   }

   @SubscribeEvent
   public static void blood_water(RenderLevelStageEvent event) {
      if (red_water) {
         RenderSystem.setShaderColor(0.5F, 0.1F, 0.1F, 1.0F);
      }
   }
}
