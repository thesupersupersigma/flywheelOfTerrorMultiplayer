package com.example.flywheel_of_terror;

import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   value = {Dist.CLIENT}
)
public class declinemusic {
   @SubscribeEvent
   public static void onSound(PlaySoundEvent event) {
      if (event.getSound() != null
         && event.getSound().getSource() == SoundSource.MUSIC
         && !event.getSound().getLocation().getPath().contains("shipwrecked")
         && !event.getSound().getLocation().getPath().contains("hunt")
         && !event.getSound().getLocation().getPath().contains("call")
         && !event.getSound().getLocation().getPath().contains("oh_no")
         && !event.getSound().getLocation().getPath().contains("eyes")) {
         event.setSound(null);
      }
   }
}
