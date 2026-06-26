package com.example.flywheel_of_terror;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class terror_end {
   public static int phase = 0;

   public static boolean events_end(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return tag.getBoolean("angel_sound")
         && tag.getBoolean("paral")
         && tag.getBoolean("exist_terror")
         && tag.getBoolean("below")
         && tag.getBoolean("apoc")
         && he_is_here_event.get_event_was(player)
         && eye_intervention.get_event_was(player)
         && tag.getInt("tool_break") > 0
         && tag.getInt("sound_heart") > 0
         && tag.getInt("sound_knife_attack") > 0
         && tag.getInt("circle_christ") > 0
         && tag.getInt("fire_steps") > 0
         && tag.getInt("somewho") > 0
         && tag.getInt("periferia") > 0
         && tag.getInt("thunder_behind") > 1
         && tag.getInt("forge_revenge") > 0
         && tag.getInt("panic") > 0
         && tag.getInt("notice_in_inventory") > 0
         && tag.getInt("remove_entities") > 0
         && tag.getInt("oh_no_behind") > 0
         && tag.getInt("all_look_at_you") > 0
         && tag.getInt("oh_no_stalker_event") > 0
         && tag.getInt("family") > 0
         && tag.getInt("invisible") > 0
         && tag.getInt("fake_darknet_access") > 0
         && oh_no_between_screens.get_event_was(player);
   }

   @SubscribeEvent
   public static void endd(PlayerTickEvent event) {
      Player player = event.player;
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      boolean client = player.level().isClientSide();
      if (!client) {
         phase = tag.getInt(nbt_adresses.phase_nbt);
         if (player.tickCount % 40 == 0) {
            tag.putInt(nbt_adresses.seconds_to_die_oh_no, tag.getInt(nbt_adresses.seconds_to_die_oh_no) - 1);
            global_tag.put("flywheel_of_terror", tag);
         }

         if (tag.getInt(nbt_adresses.seconds_to_die_oh_no) == 1) {
            player.sendSystemMessage(Component.literal("oh_no was slain by consequences"));
            tag.putInt(nbt_adresses.phase_nbt, 1);
            tag.putInt(nbt_adresses.seconds_to_die_oh_no, -1);
            global_tag.put("flywheel_of_terror", tag);
         }

         if (phase == 0 && events_end(player) && tag.getInt(nbt_adresses.seconds_to_die_oh_no) < 0) {
            tag.putInt(nbt_adresses.seconds_to_die_oh_no, 300);
            global_tag.put("flywheel_of_terror", tag);
         }
      }
   }
}
