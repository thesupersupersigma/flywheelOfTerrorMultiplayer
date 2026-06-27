package com.example.flywheel_of_terror;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent.Post;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class oh_no_between_screens {
   public static int current_frame = 1;
   public static int cycle = 1;
   public static int tics_of_event = 0;
   public static final String path_to_oh_no = "textures/oh_no_frames/frame";
   public static int count_of_frames = 37;
   public static int frame_width = 1908;
   public static int frame_height = 1080;

   public static void do_event(Player player) {
      set_event_was(player, true);
      Network.fx(player, Network.OHNO_FRAMES, 400);
   }

   public static void set_event_was(Player player, boolean state) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putBoolean(nbt_adresses.between_nbt, state);
      global_tag.put("flywheel_of_terror", tag);
   }

   public static boolean get_event_was(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return tag.getBoolean(nbt_adresses.between_nbt);
   }

   // Phase 3: the frame counter advances on the client (see client_net.clientTick), driven by the
   // OHNO_FRAMES FxPacket, so each client animates its own overlay independently.

   @EventBusSubscriber(value = {Dist.CLIENT})
   public static class client_events {
   @SubscribeEvent
   public static void render(Post event) {
      if (tics_of_event >= 1 && Minecraft.getInstance().screen == null) {
         int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
         int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
         position_and_size_at_screen place = new position_and_size_at_screen(20, 20, 80, 90);
         place.set_at_center_of_screen_by_size();
         place.set_height_offset(30);
         int required_width_offset = place.get_width_offset_by_screen_size(width);
         int required_height_offset = place.get_height_offset_by_screen_size(height);
         int required_width_size = place.get_width_size_by_screen_size(width);
         int required_height_size = place.get_height_size_by_screen_size(height);
         event.getGuiGraphics()
            .blit(
               new ResourceLocation("flywheel_of_terror", "textures/oh_no_frames/frame" + current_frame + ".png"),
               required_width_offset,
               required_height_offset,
               required_width_size,
               required_height_size,
               0.0F,
               0.0F,
               frame_width,
               frame_height,
               frame_width,
               frame_height
            );
      }
   }

   @SubscribeEvent
   public static void render2(net.minecraftforge.client.event.ScreenEvent.Render.Post event) {
      if (tics_of_event >= 1 && Minecraft.getInstance().screen != null) {
         int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
         int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
         position_and_size_at_screen place = new position_and_size_at_screen(20, 20, 80, 90);
         place.set_at_center_of_screen_by_size();
         place.set_height_offset(30);
         int required_width_offset = place.get_width_offset_by_screen_size(width);
         int required_height_offset = place.get_height_offset_by_screen_size(height);
         int required_width_size = place.get_width_size_by_screen_size(width);
         int required_height_size = place.get_height_size_by_screen_size(height);
         event.getGuiGraphics()
            .blit(
               new ResourceLocation("flywheel_of_terror", "textures/oh_no_frames/frame" + current_frame + ".png"),
               required_width_offset,
               required_height_offset,
               required_width_size,
               required_height_size,
               0.0F,
               0.0F,
               frame_width,
               frame_height,
               frame_width,
               frame_height
            );
         // The frame counter is also advanced once per client tick in client_net.clientTick. The
         // original advanced it a second time here while a GUI screen was open, so the overlay ran at
         // double speed (and finished sooner) over screens — restore that by advancing again here.
         tics_of_event--;
         if (cycle == 1) {
            current_frame++;
         } else {
            current_frame--;
         }

         if (current_frame == count_of_frames && cycle == 1) {
            cycle = 2;
         }

         if (current_frame == 1 && cycle == 2) {
            cycle = 1;
         }
      }
   }
   }
}
