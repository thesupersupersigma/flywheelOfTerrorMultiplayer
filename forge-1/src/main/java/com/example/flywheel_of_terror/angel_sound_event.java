package com.example.flywheel_of_terror;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class angel_sound_event {
   public static void set_active(Player player, boolean value) {
      state.putBool(player, "angel_sound_active", value);
   }

   @SubscribeEvent
   public static void do_event(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.level().isClientSide()) {
         if (state.getBool(player, "angel_sound_active")) {
            CompoundTag global_tag = player.getPersistentData();
            CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
            tag.putBoolean("angel_sound", true);
            global_tag.put("flywheel_of_terror", tag);
            player.level()
               .playSound(
                  null, player.getX(), player.getY(), player.getZ(), (SoundEvent)register_sounds.angel_sound.get(), SoundSource.PLAYERS, 1.0F, 1.0F
               );
            state.putBool(player, "angel_sound_active", false);
         }
      }
   }
}
