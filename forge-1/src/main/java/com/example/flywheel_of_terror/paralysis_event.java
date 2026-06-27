package com.example.flywheel_of_terror;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class paralysis_event {
   // Freeze timer + pose → per-player NBT ("paral_time" / "paral_yrot/xrot/xpos/ypos/zpos").
   // sound_must_be stays static (A/V).
   public static boolean sound_must_be = true;

   public static void start_paralysis(Player player, int seconds) {
      CompoundTag tag = state.tag(player);
      tag.putInt("paral_time", seconds);
      tag.putFloat("paral_yrot", player.getYRot());
      tag.putFloat("paral_xrot", player.getXRot());
      tag.putDouble("paral_xpos", player.getX());
      tag.putDouble("paral_ypos", player.getY());
      tag.putDouble("paral_zpos", player.getZ());
      state.save(player, tag);
   }

   public static void do_event(Player player, int time_of_paralysis) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putBoolean("paral", true);
      tag.putBoolean("wait_paralysis", false);
      global_tag.put("flywheel_of_terror", tag);
      start_paralysis(player, time_of_paralysis);
      player.level()
         .playSound(null, player.getX(), player.getY(), player.getZ(), (SoundEvent)register_sounds.paralysis.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

      for (LivingEntity mob : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(100.0))) {
         if (!(mob instanceof Player)) {
            mob.kill();
         }
      }
   }

   public static boolean wait_situation(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return player.onGround() && information.block_under_player instanceof GrassBlock && tag.getBoolean("wait_paralysis");
   }

   @SubscribeEvent
   public static void eventdo(PlayerTickEvent event) {
      Player player = event.player;
      boolean client = player.level().isClientSide();
      CompoundTag tag = state.tag(player);
      if (!client && player.tickCount % 80 == 0) {
         tag.putInt("paral_time", tag.getInt("paral_time") - 1);
         state.save(player, tag);
      }

      if (tag.getInt("paral_time") > 0) {
         player.setYRot(tag.getFloat("paral_yrot"));
         player.setXRot(tag.getFloat("paral_xrot"));
         player.teleportTo(tag.getDouble("paral_xpos"), tag.getDouble("paral_ypos"), tag.getDouble("paral_zpos"));
      }

      if (!client && wait_situation(player)) {
         do_event(player, 14);
      }
   }
}
