package com.example.flywheel_of_terror;

import java.util.Random;
import com.example.flywheel_of_terror.client.client_safe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class exist_terror_event {
   // event_in_process → per-player NBT ("exist_terror_active"); the chat-typing fields stay static
   // (purely client-side A/V, Phase 3).
   public static Random random = new Random();
   public static int tics_to_next_letter = 20;
   public static int next_letter = 0;
   public static String mytext = "Let me out, someone, please ";
   public static String context = "";

   public static void set_active(Player player, boolean value) {
      state.putBool(player, "exist_terror_active", value);
   }

   @SubscribeEvent
   public static void zerodamage(LivingHurtEvent event) {
      if (event.getEntity() instanceof Player player && state.getBool(player, "exist_terror_active")) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void writechar(PlayerTickEvent event) {
      Player player = event.player;
      if (player.level().isClientSide) {
         if (state.getBool(player, "exist_terror_active")) {
            CompoundTag global_tag = player.getPersistentData();
            CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
            tag.putBoolean("exist_terror", true);
            global_tag.put("flywheel_of_terror", tag);
            tics_to_next_letter--;
            if (tics_to_next_letter <= 0) {
               context = context + mytext.charAt(next_letter);
               DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client_safe.existTerrorTypeChat(context));
               tics_to_next_letter = random.nextInt(3, 15);
               next_letter++;
               System.out.println(context);
            }

            if (context.length() == mytext.length()) {
               state.putBool(player, "exist_terror_active", false);
               DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client_safe.existTerrorClose());
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
      if (state.getBool(event.getPlayer(), "exist_terror_active")) {
         event.setCanceled(true);
      }
   }
}
