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
   public static boolean event_in_process = false;
   public static boolean event_in_process2 = false;
   public static boolean sound_must_be = false;
   public static int time_to_end = 29;
   public static boolean exclude_escape = false;
   public static int XC;
   public static int ZC;
   public static double height = 0.0;

   @SubscribeEvent
   public static void declineblocks(EntityPlaceEvent event) {
      if (event.getEntity() instanceof Player player && exclude_escape) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void declinedamage(LivingDamageEvent event) {
      if (event.getEntity() instanceof Player player && exclude_escape) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void everyrime(PlayerTickEvent event) {
      Player player = event.player;
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      if (player.level().isClientSide() && player.tickCount % 40 == 0 && exclude_escape) {
         time_to_end--;
      }

      if (exclude_escape && time_to_end >= 3 && player.getY() < height - 8.0 && !player.level().isClientSide()) {
         player.teleportTo((double)XC, height + 5.0, (double)ZC);
      }

      if (time_to_end <= 0 && exclude_escape && !player.level().isClientSide()) {
         exclude_escape = false;
         MobEffectInstance blind = new MobEffectInstance(MobEffects.BLINDNESS, 4, 10, false, true, false);
         player.addEffect(blind);
      }

      if (sound_must_be && player.level().isClientSide()) {
         player.playSound((SoundEvent)register_sounds.below2.get(), 1.0F, 1.0F);
         sound_must_be = false;
      }

      if (event_in_process && player.getY() > 20.0 && player.level().isClientSide()) {
         paranoia.set_seconds_to_call(player, 60);
         height = player.getY() - 60.0;
         global_tag.put("flywheel_of_terror", tag);
         player.heal(20.0F);
         player.playSound((SoundEvent)register_sounds.below1.get(), 1.0F, 1.0F);
         MobEffectInstance blind = new MobEffectInstance(MobEffects.BLINDNESS, 740, 10, false, true, false);
         player.addEffect(blind);
         event_in_process = false;
         event_in_process2 = true;
      }

      if (event_in_process2 && player.tickCount % 5 == 0 && !player.level().isClientSide()) {
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
         if (player.getY() <= height) {
            sound_must_be = true;

            for (double x = player.getX() - 20.0; x <= player.getX() + 20.0; x++) {
               for (double y = player.getY() - 20.0; y <= player.getY() + 20.0; y++) {
                  for (double z = player.getZ() - 20.0; z <= player.getZ() + 20.0; z++) {
                     BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                     player.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                  }
               }
            }

            exclude_escape = true;

            for (double x = player.getX() - 1.0; x <= player.getX() + 1.0; x++) {
               for (double y = player.getY() - 4.0; y <= player.getY() - 4.0; y++) {
                  for (double z = player.getZ() - 1.0; z <= player.getZ() + 1.0; z++) {
                     BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                     player.level().setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
                  }
               }
            }

            XC = (int)player.getX();
            ZC = (int)player.getZ();
            exclude_escape = true;
            event_in_process2 = false;
            paranoia.set_seconds_to_call(player, 60);
         }
      }
   }
}
