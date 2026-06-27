package com.example.flywheel_of_terror;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class something_wrong {
   // seconds_to_change → per-player NBT (server-authoritative). Phase 3: tics_of_silence (read by the
   // client PlaySoundEvent handler) is set + counted down on the client via the SILENCE FxPacket, so
   // the sound-muting window belongs to the one player whose terrain is being reshaped.
   public static Random random = new Random();
   public static final int min_time = 180;
   public static final int max_time = 360;
   public static int tics_of_silence = 0;

   @EventBusSubscriber(value = {Dist.CLIENT})
   public static class client_events {
      @SubscribeEvent
      public static void silence(PlaySoundEvent event) {
         if (event.getSound() != null && tics_of_silence > 0) {
            event.setSound(null);
         }
      }
   }

   public static int get_biggest(int x, int z) {
      return x > z ? x : z;
   }

   public static void change_block_to_new(Player player, int radius, Block block1, Block block2) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");

      for (double xx = player.getX() - (double)radius + 1.0; xx <= player.getX() + (double)radius - 1.0; xx++) {
         for (double yy = player.getY() - (double)radius + 1.0; yy <= player.getY() + (double)radius - 1.0; yy++) {
            for (double zz = player.getZ() - (double)radius + 1.0; zz <= player.getZ() + (double)radius - 1.0; zz++) {
               BlockPos pos = new BlockPos((int)xx, (int)yy, (int)zz);
               String coordinates_house = information.getCoordinates(xx, yy, zz) + "house";
               if (player.level().getBlockState(pos).getBlock() == block1 && !tag.getBoolean(coordinates_house)) {
                  player.level().setBlock(pos, block2.defaultBlockState(), 3);
               }
            }
         }
      }
   }

   public static void change_blocks_to_new(Player player, int radius, int height, List<Block> blocks, Block block2) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");

      for (double xx = player.getX() - (double)radius + 1.0; xx <= player.getX() + (double)radius - 1.0; xx++) {
         for (double yy = player.getY() - (double)height + 1.0; yy <= player.getY() + (double)height - 1.0; yy++) {
            for (double zz = player.getZ() - (double)radius + 1.0; zz <= player.getZ() + (double)radius - 1.0; zz++) {
               BlockPos pos = new BlockPos((int)xx, (int)yy, (int)zz);
               String coordinates_house = information.getCoordinates(xx, yy, zz) + "house";
               if (blocks.contains(player.level().getBlockState(pos).getBlock()) && !tag.getBoolean(coordinates_house)) {
                  player.level().setBlock(pos, block2.defaultBlockState(), 3);
               }
            }
         }
      }
   }

   public static void create_pit_behind(Player player, int distance, int radius, int deep) {
      double x = player.getX() - player.getLookAngle().x * (double)distance;
      double z = player.getZ() - player.getLookAngle().z * (double)distance;
      int y = player.level().getHeight(Types.WORLD_SURFACE, (int)x, (int)z);
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");

      for (double xx = x - (double)radius + 1.0; xx <= x + (double)radius - 1.0; xx++) {
         for (double yy = (double)(y - deep + 1); yy <= (double)(y + deep - 1); yy++) {
            for (double zz = z - (double)radius + 1.0; zz <= z + (double)radius - 1.0; zz++) {
               BlockPos pos = new BlockPos((int)xx, (int)yy, (int)zz);
               String coordinates_house = information.getCoordinates(xx, yy, zz) + "house";
               if (!tag.getBoolean(coordinates_house)) {
                  player.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
               }
            }
         }
      }

      change_block_to_new(player, 10, Blocks.DIRT, Blocks.GRASS_BLOCK);
   }

   public static void remove_layer_behind(Player player, int distance, int area) {
      double x = player.getX() - player.getLookAngle().x * (double)distance;
      double z = player.getZ() - player.getLookAngle().z * (double)distance;
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");

      for (double xx = x - (double)area + 1.0; xx <= x + (double)area - 1.0; xx++) {
         for (double zz = z - (double)area + 1.0; zz <= z + (double)area - 1.0; zz++) {
            int yy = player.level().getHeight(Types.WORLD_SURFACE, (int)xx, (int)zz) - 1;
            BlockPos pos = new BlockPos((int)xx, yy, (int)zz);
            String coordinates_house = information.getCoordinates(xx, (double)yy, zz) + "house";
            if (!tag.getBoolean(coordinates_house)) {
               player.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
         }
      }

      change_block_to_new(player, 10, Blocks.DIRT, Blocks.GRASS_BLOCK);
   }

   public static void create_hill_behind(Player player, int distance, int height, int radius_x, int radius_z) {
      double x = player.getX() - player.getLookAngle().x * (double)distance;
      double z = player.getZ() - player.getLookAngle().z * (double)distance;

      for (int i = 0; i < height; i++) {
         int new_radius_x = radius_x - i;
         int new_radius_z = radius_z - i;

         for (double xx = x - (double)new_radius_x + 1.0; xx <= x + (double)new_radius_x - 1.0; xx++) {
            for (double yy = player.getY() + (double)i; yy <= player.getY() + (double)i; yy++) {
               for (double zz = z - (double)new_radius_z + 1.0; zz <= z + (double)new_radius_z - 1.0; zz++) {
                  int yyy = player.level().getHeight(Types.MOTION_BLOCKING, (int)xx, (int)zz);
                  BlockPos pos = new BlockPos((int)xx, yyy, (int)zz);
                  player.level().setBlock(pos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                  if ((
                        xx == x - (double)new_radius_x + 1.0
                           || xx == x + (double)new_radius_x - 1.0
                           || zz == z - (double)new_radius_z + 1.0
                           || zz == z + (double)new_radius_z - 1.0
                     )
                     && random.nextInt(1, 10) == 2) {
                     player.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                  }
               }
            }
         }

         change_block_to_new(player, 20, Blocks.DIRT, Blocks.GRASS_BLOCK);
      }
   }

   @SubscribeEvent
   public static void change_surface(PlayerTickEvent event) {
      Player player = event.player;
      boolean client = player.level().isClientSide();
      boolean server = !player.level().isClientSide();
      if (server) {
         if (player.tickCount % 40 == 0) {
            state.putInt(player, "seconds_to_change", state.getInt(player, "seconds_to_change") - 1);
         }

         double xrot = (double)player.getXRot();
         if (state.getInt(player, "seconds_to_change") <= 0 && terror_beginning.far_away_house(player) && xrot > -10.0 && xrot < 10.0) {
            Network.fx(player, Network.SILENCE, 100);
            state.putInt(player, "seconds_to_change", random.nextInt(180, 360));
            int i = random.nextInt(1, 3);
            switch (i) {
               case 1:
                  create_pit_behind(player, 5, 2, 2);
                  break;
               case 2:
                  remove_layer_behind(player, 20, 9);
                  break;
               case 3:
                  int x = random.nextInt(4, 9);
                  int z = random.nextInt(4, 9);
                  int y = get_biggest(x, z) + 2;
                  create_hill_behind(player, y, 2, x, z);
            }
         }

         List<Block> delete = new ArrayList<>();
         delete.add(Blocks.TALL_GRASS);
         delete.add(Blocks.GRASS);
         delete.add(Blocks.FLOWER_POT);
         delete.add(Blocks.SUNFLOWER);
         delete.add(Blocks.FERN);
         if (player.tickCount % 10 == 0) {
            change_blocks_to_new(player, 10, 5, delete, Blocks.AIR);
         }
      }
   }
}
