package com.example.flywheel_of_terror;

import com.example.flywheel_of_terror.client.client_safe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class fake_darknet_access {
   public static int tics_to_error = 0;

   public static void set_wait_situation(Player player, boolean state) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putBoolean("wait_fake_darknet_access", state);
      global_tag.put("flywheel_of_terror", tag);
   }

   public static boolean get_wait_situation(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return tag.getBoolean("wait_fake_darknet_access");
   }

   public static void do_event(Player player) {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client_safe.darknetScreenshot());
      tics_to_error = 200;
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putInt("fake_darknet_access", 1);
   }

   @SubscribeEvent
   public static void every(PlayerTickEvent event) {
      Player player = event.player;
      boolean client = player.level().isClientSide();
      if (!client && event.phase == Phase.START) {
         tics_to_error--;
         System.out.println(tics_to_error);
      }

      if (tics_to_error == 1 && !client) {
         String text = "§caccess to http://flywheel_of_terror/" + information.get_name_of_the_player(player) + "/evidence.onion failed after 40 retries";
         player.sendSystemMessage(Component.literal(text));
         tics_to_error = -100;
      }
   }

   @SubscribeEvent
   public static void kill(LivingDeathEvent event) {
      if (event.getSource().getEntity() instanceof Player player && get_wait_situation(player)) {
         do_event(player);
         set_wait_situation(player, false);
      }
   }
}
