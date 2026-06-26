package com.example.flywheel_of_terror;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class advanced_baron_detector {
   public static Random random = new Random();
   public static int tics_to_destroy_tower = -2;

   public static int get_delay_to_destroy_tower() {
      return 4000;
   }

   public static boolean may_destroy_tower(Player player) {
      return tics_to_destroy_tower <= 0 && player.onGround() && player.getY() > 55.0;
   }

   public static boolean player_is_baron(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return tag.getBoolean("baron");
   }

   public static int get_count_of_layers_under_player(Player player, int deep_of_scan, int radius_of_tower) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      double x = player.getX();
      double y = player.getY();
      double z = player.getZ();
      int count_of_layers = 0;

      for (double yy = y - (double)deep_of_scan; yy < y; yy++) {
         boolean block_finded = false;

         for (double xx = x - (double)radius_of_tower; xx <= x + (double)radius_of_tower; xx++) {
            for (double zz = z - (double)radius_of_tower; zz <= z + (double)radius_of_tower; zz++) {
               String coordinates = (int)xx + "," + (int)yy + "," + (int)zz;
               if (tag.getBoolean(coordinates) && !block_finded) {
                  count_of_layers++;
                  block_finded = true;
               }
            }
         }
      }

      return count_of_layers;
   }

   public static boolean baron_on_tower(Player player) {
      return get_count_of_layers_under_player(player, 5, 5) == 5;
   }

   public static void destroy_baron_tower(Player player, int number_of_punishment) {
      tics_to_destroy_tower = get_delay_to_destroy_tower();
      switch (number_of_punishment) {
         case 1:
            for (double x = player.getX() - 2.0; x <= player.getX() + 2.0; x++) {
               for (double y = player.getY() + 30.0; y <= player.getY() + 30.0; y++) {
                  for (double z = player.getZ() - 2.0; z <= player.getZ() + 2.0; z++) {
                     BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                     player.level().setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
                  }
               }
            }
            break;
         case 2:
            for (BlockPos pos : information.get_poses_of_blocks_placed_by_player(player, 5, 12, 5, false)) {
               player.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
            break;
         case 3:
            information.thunder_player(player);
      }

      player.sendSystemMessage(Component.literal("this place is not for you"));
   }

   @SubscribeEvent
   public static void baron_activity(PlayerTickEvent event) {
      Player player = event.player;
      Level level = player.level();
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      if (player.level().isClientSide()) {
         tics_to_destroy_tower--;
      } else {
         if (information.get_name_of_the_player(player).contains("baron")) {
            tag.putBoolean("baron", true);
         }

         if (player_is_baron(player) && baron_on_tower(player) && may_destroy_tower(player)) {
            destroy_baron_tower(player, random.nextInt(1, 4));
         }
      }
   }
}
