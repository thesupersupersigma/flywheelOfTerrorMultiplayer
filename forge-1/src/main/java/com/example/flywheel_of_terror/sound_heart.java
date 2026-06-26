package com.example.flywheel_of_terror;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class sound_heart {
   public static boolean event_in_process = false;
   public static int to_reload_event = 0;

   @SubscribeEvent
   public static void everytime(PlayerTickEvent event) {
      Player player = event.player;
      if (player.tickCount % 80 == 0) {
         to_reload_event--;
      }

      if (event_in_process && player.level().isClientSide() && to_reload_event <= 20) {
         to_reload_event = 20;
         System.out.println("heart attack");
         event.player.playSound((SoundEvent)register_sounds.heart_attack.get(), 8.0F, 1.0F);
         event_in_process = false;
      }
   }
}
