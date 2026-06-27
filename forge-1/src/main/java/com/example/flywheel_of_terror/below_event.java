package com.example.flywheel_of_terror;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class below_event {
   // Per-player state → NBT ("below_active/2", "below_escape", "below_time_to_end", "below_XC/ZC",
   // "below_height"). Phase 3: the "you hit the bottom" sound is now an S2C packet.
   public static void set_active(Player player, boolean value) {
      state.putBool(player, "below_active", value);
   }

   @SubscribeEvent
   public static void declineblocks(EntityPlaceEvent event) {
      if (event.getEntity() instanceof Player player && state.getBool(player, "below_escape")) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void declinedamage(LivingDamageEvent event) {
      if (event.getEntity() instanceof Player player && state.getBool(player, "below_escape")) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void everyrime(PlayerTickEvent event) {
      Player player = event.player;
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      boolean exclude_escape = tag.getBoolean("below_escape");
      int time_to_end = tag.getInt("below_time_to_end");
      double height = tag.getDouble("below_height");
      if (!player.level().isClientSide() && player.tickCount % 40 == 0 && exclude_escape) {
         tag.putInt("below_time_to_end", time_to_end - 1);
         global_tag.put("flywheel_of_terror", tag);
      }

      if (exclude_escape && time_to_end >= 3 && player.getY() < height - 8.0 && !player.level().isClientSide()) {
         player.teleportTo((double)tag.getInt("below_XC"), height + 5.0, (double)tag.getInt("below_ZC"));
      }

      if (time_to_end <= 0 && exclude_escape && !player.level().isClientSide()) {
         tag.putBoolean("below_escape", false);
         global_tag.put("flywheel_of_terror", tag);
         MobEffectInstance blind = new MobEffectInstance(MobEffects.BLINDNESS, 4, 10, false, true, false);
         player.addEffect(blind);
      }

      if (tag.getBoolean("below_active") && player.getY() > 20.0 && !player.level().isClientSide()) {
         paranoia.set_seconds_to_call(player, 60);
         tag.putDouble("below_height", player.getY() - 60.0);
         tag.putBoolean("below_active", false);
         tag.putBoolean("below_active2", true);
         global_tag.put("flywheel_of_terror", tag);
         player.heal(20.0F);
         Network.sound(player, (SoundEvent)register_sounds.below1.get());
         MobEffectInstance blind = new MobEffectInstance(MobEffects.BLINDNESS, 740, 10, false, true, false);
         player.addEffect(blind);
      }

      if (tag.getBoolean("below_active2") && player.tickCount % 5 == 0 && !player.level().isClientSide()) {
         tag.putBoolean("below", true);

         for (double x = player.getX() - 2.0; x <= player.getX() + 2.0; x++) {
            for (double y = player.getY() - 5.0; y <= player.getY() + 5.0; y++) {
               for (double z = player.getZ() - 2.0; z <= player.getZ() + 2.0; z++) {
                  BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                  player.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
               }
            }
         }

         for (double x = player.getX() - 10.0; x <= player.getX() + 10.0; x++) {
            for (double y = player.getY() - 1.0; y <= player.getY() + 1.0; y++) {
               for (double z = player.getZ() - 10.0; z <= player.getZ() + 10.0; z++) {
                  BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                  if (player.level().getBlockState(pos).getBlock() != Blocks.WATER && player.level().getBlockState(pos).getBlock() == Blocks.LAVA) {
                  }

                  player.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
               }
            }
         }

         player.teleportTo(player.getX(), player.getY() - 1.0, player.getZ());
         if (player.getY() <= tag.getDouble("below_height")) {
            Network.sound(player, (SoundEvent)register_sounds.below2.get());

            for (double x = player.getX() - 20.0; x <= player.getX() + 20.0; x++) {
               for (double y = player.getY() - 20.0; y <= player.getY() + 20.0; y++) {
                  for (double z = player.getZ() - 20.0; z <= player.getZ() + 20.0; z++) {
                     BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                     player.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                  }
               }
            }

            for (double x = player.getX() - 1.0; x <= player.getX() + 1.0; x++) {
               for (double y = player.getY() - 4.0; y <= player.getY() - 4.0; y++) {
                  for (double z = player.getZ() - 1.0; z <= player.getZ() + 1.0; z++) {
                     BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                     player.level().setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
                  }
               }
            }

            tag.putInt("below_XC", (int)player.getX());
            tag.putInt("below_ZC", (int)player.getZ());
            tag.putBoolean("below_escape", true);
            tag.putInt("below_time_to_end", 29);
            tag.putBoolean("below_active2", false);
            global_tag.put("flywheel_of_terror", tag);
            paranoia.set_seconds_to_call(player, 60);
         }
      }
   }
}
