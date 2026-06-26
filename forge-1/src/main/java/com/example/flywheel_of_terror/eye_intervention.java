package com.example.flywheel_of_terror;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderGuiEvent.Post;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class eye_intervention {
   public static List<position_and_size_at_screen> eye_states = new ArrayList<>();
   public static final int max_count_of_eyes = 15;
   public static List<Integer> eye_states2 = new ArrayList<>(15);
   public static final int eye_picture_width = 200;
   public static final int eye_picture_height = 100;
   public static int current_count_of_eyes = 0;
   public static int constant_tics_to_next_eye = 60;
   public static int tics_to_next_eye = 1;
   public static final String path_to_eye = "textures/eyes/eye";
   public static final String may_do_event_nbt = "eye_intervention";
   public static boolean event_in_process = false;
   public static float red_intense = 1.0F;
   public static boolean end_music = false;
   public static boolean sound_must_be = false;
   public static Random random = new Random();

   public static void do_event(Player player) {
      event_in_process = true;
      set_event_was(player, true);
      sound_must_be = true;
   }

   public static boolean get_event_was(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return tag.getBoolean("eye_intervention");
   }

   public static void set_event_was(Player player, boolean state) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putBoolean("eye_intervention", state);
      global_tag.put("flywheel_of_terror", tag);
   }

   @SubscribeEvent
   public static void every_entry(PlayerLoggedInEvent event) {
      eye_states.clear();
      eye_states2.clear();
      new ArrayList();
      position_and_size_at_screen state1 = new position_and_size_at_screen(47, 47, 6, 6);
      position_and_size_at_screen state2 = new position_and_size_at_screen(39, 52, 8, 8);
      position_and_size_at_screen state3 = new position_and_size_at_screen(70, 20, 4, 4);
      position_and_size_at_screen state4 = new position_and_size_at_screen(20, 80, 5, 5);
      position_and_size_at_screen state5 = new position_and_size_at_screen(35, 64, 12, 12);
      position_and_size_at_screen state6 = new position_and_size_at_screen(65, 73, 6, 6);
      position_and_size_at_screen state7 = new position_and_size_at_screen(10, 10, 7, 7);
      position_and_size_at_screen state8 = new position_and_size_at_screen(30, 15, 9, 9);
      position_and_size_at_screen state9 = new position_and_size_at_screen(40, 10, 7, 7);
      position_and_size_at_screen state10 = new position_and_size_at_screen(90, 40, 8, 8);
      position_and_size_at_screen state11 = new position_and_size_at_screen(50, 71, 5, 5);
      position_and_size_at_screen state12 = new position_and_size_at_screen(20, 48, 10, 10);
      position_and_size_at_screen state13 = new position_and_size_at_screen(70, 2, 11, 11);
      position_and_size_at_screen state14 = new position_and_size_at_screen(30, 0, 7, 7);
      position_and_size_at_screen state15 = new position_and_size_at_screen(90, 90, 9, 9);
      eye_states.add(state1);
      eye_states.add(state2);
      eye_states.add(state3);
      eye_states.add(state4);
      eye_states.add(state5);
      eye_states.add(state6);
      eye_states.add(state7);
      eye_states.add(state8);
      eye_states.add(state9);
      eye_states.add(state10);
      eye_states.add(state11);
      eye_states.add(state12);
      eye_states.add(state13);
      eye_states.add(state14);
      eye_states.add(state15);
      eye_states2.add(1);
      eye_states2.add(1);
      eye_states2.add(1);
      eye_states2.add(1);
      eye_states2.add(1);
      eye_states2.add(1);
      eye_states2.add(1);
      eye_states2.add(1);
      eye_states2.add(1);
      eye_states2.add(1);
      eye_states2.add(1);
      eye_states2.add(1);
      eye_states2.add(1);
      eye_states2.add(1);
      eye_states2.add(1);
   }

   @SubscribeEvent
   public static void add_eyes(PlayerTickEvent event) {
      Player player = event.player;
      boolean client = player.level().isClientSide();
      if (event_in_process && tics_to_next_eye <= 0) {
         tics_to_next_eye = constant_tics_to_next_eye;
         current_count_of_eyes++;
      }

      if (end_music) {
         information.do_a_silence(event.player);
         end_music = false;
      }

      if (client && sound_must_be) {
         sound_must_be = false;
         player.playSound((SoundEvent)register_sounds.some_eyes.get(), 1.0F, 1.0F);
      }
   }

   @SubscribeEvent
   public static void decrease(ServerTickEvent event) {
      tics_to_next_eye--;
   }

   @EventBusSubscriber(value = {Dist.CLIENT})
   public static class client_events {
      @SubscribeEvent
      public static void render_eyes(Post event) {
         int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
         int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();

         for (int count_of_eyes = 1; count_of_eyes <= current_count_of_eyes; count_of_eyes++) {
            if (current_count_of_eyes <= 15) {
               int required_id = count_of_eyes - 1;
               if (random.nextInt(1, 6) == 3) {
                  eye_states2.set(required_id, random.nextInt(1, 5));
               }

               int required_width_offset = eye_states.get(required_id).get_width_offset_by_screen_size(width);
               int required_height_offset = eye_states.get(required_id).get_height_offset_by_screen_size(height);
               int required_width_size = eye_states.get(required_id).get_width_size_by_screen_size(width);
               int required_height_size = eye_states.get(required_id).get_height_size_by_screen_size(height);
               event.getGuiGraphics()
                  .blit(
                     new ResourceLocation("flywheel_of_terror", "textures/eyes/eye" + eye_states2.get(required_id) + ".png"),
                     required_width_offset,
                     required_height_offset,
                     required_width_size,
                     required_height_size,
                     0.0F,
                     0.0F,
                     200,
                     100,
                     200,
                     100
                  );
               Minecraft.getInstance().setScreen(null);
            } else {
               event_in_process = false;
               current_count_of_eyes = 0;
               red_intense = 1.0F;
               end_music = true;
            }
         }
      }

      @SubscribeEvent
      public static void red(RenderLevelStageEvent event) {
         if (event_in_process) {
            RenderSystem.setShaderColor(red_intense, 1.0F, 1.0F, 1.0F);
            if (red_intense < 4.0F) {
               red_intense += 9.5E-4F;
            }
         }
      }
   }
}
