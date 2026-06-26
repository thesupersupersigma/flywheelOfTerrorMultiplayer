package com.example.flywheel_of_terror;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent.Opening;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = {Dist.CLIENT})
public class deep_terror {
   @SubscribeEvent
   public static void open_pause(Opening event) {
      if (event.getScreen() instanceof PauseScreen) {
         event.setCanceled(true);
         Minecraft.getInstance().setScreen(new fake_exit(true));
      }

      if (event.getScreen() instanceof TitleScreen) {
         event.setCanceled(true);
         Minecraft.getInstance().setScreen(new fake_main_menu(true));
      }
   }
}
