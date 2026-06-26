package com.example.flywheel_of_terror;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class exist_terror_event {
   public static Random random = new Random();
   public static boolean event_in_process = false;
   public static int tics_to_next_letter = 20;
   public static int next_letter = 0;
   public static String mytext = "Let me out, someone, please ";
   public static String context = "";

   @SubscribeEvent
   public static void zerodamage(LivingHurtEvent event) {
      if (event.getEntity() instanceof Player && event_in_process) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void writechar(PlayerTickEvent event) {
      Player player = event.player;
      if (player.level().isClientSide) {
         if (event_in_process) {
            CompoundTag global_tag = player.getPersistentData();
            CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
            tag.putBoolean("exist_terror", true);
            global_tag.put("flywheel_of_terror", tag);
            tics_to_next_letter--;
            if (tics_to_next_letter <= 0) {
               context = context + mytext.charAt(next_letter);
               Minecraft.getInstance().setScreen(new ChatScreen(context));
               tics_to_next_letter = random.nextInt(3, 15);
               next_letter++;
               System.out.println(context);
            }

            if (context.length() == mytext.length()) {
               event_in_process = false;
               Minecraft.getInstance().player.closeContainer();
               context = "";
               next_letter = 0;
               tics_to_next_letter = 10;
               player.sendSystemMessage(Component.literal("He has no permissions to anything."));
            }
         }
      }
   }

   @SubscribeEvent
   public static void declinebreak(ServerChatEvent event) {
      if (event_in_process) {
         event.setCanceled(true);
      }
   }
}
